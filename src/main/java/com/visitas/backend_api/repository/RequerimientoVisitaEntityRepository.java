package com.visitas.backend_api.repository;

import com.visitas.backend_api.entity.RequerimientoVisitaEntity;
import com.visitas.backend_api.enums.EstadoRequerimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequerimientoVisitaEntityRepository extends JpaRepository<RequerimientoVisitaEntity, Integer> {
    List<RequerimientoVisitaEntity> findByVisitaId(Integer idVisita);
    
    @Query("SELECT COUNT(r) FROM RequerimientoVisitaEntity r WHERE r.visita.usuarioAuditor.id = :auditorId AND r.estado IN (:estados)")
    long countRequerimientosPendientesByAuditor(@Param("auditorId") Integer auditorId, @Param("estados") List<EstadoRequerimiento> estados);
    
    // Nuevos métodos para flujo de requerimientos
    @Query("SELECT r FROM RequerimientoVisitaEntity r WHERE r.visita.docente.id = :docenteId ORDER BY r.fechaSolicitud DESC")
    List<RequerimientoVisitaEntity> findByVisitaDocenteId(@Param("docenteId") Integer docenteId);
    
    @Query("SELECT r FROM RequerimientoVisitaEntity r WHERE r.visita.usuarioAuditor.id = :auditorId ORDER BY r.fechaSolicitud DESC")
    List<RequerimientoVisitaEntity> findByVisitaUsuarioAuditorId(@Param("auditorId") Integer auditorId);
}
