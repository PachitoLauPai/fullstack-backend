package com.visitas.backend_api.service;
 
import com.visitas.backend_api.dto.ResponsableVisitaDTO;
import com.visitas.backend_api.entity.RolEntity;
import com.visitas.backend_api.entity.UsuarioSistemaEntity;
import com.visitas.backend_api.enums.Rol;
import com.visitas.backend_api.exception.ResourceNotFoundException;
import com.visitas.backend_api.repository.RolEntityRepository;
import com.visitas.backend_api.repository.UsuarioSistemaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
 
import java.util.List;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
public class ResponsableVisitaService {
 
    private final UsuarioSistemaEntityRepository usuarioRepository;
    private final RolEntityRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
 
    public List<ResponsableVisitaDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() != null && u.getRol().getNombreRol() == Rol.AUDITOR)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
 
    public List<ResponsableVisitaDTO> listarActivos() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() != null && u.getRol().getNombreRol() == Rol.AUDITOR && u.getEstado() != null && u.getEstado())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
 
    public ResponsableVisitaDTO obtenerPorId(Integer id) {
        UsuarioSistemaEntity responsable = usuarioRepository.findById(id)
                .filter(u -> u.getRol() != null && u.getRol().getNombreRol() == Rol.AUDITOR)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable", id));
        return toDTO(responsable);
    }
 
    @Transactional
    public ResponsableVisitaDTO crear(ResponsableVisitaDTO dto) {
        RolEntity rolAuditor = rolRepository.findById(2) // 2 is AUDITOR
                .orElseThrow(() -> new ResourceNotFoundException("Rol AUDITOR (2) no encontrado", 2));
 
        UsuarioSistemaEntity responsable = new UsuarioSistemaEntity();
        responsable.setNombres(dto.getNombres());
        responsable.setApellidos(dto.getApellidos());
        responsable.setCargo(dto.getCargo());
        responsable.setEmail(dto.getEmail());
        responsable.setRol(rolAuditor);
        responsable.setPasswordHash(passwordEncoder.encode("password123")); // Default password
        responsable.setEstado(true);
        responsable = usuarioRepository.save(responsable);
        return toDTO(responsable);
    }
 
    @Transactional
    public ResponsableVisitaDTO actualizar(Integer id, ResponsableVisitaDTO dto) {
        UsuarioSistemaEntity responsable = usuarioRepository.findById(id)
                .filter(u -> u.getRol() != null && u.getRol().getNombreRol() == Rol.AUDITOR)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable", id));
        responsable.setNombres(dto.getNombres());
        responsable.setApellidos(dto.getApellidos());
        responsable.setCargo(dto.getCargo());
        responsable.setEmail(dto.getEmail());
        responsable = usuarioRepository.save(responsable);
        return toDTO(responsable);
    }
 
    @Transactional
    public void eliminar(Integer id) {
        UsuarioSistemaEntity responsable = usuarioRepository.findById(id)
                .filter(u -> u.getRol() != null && u.getRol().getNombreRol() == Rol.AUDITOR)
                .orElseThrow(() -> new ResourceNotFoundException("Responsable", id));
        usuarioRepository.delete(responsable);
    }
 
    private ResponsableVisitaDTO toDTO(UsuarioSistemaEntity entity) {
        ResponsableVisitaDTO dto = new ResponsableVisitaDTO();
        dto.setId(entity.getId());
        dto.setNombres(entity.getNombres());
        dto.setApellidos(entity.getApellidos());
        dto.setCargo(entity.getCargo());
        dto.setEmail(entity.getEmail());
        return dto;
    }
}
