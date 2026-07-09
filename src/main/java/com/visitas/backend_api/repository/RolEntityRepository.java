package com.visitas.backend_api.repository;

import com.visitas.backend_api.entity.RolEntity;
import com.visitas.backend_api.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolEntityRepository extends JpaRepository<RolEntity, Integer> {
    Optional<RolEntity> findByNombreRol(Rol nombreRol);
}
