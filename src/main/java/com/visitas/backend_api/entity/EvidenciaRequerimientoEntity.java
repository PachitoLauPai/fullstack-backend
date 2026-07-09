package com.visitas.backend_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "evidenciarequerimiento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvidenciaRequerimientoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evidencia")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_requerimiento", nullable = false)
    private RequerimientoVisitaEntity requerimiento;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "tipo_archivo", length = 50)
    private String tipoArchivo;

    @Column(name = "ruta_archivo", length = 500, nullable = false)
    private String rutaArchivo;

    @Column(name = "tamaño_bytes")
    private Long tamañoBytes = 0L;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_carga")
    private LocalDateTime fechaCarga = LocalDateTime.now();
}
