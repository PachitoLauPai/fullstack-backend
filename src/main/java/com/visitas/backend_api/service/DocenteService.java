package com.visitas.backend_api.service;
 
import com.visitas.backend_api.dto.DocenteDTO;
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
public class DocenteService {
 
    private final UsuarioSistemaEntityRepository usuarioRepository;
    private final RolEntityRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
 
    public List<DocenteDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() != null && u.getRol().getNombreRol() == Rol.DOCENTE)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
 
    public List<DocenteDTO> listarActivos() {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getRol() != null && u.getRol().getNombreRol() == Rol.DOCENTE && u.getEstado() != null && u.getEstado())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
 
    public DocenteDTO obtenerPorId(Integer id) {
        UsuarioSistemaEntity docente = usuarioRepository.findById(id)
                .filter(u -> u.getRol() != null && u.getRol().getNombreRol() == Rol.DOCENTE)
                .orElseThrow(() -> new ResourceNotFoundException("Docente", id));
        return toDTO(docente);
    }
 
    @Transactional
    public DocenteDTO crear(DocenteDTO dto) {
        RolEntity rolDocente = rolRepository.findById(3) // 3 is DOCENTE
                .orElseThrow(() -> new ResourceNotFoundException("Rol DOCENTE (3) no encontrado", 3));
 
        UsuarioSistemaEntity docente = new UsuarioSistemaEntity();
        docente.setNombres(dto.getNombres());
        docente.setApellidos(dto.getApellidos());
        docente.setEmail(dto.getEmail());
        docente.setRol(rolDocente);
        docente.setPasswordHash(passwordEncoder.encode("password123")); // Default password
        docente.setEstado(dto.getEstadoActivo() != null ? dto.getEstadoActivo() : true);
        docente = usuarioRepository.save(docente);
        return toDTO(docente);
    }
 
    @Transactional
    public DocenteDTO actualizar(Integer id, DocenteDTO dto) {
        UsuarioSistemaEntity docente = usuarioRepository.findById(id)
                .filter(u -> u.getRol() != null && u.getRol().getNombreRol() == Rol.DOCENTE)
                .orElseThrow(() -> new ResourceNotFoundException("Docente", id));
        docente.setNombres(dto.getNombres());
        docente.setApellidos(dto.getApellidos());
        docente.setEmail(dto.getEmail());
        docente.setEstado(dto.getEstadoActivo());
        docente = usuarioRepository.save(docente);
        return toDTO(docente);
    }
 
    @Transactional
    public void eliminar(Integer id) {
        UsuarioSistemaEntity docente = usuarioRepository.findById(id)
                .filter(u -> u.getRol() != null && u.getRol().getNombreRol() == Rol.DOCENTE)
                .orElseThrow(() -> new ResourceNotFoundException("Docente", id));
        usuarioRepository.delete(docente);
    }
 
    private DocenteDTO toDTO(UsuarioSistemaEntity entity) {
        DocenteDTO dto = new DocenteDTO();
        dto.setId(entity.getId());
        dto.setNombres(entity.getNombres());
        dto.setApellidos(entity.getApellidos());
        dto.setEmail(entity.getEmail());
        dto.setEstadoActivo(entity.getEstado());
        return dto;
    }
}
