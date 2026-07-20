package com.visitas.backend_api.security;

import com.visitas.backend_api.entity.UsuarioSistemaEntity;
import com.visitas.backend_api.repository.UsuarioSistemaEntityRepository;
import com.visitas.backend_api.enums.Rol;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioSistemaEntityRepository usuarioSistemaRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("BUSCANDO USUARIO CON EMAIL: [" + email + "]");
        UsuarioSistemaEntity usuario = usuarioSistemaRepository.findByEmail(email)
                .orElseThrow(() -> {
                    System.out.println("USUARIO NO ENCONTRADO EN REPOSITORIO");
                    return new UsernameNotFoundException("Usuario no encontrado con email: " + email);
                });

        System.out.println("USUARIO ENCONTRADO: " + usuario.getEmail() + " - ID: " + usuario.getId());
        Rol rol = (usuario.getRol() != null) ? usuario.getRol().getNombreRol() : null;
        Integer idDocente = rol == Rol.DOCENTE ? usuario.getId() : null;
        Integer idResponsable = rol == Rol.AUDITOR ? usuario.getId() : null;

        return new CustomUserDetails(
                usuario.getId(),
                usuario.getEmail(),
                usuario.getPasswordHash(),
                usuario.getNombres(),
                usuario.getApellidos(),
                rol,
                idDocente,
                idResponsable,
                usuario.getEstado()
        );
    }
}
