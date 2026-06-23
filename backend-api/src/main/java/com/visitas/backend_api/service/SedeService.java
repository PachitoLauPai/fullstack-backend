package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.SedeDTO;
import com.visitas.backend_api.entity.SedeEntity;
import com.visitas.backend_api.entity.UniversidadEntity;
import com.visitas.backend_api.exception.ResourceNotFoundException;
import com.visitas.backend_api.repository.SedeEntityRepository;
import com.visitas.backend_api.repository.UniversidadEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SedeService {

    private final SedeEntityRepository sedeRepository;
    private final UniversidadEntityRepository universidadRepository;

    public List<SedeDTO> listarTodas() {
        return sedeRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<SedeDTO> listarPorUniversidad(Integer idUniversidad) {
        return sedeRepository.findByUniversidad_Id(idUniversidad).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SedeDTO obtenerPorId(Integer id) {
        SedeEntity sede = sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede", id));
        return toDTO(sede);
    }

    @Transactional
    public SedeDTO crear(SedeDTO dto) {
        UniversidadEntity universidad = universidadRepository.findById(dto.getIdUniversidad())
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", dto.getIdUniversidad()));

        SedeEntity sede = new SedeEntity();
        sede.setNombre(dto.getNombre());
        sede.setUniversidad(universidad);
        sede = sedeRepository.save(sede);
        return toDTO(sede);
    }

    @Transactional
    public SedeDTO actualizar(Integer id, SedeDTO dto) {
        SedeEntity sede = sedeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sede", id));

        UniversidadEntity universidad = universidadRepository.findById(dto.getIdUniversidad())
                .orElseThrow(() -> new ResourceNotFoundException("Universidad", dto.getIdUniversidad()));

        sede.setNombre(dto.getNombre());
        sede.setUniversidad(universidad);
        sede = sedeRepository.save(sede);
        return toDTO(sede);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!sedeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Sede", id);
        }
        sedeRepository.deleteById(id);
    }

    private SedeDTO toDTO(SedeEntity entity) {
        SedeDTO dto = new SedeDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setIdUniversidad(entity.getUniversidad().getId());
        dto.setNombreUniversidad(entity.getUniversidad().getNombre());
        return dto;
    }
}
