package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.ResponsableVisitaDTO;
import com.visitas.backend_api.entity.ResponsableVisitaEntity;
import com.visitas.backend_api.exception.ResourceNotFoundException;
import com.visitas.backend_api.repository.ResponsableVisitaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResponsableVisitaService {

    private final ResponsableVisitaEntityRepository responsableRepository;

    public List<ResponsableVisitaDTO> listarTodos() {
        return responsableRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ResponsableVisitaDTO> listarActivos() {
        // Since there's no estadoActivo field, return all responsables
        return listarTodos();
    }

    public ResponsableVisitaDTO obtenerPorId(Integer id) {
        ResponsableVisitaEntity responsable = responsableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable", id));
        return toDTO(responsable);
    }

    @Transactional
    public ResponsableVisitaDTO crear(ResponsableVisitaDTO dto) {
        ResponsableVisitaEntity responsable = new ResponsableVisitaEntity();
        responsable.setNombres(dto.getNombres());
        responsable.setApellidos(dto.getApellidos());
        responsable.setCargo(dto.getCargo());
        responsable.setEmail(dto.getEmail());
        responsable = responsableRepository.save(responsable);
        return toDTO(responsable);
    }

    @Transactional
    public ResponsableVisitaDTO actualizar(Integer id, ResponsableVisitaDTO dto) {
        ResponsableVisitaEntity responsable = responsableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable", id));
        responsable.setNombres(dto.getNombres());
        responsable.setApellidos(dto.getApellidos());
        responsable.setCargo(dto.getCargo());
        responsable.setEmail(dto.getEmail());
        responsable = responsableRepository.save(responsable);
        return toDTO(responsable);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!responsableRepository.existsById(id)) {
            throw new ResourceNotFoundException("Responsable", id);
        }
        responsableRepository.deleteById(id);
    }

    private ResponsableVisitaDTO toDTO(ResponsableVisitaEntity entity) {
        ResponsableVisitaDTO dto = new ResponsableVisitaDTO();
        dto.setId(entity.getId());
        dto.setNombres(entity.getNombres());
        dto.setApellidos(entity.getApellidos());
        dto.setCargo(entity.getCargo());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}
