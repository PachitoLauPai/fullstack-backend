package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.RequerimientoCreateDTO;
import com.visitas.backend_api.dto.RequerimientoUpdateDTO;
import com.visitas.backend_api.dto.RequerimientoVisitaDTO;
import com.visitas.backend_api.entity.RequerimientoVisitaEntity;
import com.visitas.backend_api.entity.VisitaInopinadaEntity;
import com.visitas.backend_api.enums.EstadoRequerimiento;
import com.visitas.backend_api.exception.ResourceNotFoundException;
import com.visitas.backend_api.exception.UnauthorizedAccessException;
import com.visitas.backend_api.repository.RequerimientoVisitaEntityRepository;
import com.visitas.backend_api.repository.VisitaInopinadaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequerimientoVisitaService {

    private final RequerimientoVisitaEntityRepository requerimientoRepository;
    private final VisitaInopinadaEntityRepository visitaRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public List<RequerimientoVisitaDTO> listarPorVisita(Integer idVisita) {
        return requerimientoRepository.findByVisitaId(idVisita).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RequerimientoVisitaDTO> listarTodos() {
        return requerimientoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public RequerimientoVisitaDTO obtenerPorId(Integer id) {
        RequerimientoVisitaEntity requerimiento = requerimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requerimiento", id));
        return toDTO(requerimiento);
    }

    @Transactional
    public RequerimientoVisitaDTO crear(RequerimientoCreateDTO dto) {
        VisitaInopinadaEntity visita = visitaRepository.findById(dto.getIdVisita())
                .orElseThrow(() -> new ResourceNotFoundException("Visita", dto.getIdVisita()));

        RequerimientoVisitaEntity requerimiento = new RequerimientoVisitaEntity();
        requerimiento.setVisita(visita);
        requerimiento.setDescripcion(dto.getDescripcion());
        requerimiento.setEstado(EstadoRequerimiento.PENDIENTE);
        requerimiento.setFechaSolicitud(LocalDate.now());

        requerimiento = requerimientoRepository.save(requerimiento);
        return toDTO(requerimiento);
    }

    @Transactional
    public RequerimientoVisitaDTO responder(Integer id, RequerimientoUpdateDTO dto) {
        RequerimientoVisitaEntity requerimiento = requerimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requerimiento", id));

        requerimiento.setRespuesta(dto.getRespuesta());
        if (dto.getEstado() != null) {
            requerimiento.setEstado(dto.getEstado());
        }
        requerimiento.setFechaRespuesta(dto.getFechaRespuesta() != null ? dto.getFechaRespuesta() : LocalDate.now());

        requerimiento = requerimientoRepository.save(requerimiento);
        return toDTO(requerimiento);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!requerimientoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Requerimiento", id);
        }
        requerimientoRepository.deleteById(id);
    }

    // Nuevos métodos para flujo de requerimientos
    
    @Transactional(readOnly = true)
    public List<RequerimientoVisitaDTO> listarMisRequerimientosComoDocente() {
        Integer currentDocenteId = authService.getCurrentDocenteId();
        return requerimientoRepository.findByVisitaDocenteId(currentDocenteId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<RequerimientoVisitaDTO> listarRequerimientosDeMisVisitas() {
        Integer currentAuditorId = authService.getCurrentUserId();
        return requerimientoRepository.findByVisitaUsuarioAuditorId(currentAuditorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public RequerimientoVisitaDTO atenderRequerimientoComoDocente(Integer id, String respuesta) {
        Integer currentDocenteId = authService.getCurrentDocenteId();
        
        RequerimientoVisitaEntity requerimiento = requerimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Requerimiento", id));
        
        // Verificar que el requerimiento pertenece a una visita del docente actual
        if (!requerimiento.getVisita().getDocente().getId().equals(currentDocenteId)) {
            throw new UnauthorizedAccessException("No puedes atender requerimientos de otras visitas");
        }
        
        requerimiento.setRespuesta(respuesta);
        requerimiento.setEstado(EstadoRequerimiento.ATENDIDO);
        requerimiento.setFechaRespuesta(LocalDate.now());
        
        requerimiento = requerimientoRepository.save(requerimiento);
        return toDTO(requerimiento);
    }

    private RequerimientoVisitaDTO toDTO(RequerimientoVisitaEntity entity) {
        RequerimientoVisitaDTO dto = new RequerimientoVisitaDTO();
        dto.setId(entity.getId());
        dto.setIdVisita(entity.getVisita().getId());
        dto.setDescripcion(entity.getDescripcion());
        dto.setFechaSolicitud(entity.getFechaSolicitud());
        dto.setEstado(entity.getEstado());
        dto.setRespuesta(entity.getRespuesta());
        dto.setFechaRespuesta(entity.getFechaRespuesta());

        if (entity.getVisita() != null) {
            if (entity.getVisita().getDocente() != null) {
                dto.setNombreDocente(entity.getVisita().getDocente().getNombres() + " " + entity.getVisita().getDocente().getApellidos());
            }
            if (entity.getVisita().getAsignatura() != null) {
                dto.setNombreAsignatura(entity.getVisita().getAsignatura().getNombre());
            }
            if (entity.getVisita().getSede() != null) {
                dto.setNombreSede(entity.getVisita().getSede().getNombre());
            }
        }

        return dto;
    }
}
