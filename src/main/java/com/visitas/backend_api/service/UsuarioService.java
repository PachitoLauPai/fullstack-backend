package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.UsuarioDTO;
import com.visitas.backend_api.entity.RolEntity;
import com.visitas.backend_api.entity.UsuarioSistemaEntity;
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
public class UsuarioService {

    private final UsuarioSistemaEntityRepository usuarioRepository;
    private final RolEntityRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> listarActivos() {
        List<UsuarioSistemaEntity> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .filter(u -> u.getEstado() != null && u.getEstado())
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDTO> listarAuditors() {
        List<UsuarioSistemaEntity> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .filter(u -> u.getEstado() != null && u.getEstado() && 
                           u.getRol() != null && u.getRol().getNombreRol().name().equals("AUDITOR"))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UsuarioDTO obtenerPorId(Integer id) {
        UsuarioSistemaEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO crear(UsuarioDTO dto) {
        if (dto.getDni() != null && !dto.getDni().isBlank()) {
            String cleanDni = dto.getDni().trim();
            if (!cleanDni.matches("\\d{8}")) {
                throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos");
            }
            if (usuarioRepository.findByDni(cleanDni).isPresent()) {
                throw new IllegalArgumentException("El DNI ya está registrado");
            }
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
                throw new IllegalArgumentException("El correo electrónico ya está registrado");
            }
        }

        RolEntity rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", dto.getIdRol()));

        UsuarioSistemaEntity usuario = new UsuarioSistemaEntity();
        usuario.setEmail(dto.getEmail());
        usuario.setPasswordHash(passwordEncoder.encode(
                dto.getPassword() != null && !dto.getPassword().isBlank()
                        ? dto.getPassword()
                        : "password123"
        ));
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setRol(rol);
        usuario.setDni(dto.getDni());
        usuario.setCargo(dto.getCargo());
        usuario.setEstado(true);

        usuario = usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Transactional
    public UsuarioDTO actualizar(Integer id, UsuarioDTO dto) {
        UsuarioSistemaEntity usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));

        if (dto.getDni() != null && !dto.getDni().isBlank()) {
            String cleanDni = dto.getDni().trim();
            if (!cleanDni.matches("\\d{8}")) {
                throw new IllegalArgumentException("El DNI debe tener exactamente 8 dígitos numéricos");
            }
            usuarioRepository.findByDni(cleanDni).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("El DNI ya está registrado por otro usuario");
                }
            });
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            usuarioRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new IllegalArgumentException("El correo electrónico ya está registrado por otro usuario");
                }
            });
        }

        RolEntity rol = rolRepository.findById(dto.getIdRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol", dto.getIdRol()));

        usuario.setEmail(dto.getEmail());
        usuario.setNombres(dto.getNombres());
        usuario.setApellidos(dto.getApellidos());
        usuario.setRol(rol);
        usuario.setDni(dto.getDni());
        usuario.setCargo(dto.getCargo());
        usuario.setEstado(dto.getEstado());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        usuario = usuarioRepository.save(usuario);
        return toDTO(usuario);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario", id);
        }
        usuarioRepository.deleteById(id);
    }

    private UsuarioDTO toDTO(UsuarioSistemaEntity entity) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setNombres(entity.getNombres());
        dto.setApellidos(entity.getApellidos());
        dto.setIdRol(entity.getRol().getId());
        dto.setRol(entity.getRol().getNombreRol().name());
        dto.setDni(entity.getDni());
        dto.setCargo(entity.getCargo());
        dto.setEstado(entity.getEstado());

        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    @Transactional
    public String guardarMiFirma(String firmaHash) {
        UsuarioSistemaEntity usuario = authService.getCurrentUser();
        usuario.setFirmaHash(firmaHash);
        usuarioRepository.save(usuario);
        return "Firma guardada exitosamente";
    }

    public String obtenerMiFirma() {
        UsuarioSistemaEntity usuario = authService.getCurrentUser();
        return usuario.getFirmaHash();
    }
}
