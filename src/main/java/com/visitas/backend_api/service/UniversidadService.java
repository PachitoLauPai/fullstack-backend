package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.UniversidadDTO;
import com.visitas.backend_api.entity.UniversidadEntity;
import com.visitas.backend_api.exception.ResourceNotFoundException;
import com.visitas.backend_api.repository.UniversidadEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversidadService {

    private final UniversidadEntityRepository universidadRepository;

    public List<UniversidadDTO> listarTodas() {
        return universidadRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UniversidadDTO obtenerPorId(Integer id) {
        UniversidadEntity universidad = universidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", id));
        return toDTO(universidad);
    }

    @Transactional
    public UniversidadDTO crear(UniversidadDTO dto) {
        UniversidadEntity universidad = new UniversidadEntity();
        universidad.setNombre(dto.getNombreUniversidad());
        universidad = universidadRepository.save(universidad);
        return toDTO(universidad);
    }

    @Transactional
    public UniversidadDTO actualizar(Integer id, UniversidadDTO dto) {
        UniversidadEntity universidad = universidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", id));
        universidad.setNombre(dto.getNombreUniversidad());
        universidad = universidadRepository.save(universidad);
        return toDTO(universidad);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!universidadRepository.existsById(id)) {
            throw new ResourceNotFoundException("Universidad", id);
        }
        universidadRepository.deleteById(id);
    }

    private UniversidadDTO toDTO(UniversidadEntity entity) {
        UniversidadDTO dto = new UniversidadDTO();
        dto.setId(entity.getId());
        dto.setNombreUniversidad(entity.getNombre());
        return dto;
    }
}
