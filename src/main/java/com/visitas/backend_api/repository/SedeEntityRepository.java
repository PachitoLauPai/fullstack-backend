package com.visitas.backend_api.repository;

import com.visitas.backend_api.entity.SedeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SedeEntityRepository extends JpaRepository<SedeEntity, Integer> {
    List<SedeEntity> findByUniversidad_Id(Integer idUniversidad);
}
