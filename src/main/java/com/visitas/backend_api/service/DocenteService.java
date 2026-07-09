package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.DocenteDTO;
import com.visitas.backend_api.entity.DocenteEntity;
import com.visitas.backend_api.exception.ResourceNotFoundException;
import com.visitas.backend_api.repository.DocenteEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocenteService {

    private final DocenteEntityRepository docenteRepository;

    public List<DocenteDTO> listarTodos() {
        return docenteRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DocenteDTO> listarActivos() {
        List<DocenteEntity> docentes = docenteRepository.findAll();
        return docentes.stream()
                .filter(d -> d.getEstadoActivo() != null && d.getEstadoActivo())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public DocenteDTO obtenerPorId(Integer id) {
        DocenteEntity docente = docenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente", id));
        return toDTO(docente);
    }

    @Transactional
    public DocenteDTO crear(DocenteDTO dto) {
        DocenteEntity docente = new DocenteEntity();
        docente.setNombres(dto.getNombres());
        docente.setApellidos(dto.getApellidos());
        docente.setEmail(dto.getEmail());
        docente.setEstadoActivo(dto.getEstadoActivo() != null ? dto.getEstadoActivo() : true);
        docente = docenteRepository.save(docente);
        return toDTO(docente);
    }

    @Transactional
    public DocenteDTO actualizar(Integer id, DocenteDTO dto) {
        DocenteEntity docente = docenteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Docente", id));
        docente.setNombres(dto.getNombres());
        docente.setApellidos(dto.getApellidos());
        docente.setEmail(dto.getEmail());
        docente.setEstadoActivo(dto.getEstadoActivo());
        docente = docenteRepository.save(docente);
        return toDTO(docente);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!docenteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Docente", id);
        }
        docenteRepository.deleteById(id);
    }

    private DocenteDTO toDTO(DocenteEntity entity) {
        DocenteDTO dto = new DocenteDTO();
        dto.setId(entity.getId());
        dto.setNombres(entity.getNombres());
        dto.setApellidos(entity.getApellidos());
        dto.setEmail(entity.getEmail());
        dto.setEstadoActivo(entity.getEstadoActivo());
        return dto;
    }
}
