package com.visitas.backend_api.repository;

import com.visitas.backend_api.entity.DocenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocenteEntityRepository extends JpaRepository<DocenteEntity, Integer> {
    List<DocenteEntity> findByEstadoActivoTrue();
}
