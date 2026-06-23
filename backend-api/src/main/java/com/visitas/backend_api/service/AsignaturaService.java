package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.AsignaturaDTO;
import com.visitas.backend_api.entity.AsignaturaEntity;
import com.visitas.backend_api.exception.ResourceNotFoundException;
import com.visitas.backend_api.repository.AsignaturaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsignaturaService {

    private final AsignaturaEntityRepository asignaturaRepository;

    public List<AsignaturaDTO> listarTodas() {
        return asignaturaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AsignaturaDTO obtenerPorId(Integer id) {
        AsignaturaEntity asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura", id));
        return toDTO(asignatura);
    }

    @Transactional
    public AsignaturaDTO crear(AsignaturaDTO dto) {
        AsignaturaEntity asignatura = new AsignaturaEntity();
        asignatura.setNombre(dto.getNombre());
        asignatura.setCampoFormativo(dto.getCampoFormativo());
        asignatura.setCicloAcademico(dto.getCicloAcademico());
        asignatura.setTurno(dto.getTurno());
        asignatura.setTipoHorario(dto.getTipoHorario());
        asignatura = asignaturaRepository.save(asignatura);
        return toDTO(asignatura);
    }

    @Transactional
    public AsignaturaDTO actualizar(Integer id, AsignaturaDTO dto) {
        AsignaturaEntity asignatura = asignaturaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignatura", id));
        asignatura.setNombre(dto.getNombre());
        asignatura.setCampoFormativo(dto.getCampoFormativo());
        asignatura.setCicloAcademico(dto.getCicloAcademico());
        asignatura.setTurno(dto.getTurno());
        asignatura.setTipoHorario(dto.getTipoHorario());
        asignatura = asignaturaRepository.save(asignatura);
        return toDTO(asignatura);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!asignaturaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Asignatura", id);
        }
        asignaturaRepository.deleteById(id);
    }

    private AsignaturaDTO toDTO(AsignaturaEntity entity) {
        AsignaturaDTO dto = new AsignaturaDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setCampoFormativo(entity.getCampoFormativo());
        dto.setCicloAcademico(entity.getCicloAcademico());
        dto.setTurno(entity.getTurno());
        dto.setTipoHorario(entity.getTipoHorario());
        return dto;
    }
}
