package com.visitas.backend_api.repository;

import com.visitas.backend_api.entity.UsuarioSistemaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioSistemaEntityRepository extends JpaRepository<UsuarioSistemaEntity, Integer> {
    Optional<UsuarioSistemaEntity> findByEmail(String email);
    List<UsuarioSistemaEntity> findByEstadoTrue();
}
