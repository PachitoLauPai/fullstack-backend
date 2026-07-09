 package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.*;
import com.visitas.backend_api.entity.VisitaInopinadaEntity;
import com.visitas.backend_api.enums.EstadoRequerimiento;
import com.visitas.backend_api.enums.EstadoVisita;
import com.visitas.backend_api.repository.RequerimientoVisitaEntityRepository;
import com.visitas.backend_api.repository.VisitaInopinadaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final VisitaInopinadaEntityRepository visitaRepository;
    private final RequerimientoVisitaEntityRepository requerimientoRepository;

    public ReporteDataDTO obtenerReporteCompleto(String periodo) {
        ReporteDataDTO reporte = new ReporteDataDTO();
        reporte.setEstadisticas(obtenerEstadisticas(periodo));
        reporte.setVisitasPorSede(obtenerVisitasPorSede());
        reporte.setTopDocentes(obtenerTopDocentes());
        reporte.setRequerimientosPendientes(obtenerRequerimientosPendientes());
        // CumplimientoPorArea y EvolucionCumplimiento eliminados - requieren lógica compleja de evaluaciones
        return reporte;
    }

    public ReportesStatsDTO obtenerEstadisticas(String periodo) {
        ReportesStatsDTO stats = new ReportesStatsDTO();
        
        periodo = normalizarPeriodo(periodo);
        LocalDate hoy = LocalDate.now();
        LocalDate inicioPeriodo;
        LocalDate inicioPeriodoAnterior;
        
        switch (periodo.toLowerCase()) {
            case "mes":
                inicioPeriodo = hoy.withDayOfMonth(1);
                inicioPeriodoAnterior = inicioPeriodo.minusMonths(1);
                break;
            case "año":
            case "ano":
                inicioPeriodo = hoy.withDayOfYear(1);
                inicioPeriodoAnterior = inicioPeriodo.minusYears(1);
                break;
            case "semestre":
            default:
                // Semestre (6 meses)
                if (hoy.getMonthValue() <= 6) {
                    inicioPeriodo = LocalDate.of(hoy.getYear(), 1, 1);
                    inicioPeriodoAnterior = LocalDate.of(hoy.getYear() - 1, 7, 1);
                } else {
                    inicioPeriodo = LocalDate.of(hoy.getYear(), 7, 1);
                    inicioPeriodoAnterior = LocalDate.of(hoy.getYear(), 1, 1);
                }
                break;
        }

        final LocalDate inicio = inicioPeriodo;
        final LocalDate inicioAnterior = inicioPeriodoAnterior;
        final LocalDate finAnterior = inicio.minusDays(1);

        // Total visitas en el período
        List<VisitaInopinadaEntity> visitasPeriodo = visitaRepository.findAll().stream()
                .filter(v -> !v.getFechaVisita().isBefore(inicio))
                .collect(Collectors.toList());
        stats.setTotalVisitas(visitasPeriodo.size());

        // Visitas período anterior
        List<VisitaInopinadaEntity> visitasAnterior = visitaRepository.findAll().stream()
                .filter(v -> !v.getFechaVisita().isBefore(inicioAnterior) && !v.getFechaVisita().isAfter(finAnterior))
                .collect(Collectors.toList());

        if (visitasAnterior.isEmpty()) {
            stats.setTotalVisitasCrecimiento(0.0);
        } else {
            double crecimiento = ((double) (visitasPeriodo.size() - visitasAnterior.size()) / visitasAnterior.size()) * 100;
            stats.setTotalVisitasCrecimiento(Math.round(crecimiento * 100.0) / 100.0);
        }

        // Cumplimiento
        long visitasCompletadas = visitasPeriodo.stream()
                .filter(v -> v.getEstadoVisita() == EstadoVisita.COMPLETADA)
                .count();
        
        if (visitasPeriodo.isEmpty()) {
            stats.setCumplimiento(0.0);
        } else {
            double cumplimiento = ((double) visitasCompletadas / visitasPeriodo.size()) * 100;
            stats.setCumplimiento(Math.round(cumplimiento * 100.0) / 100.0);
        }
        stats.setCumplimientoCrecimiento(5.0); // Simulado

        // Docentes visitados
        long docentesVisitados = visitasPeriodo.stream()
                .map(v -> v.getDocente().getId())
                .distinct()
                .count();
        stats.setDocentesVisitados((int) docentesVisitados);
        stats.setTotalDocentes(48); // Total de docentes en BD

        // Sedes activas
        long sedesActivas = visitasPeriodo.stream()
                .filter(v -> v.getSede() != null)
                .map(v -> v.getSede().getId())
                .distinct()
                .count();
        stats.setSedesActivas((int) sedesActivas);
        stats.setSedesDescripcion("con visitas registradas");

        return stats;
    }

    public List<VisitasPorSedeDTO> obtenerVisitasPorSede() {
        List<VisitaInopinadaEntity> todasLasVisitas = visitaRepository.findAll();
        
        Map<String, Long> visitasPorSede = todasLasVisitas.stream()
                .filter(v -> v.getSede() != null)
                .collect(Collectors.groupingBy(
                        v -> v.getSede().getNombre(),
                        Collectors.counting()
                ));
        
        long totalVisitas = visitasPorSede.values().stream().mapToLong(Long::longValue).sum();
        
        List<VisitasPorSedeDTO> resultado = new ArrayList<>();
        visitasPorSede.forEach((sede, cantidad) -> {
            VisitasPorSedeDTO dto = new VisitasPorSedeDTO();
            dto.setSede(sede);
            dto.setCantidad(cantidad.intValue());
            dto.setPorcentaje(totalVisitas > 0 ? Math.round((cantidad * 100.0 / totalVisitas) * 100.0) / 100.0 : 0.0);
            resultado.add(dto);
        });
        
        return resultado;
    }

    public List<TopDocenteDTO> obtenerTopDocentes() {
        List<TopDocenteDTO> resultado = new ArrayList<>();
        
        List<VisitaInopinadaEntity> todasLasVisitas = visitaRepository.findAll();
        
        // Agrupar por docente y calcular cumplimiento
        Map<Integer, List<VisitaInopinadaEntity>> visitasPorDocente = todasLasVisitas.stream()
                .collect(Collectors.groupingBy(v -> v.getDocente().getId()));
        
        List<TopDocenteDTO> docentes = new ArrayList<>();
        visitasPorDocente.forEach((docenteId, visitas) -> {
            long completadas = visitas.stream()
                    .filter(v -> v.getEstadoVisita() == EstadoVisita.COMPLETADA)
                    .count();
            double cumplimiento = visitas.isEmpty() ? 0.0 : (completadas * 100.0 / visitas.size());
            
            TopDocenteDTO dto = new TopDocenteDTO();
            dto.setNombre(visitas.get(0).getDocente().getNombres() + " " + visitas.get(0).getDocente().getApellidos());
            dto.setTotalVisitas(visitas.size());
            dto.setCumplimiento(Math.round(cumplimiento * 100.0) / 100.0);
            docentes.add(dto);
        });
        
        // Ordenar por cumplimiento descendente y tomar top 5
        docentes.sort(Comparator.comparing(TopDocenteDTO::getCumplimiento).reversed());
        
        int ranking = 1;
        for (TopDocenteDTO dto : docentes.stream().limit(5).collect(Collectors.toList())) {
            dto.setRanking(ranking++);
            resultado.add(dto);
        }
        
        return resultado;
    }

    public List<RequerimientoPendienteDTO> obtenerRequerimientosPendientes() {
        return requerimientoRepository.findAll().stream()
                .filter(r -> r.getEstado() == EstadoRequerimiento.PENDIENTE)
                .sorted((r1, r2) -> r1.getFechaSolicitud().compareTo(r2.getFechaSolicitud()))
                .limit(10)
                .map(r -> {
                    RequerimientoPendienteDTO dto = new RequerimientoPendienteDTO();
                    dto.setId(r.getId());
                    dto.setDescripcion(r.getDescripcion());
                    dto.setDocente(r.getVisita().getDocente().getNombres() + " " + r.getVisita().getDocente().getApellidos());
                    dto.setFecha(r.getFechaSolicitud());
                    dto.setTipo("normal"); // Puedes agregar lógica para determinar urgencia
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public byte[] exportarExcel(String periodo) {
        ReporteDataDTO reporte = obtenerReporteCompleto(periodo);
        
        try (Workbook workbook = new XSSFWorkbook()) {
            // Hoja 1: Resumen
            Sheet resumenSheet = workbook.createSheet("Resumen");
            crearFilaEncabezado(resumenSheet, new String[]{"Métrica", "Valor"});
            crearFilaDatos(resumenSheet, 1, new String[]{"Total Visitas", reporte.getEstadisticas().getTotalVisitas().toString()});
            crearFilaDatos(resumenSheet, 2, new String[]{"Cumplimiento (%)", reporte.getEstadisticas().getCumplimiento().toString()});
            crearFilaDatos(resumenSheet, 3, new String[]{"Docentes Visitados", reporte.getEstadisticas().getDocentesVisitados().toString()});
            crearFilaDatos(resumenSheet, 4, new String[]{"Sedes Activas", reporte.getEstadisticas().getSedesActivas().toString()});

            // Hoja 2: Cumplimiento por Área
            Sheet areaSheet = workbook.createSheet("Cumplimiento por Área");
            crearFilaEncabezado(areaSheet, new String[]{"Área", "Porcentaje"});
            int rowNum = 1;
            for (CumplimientoAreaDTO area : reporte.getCumplimientoPorArea()) {
                crearFilaDatos(areaSheet, rowNum++, new String[]{area.getArea(), area.getPorcentajeCumplimiento().toString()});
            }

            // Hoja 3: Visitas por Sede
            Sheet sedeSheet = workbook.createSheet("Visitas por Sede");
            crearFilaEncabezado(sedeSheet, new String[]{"Sede", "Cantidad", "Porcentaje"});
            rowNum = 1;
            for (VisitasPorSedeDTO sede : reporte.getVisitasPorSede()) {
                crearFilaDatos(sedeSheet, rowNum++, new String[]{sede.getSede(), sede.getCantidad().toString(), sede.getPorcentaje().toString()});
            }

            // Hoja 4: Top Docentes
            Sheet docenteSheet = workbook.createSheet("Top Docentes");
            crearFilaEncabezado(docenteSheet, new String[]{"Ranking", "Nombre", "Visitas", "Cumplimiento"});
            rowNum = 1;
            for (TopDocenteDTO docente : reporte.getTopDocentes()) {
                crearFilaDatos(docenteSheet, rowNum++, new String[]{
                    docente.getRanking().toString(),
                    docente.getNombre(),
                    docente.getTotalVisitas().toString(),
                    docente.getCumplimiento().toString()
                });
            }

            // Hoja 5: Requerimientos Pendientes
            Sheet reqSheet = workbook.createSheet("Requerimientos Pendientes");
            crearFilaEncabezado(reqSheet, new String[]{"ID", "Descripción", "Docente", "Fecha", "Tipo"});
            rowNum = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            for (RequerimientoPendienteDTO req : reporte.getRequerimientosPendientes()) {
                crearFilaDatos(reqSheet, rowNum++, new String[]{
                    req.getId().toString(),
                    req.getDescripcion(),
                    req.getDocente(),
                    req.getFecha().format(formatter),
                    req.getTipo()
                });
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar Excel", e);
        }
    }

    public byte[] exportarPdf(String periodo) {
        ReporteDataDTO reporte = obtenerReporteCompleto(periodo);

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                PDFont headingFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
                PDFont bodyFont = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

                contentStream.beginText();
                contentStream.setFont(headingFont, 16);
                contentStream.newLineAtOffset(50, 730);
                contentStream.showText("Reporte de Visitas - " + periodo);
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(bodyFont, 12);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Total Visitas: " + reporte.getEstadisticas().getTotalVisitas());
                contentStream.newLineAtOffset(0, -18);
                contentStream.showText("Cumplimiento: " + reporte.getEstadisticas().getCumplimiento() + "%");
                contentStream.newLineAtOffset(0, -18);
                contentStream.showText("Docentes Visitados: " + reporte.getEstadisticas().getDocentesVisitados());
                contentStream.newLineAtOffset(0, -18);
                contentStream.showText("Sedes Activas: " + reporte.getEstadisticas().getSedesActivas());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(headingFont, 14);
                contentStream.newLineAtOffset(50, 620);
                contentStream.showText("Top Docentes");
                contentStream.endText();

                float yPosition = 600;
                contentStream.beginText();
                contentStream.setFont(bodyFont, 12);
                contentStream.newLineAtOffset(50, yPosition);
                for (TopDocenteDTO docente : reporte.getTopDocentes()) {
                    contentStream.showText(docente.getRanking() + ". " + docente.getNombre() + " - " + docente.getCumplimiento() + "%");
                    contentStream.newLineAtOffset(0, -16);
                }
                contentStream.endText();
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    private String normalizarPeriodo(String periodo) {
        if (periodo == null) {
            return "semestre";
        }

        switch (periodo.toLowerCase()) {
            case "month":
            case "mes":
                return "mes";
            case "year":
            case "año":
            case "ano":
                return "año";
            case "semester":
            case "semestre":
            default:
                return "semestre";
        }
    }

    private void crearFilaEncabezado(Sheet sheet, String[] headers) {
        Row row = sheet.createRow(0);
        CellStyle style = sheet.getWorkbook().createCellStyle();
        Font font = sheet.getWorkbook().createFont();
        font.setBold(true);
        style.setFont(font);
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void crearFilaDatos(Sheet sheet, int rowNum, String[] datos) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < datos.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(datos[i]);
        }
    }
}
