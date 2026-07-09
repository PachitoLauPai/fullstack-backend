package com.visitas.backend_api.repository;

import com.visitas.backend_api.entity.VisitaInopinadaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import com.visitas.backend_api.enums.EstadoVisita;

public interface VisitaInopinadaEntityRepository extends JpaRepository<VisitaInopinadaEntity, Integer> {
    List<VisitaInopinadaEntity> findByEstadoVisita(String estadoVisita);
    List<VisitaInopinadaEntity> findByDocenteId(Integer idDocente);
    List<VisitaInopinadaEntity> findByUsuarioAuditorId(Integer idAuditor);
    
    // Estadísticas para dashboard
    @Query("SELECT COUNT(v) FROM VisitaInopinadaEntity v WHERE v.usuarioAuditor.id = :auditorId AND MONTH(v.fechaVisita) = MONTH(CURRENT_DATE) AND YEAR(v.fechaVisita) = YEAR(CURRENT_DATE)")
    long countVisitasEsteMesByAuditor(@Param("auditorId") Integer auditorId);
    
    @Query("SELECT COUNT(DISTINCT v.docente.id) FROM VisitaInopinadaEntity v WHERE v.usuarioAuditor.id = :auditorId")
    long countDocentesEvaluadosByAuditor(@Param("auditorId") Integer auditorId);
    
    @Query("SELECT v FROM VisitaInopinadaEntity v WHERE v.usuarioAuditor.id = :auditorId ORDER BY v.id DESC")
    List<VisitaInopinadaEntity> findRecentVisitasByAuditor(@Param("auditorId") Integer auditorId);
    
    @Query("SELECT v FROM VisitaInopinadaEntity v WHERE v.usuarioAuditor.id = :auditorId AND v.fechaVisita >= :fecha ORDER BY v.fechaVisita ASC")
    List<VisitaInopinadaEntity> findProximasVisitasByAuditor(@Param("auditorId") Integer auditorId, @Param("fecha") LocalDate fecha);
    
    // Métodos de filtrado
    @Query("SELECT v FROM VisitaInopinadaEntity v WHERE " +
           "(:busqueda IS NULL OR LOWER(v.docente.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR LOWER(v.docente.apellidos) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR LOWER(CONCAT(v.docente.nombres, ' ', v.docente.apellidos)) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR LOWER(CONCAT(v.docente.apellidos, ' ', v.docente.nombres)) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR LOWER(v.asignatura.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR CAST(v.id AS string) LIKE CONCAT('%', :busqueda, '%')) " +
           "AND (:idSede IS NULL OR v.sede.id = :idSede) " +
           "AND (:estado IS NULL OR v.estadoVisita = :estado) " +
           "AND (:fechaDesde IS NULL OR v.fechaVisita >= :fechaDesde) " +
           "AND (:fechaHasta IS NULL OR v.fechaVisita <= :fechaHasta) " +
           "ORDER BY v.id DESC")
    List<VisitaInopinadaEntity> filtrarVisitas(
        @Param("busqueda") String busqueda,
        @Param("idSede") Integer idSede,
        @Param("estado") EstadoVisita estado,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );
    
    @Query("SELECT v FROM VisitaInopinadaEntity v WHERE " +
           "v.usuarioAuditor.id = :auditorId " +
           "AND (:busqueda IS NULL OR LOWER(v.docente.nombres) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR LOWER(v.docente.apellidos) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR LOWER(CONCAT(v.docente.nombres, ' ', v.docente.apellidos)) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR LOWER(CONCAT(v.docente.apellidos, ' ', v.docente.nombres)) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR LOWER(v.asignatura.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR CAST(v.id AS string) LIKE CONCAT('%', :busqueda, '%')) " +
           "AND (:idSede IS NULL OR v.sede.id = :idSede) " +
           "AND (:estado IS NULL OR v.estadoVisita = :estado) " +
           "AND (:fechaDesde IS NULL OR v.fechaVisita >= :fechaDesde) " +
           "AND (:fechaHasta IS NULL OR v.fechaVisita <= :fechaHasta) " +
           "ORDER BY v.id DESC")
    List<VisitaInopinadaEntity> filtrarVisitasPorAuditor(
        @Param("auditorId") Integer auditorId,
        @Param("busqueda") String busqueda,
        @Param("idSede") Integer idSede,
        @Param("estado") EstadoVisita estado,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );
    
    @Query("SELECT v FROM VisitaInopinadaEntity v WHERE " +
           "v.docente.id = :docenteId " +
           "AND (:busqueda IS NULL OR LOWER(v.asignatura.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) " +
           "  OR CAST(v.id AS string) LIKE CONCAT('%', :busqueda, '%')) " +
           "AND (:idSede IS NULL OR v.sede.id = :idSede) " +
           "AND (:estado IS NULL OR v.estadoVisita = :estado) " +
           "AND (:fechaDesde IS NULL OR v.fechaVisita >= :fechaDesde) " +
           "AND (:fechaHasta IS NULL OR v.fechaVisita <= :fechaHasta) " +
           "ORDER BY v.id DESC")
    List<VisitaInopinadaEntity> filtrarVisitasPorDocente(
        @Param("docenteId") Integer docenteId,
        @Param("busqueda") String busqueda,
        @Param("idSede") Integer idSede,
        @Param("estado") EstadoVisita estado,
        @Param("fechaDesde") LocalDate fechaDesde,
        @Param("fechaHasta") LocalDate fechaHasta
    );
}

