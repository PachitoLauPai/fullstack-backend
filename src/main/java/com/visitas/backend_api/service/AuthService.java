package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.LoginRequestDTO;
import com.visitas.backend_api.dto.LoginResponseDTO;
import com.visitas.backend_api.entity.UsuarioSistemaEntity;
import com.visitas.backend_api.enums.Rol;
import com.visitas.backend_api.repository.UsuarioSistemaEntityRepository;
import com.visitas.backend_api.security.CustomUserDetails;
import com.visitas.backend_api.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UsuarioSistemaEntityRepository usuarioSistemaRepository;

    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

        if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new BadCredentialsException("Bad credentials");
        }

        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        String token = jwtUtil.generateToken(customUserDetails);

        return new LoginResponseDTO(
                token,
                customUserDetails.getUsername(),
                customUserDetails.getNombres(),
                customUserDetails.getApellidos(),
                customUserDetails.getRol(),
                customUserDetails.getIdDocente(),
                customUserDetails.getIdResponsable()
        );
    }

    public UsuarioSistemaEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("Usuario no autenticado");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return usuarioSistemaRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Rol getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("Usuario no autenticado");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getRol();
    }

    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("Usuario no autenticado");
        }
        return ((CustomUserDetails) authentication.getPrincipal()).getId();
    }

    public Integer getCurrentDocenteId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new RuntimeException("Usuario no autenticado");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if (userDetails.getIdDocente() == null) {
            throw new RuntimeException("El usuario actual no es un docente");
        }
        return userDetails.getIdDocente();
    }
}
