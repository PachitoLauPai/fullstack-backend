package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.*;
import com.visitas.backend_api.entity.RequerimientoVisitaEntity;
import com.visitas.backend_api.entity.VisitaInopinadaEntity;
import com.visitas.backend_api.enums.EstadoRequerimiento;
import com.visitas.backend_api.enums.EstadoVisita;
import com.visitas.backend_api.repository.RequerimientoVisitaEntityRepository;
import com.visitas.backend_api.repository.VisitaInopinadaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final VisitaInopinadaEntityRepository visitaRepository;
    private final RequerimientoVisitaEntityRepository requerimientoRepository;

    public DashboardDataDTO obtenerDashboardData() {
        DashboardDataDTO dashboard = new DashboardDataDTO();
        dashboard.setEstadisticas(obtenerEstadisticas());
        dashboard.setVisitasPorSemana(obtenerVisitasPorSemana());
        dashboard.setProximasVisitas(obtenerProximasVisitas());
        dashboard.setVisitasRecientes(obtenerVisitasRecientes());
        return dashboard;
    }

    public DashboardStatsDTO obtenerEstadisticas() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        LocalDate hoy = LocalDate.now();
        LocalDate inicioMesActual = hoy.withDayOfMonth(1);
        LocalDate inicioMesAnterior = inicioMesActual.minusMonths(1);
        LocalDate finMesAnterior = inicioMesActual.minusDays(1);

        // Visitas este mes
        List<VisitaInopinadaEntity> visitasEsteMes = visitaRepository.findAll().stream()
                .filter(v -> !v.getFechaVisita().isBefore(inicioMesActual))
                .collect(Collectors.toList());
        stats.setVisitasEsteMes(visitasEsteMes.size());

        // Visitas mes anterior
        List<VisitaInopinadaEntity> visitasMesAnterior = visitaRepository.findAll().stream()
                .filter(v -> !v.getFechaVisita().isBefore(inicioMesAnterior)
                        && !v.getFechaVisita().isAfter(finMesAnterior))
                .collect(Collectors.toList());

        // Crecimiento visitas
        if (visitasMesAnterior.isEmpty()) {
            stats.setVisitasCrecimiento(0.0);
        } else {
            double crecimiento = ((double) (visitasEsteMes.size() - visitasMesAnterior.size()) / visitasMesAnterior.size()) * 100;
            stats.setVisitasCrecimiento(Math.round(crecimiento * 100.0) / 100.0);
        }

        // Docentes evaluados (únicos en visitas del mes)
        long docentesEvaluados = visitasEsteMes.stream()
                .map(v -> v.getDocente().getId())
                .distinct()
                .count();
        stats.setDocentesEvaluados((int) docentesEvaluados);

        // Docentes evaluados mes anterior
        long docentesMesAnterior = visitasMesAnterior.stream()
                .map(v -> v.getDocente().getId())
                .distinct()
                .count();

        if (docentesMesAnterior == 0) {
            stats.setDocentesCrecimiento(0.0);
        } else {
            double crecimientoDocentes = ((double) (docentesEvaluados - docentesMesAnterior) / docentesMesAnterior) * 100;
            stats.setDocentesCrecimiento(Math.round(crecimientoDocentes * 100.0) / 100.0);
        }

        // Cumplimiento general (visitas completadas / total)
        List<VisitaInopinadaEntity> todasLasVisitas = visitaRepository.findAll();
        long visitasCompletadas = todasLasVisitas.stream()
                .filter(v -> v.getEstadoVisita() == EstadoVisita.COMPLETADA)
                .count();

        if (todasLasVisitas.isEmpty()) {
            stats.setCumplimientoGeneral(0.0);
        } else {
            double cumplimiento = ((double) visitasCompletadas / todasLasVisitas.size()) * 100;
            stats.setCumplimientoGeneral(Math.round(cumplimiento * 100.0) / 100.0);
        }
        stats.setCumplimientoPromedio(5.0); // Valor simulado

        // Requerimientos pendientes
        List<RequerimientoVisitaEntity> requerimientosPendientes = requerimientoRepository.findAll().stream()
                .filter(r -> r.getEstado() == EstadoRequerimiento.PENDIENTE)
                .collect(Collectors.toList());
        stats.setRequerimientosPendientes(requerimientosPendientes.size());
        stats.setRequerimientosPorAtender(requerimientosPendientes.size() > 0 ? requerimientosPendientes.size() : 0);

        return stats;
    }

    public List<VisitaSemanaDTO> obtenerVisitasPorSemana() {
        List<VisitaSemanaDTO> resultado = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // Obtener las últimas 8 semanas
        for (int i = 7; i >= 0; i--) {
            LocalDate semanaInicio = hoy.minusWeeks(i).with(weekFields.dayOfWeek(), 1);
            LocalDate semanaFin = semanaInicio.plusDays(6);

            final LocalDate inicio = semanaInicio;
            final LocalDate fin = semanaFin;

            long cantidad = visitaRepository.findAll().stream()
                    .filter(v -> !v.getFechaVisita().isBefore(inicio) && !v.getFechaVisita().isAfter(fin))
                    .count();

            VisitaSemanaDTO dto = new VisitaSemanaDTO();
            dto.setSemana("Sem " + (8 - i));
            dto.setCantidadVisitas((int) cantidad);
            resultado.add(dto);
        }

        return resultado;
    }

    public List<ProximaVisitaDTO> obtenerProximasVisitas() {
        LocalDate hoy = LocalDate.now();
        LocalDate finDeSemana = hoy.plusDays(7);

        return visitaRepository.findAll().stream()
                .filter(v -> !v.getFechaVisita().isBefore(hoy) && !v.getFechaVisita().isAfter(finDeSemana))
                .filter(v -> v.getEstadoVisita() != EstadoVisita.COMPLETADA)
                .sorted((v1, v2) -> v1.getFechaVisita().compareTo(v2.getFechaVisita()))
                .limit(5)
                .map(v -> {
                    ProximaVisitaDTO dto = new ProximaVisitaDTO();
                    dto.setId(v.getId());
                    dto.setDocente(v.getDocente().getNombres() + " " + v.getDocente().getApellidos());
                    dto.setAsignatura(v.getAsignatura().getNombre());
                    dto.setFecha(v.getFechaVisita());
                    dto.setHora(v.getHoraInicio());
                    dto.setSede(v.getSede() != null ? v.getSede().getNombre() : "Sede no especificada");

                    // Formatear horario
                    String dia;
                    if (v.getFechaVisita().isEqual(hoy)) {
                        dia = "Hoy";
                    } else if (v.getFechaVisita().isEqual(hoy.plusDays(1))) {
                        dia = "Mañana";
                    } else {
                        dia = v.getFechaVisita().getDayOfWeek().toString().substring(0, 1)
                                + v.getFechaVisita().getDayOfWeek().toString().substring(1).toLowerCase();
                    }
                    dto.setHorarioFormateado(dia + " " + v.getHoraInicio().toString().substring(0, 5));

                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<VisitaRecienteDTO> obtenerVisitasRecientes() {
        return visitaRepository.findAll().stream()
                .sorted((v1, v2) -> v2.getFechaVisita().compareTo(v1.getFechaVisita()))
                .limit(10)
                .map(v -> {
                    VisitaRecienteDTO dto = new VisitaRecienteDTO();
                    dto.setId("VIS-" + String.format("%03d", v.getId()));
                    dto.setDocente(v.getDocente().getNombres() + " " + v.getDocente().getApellidos());
                    dto.setAsignatura(v.getAsignatura().getNombre());
                    dto.setSede(v.getSede() != null ? v.getSede().getNombre() : "Sede no especificada");
                    dto.setFecha(v.getFechaVisita());
                    dto.setHora(v.getHoraInicio());
                    dto.setEstado(v.getEstadoVisita());

                    // Mapear estado a texto legible
                    switch (v.getEstadoVisita()) {
                        case COMPLETADA:
                            dto.setEstadoFormateado("Cumple");
                            break;
                        case FIRMADA_DOCENTE:
                            dto.setEstadoFormateado("Parcial");
                            break;
                        case BORRADOR:
                            dto.setEstadoFormateado("No Cumple");
                            break;
                        default:
                            dto.setEstadoFormateado("En Proceso");
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}
