package com.visitas.backend_api.repository;

import com.visitas.backend_api.entity.EvidenciaRequerimientoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvidenciaRequerimientoEntityRepository extends JpaRepository<EvidenciaRequerimientoEntity, Integer> {
    List<EvidenciaRequerimientoEntity> findByRequerimientoId(Integer idRequerimiento);
    
    @Query("SELECT e FROM EvidenciaRequerimientoEntity e WHERE e.requerimiento.id = :idRequerimiento ORDER BY e.fechaCarga DESC")
    List<EvidenciaRequerimientoEntity> findByRequerimientoIdOrderByFechaCargaDesc(@Param("idRequerimiento") Integer idRequerimiento);
}
