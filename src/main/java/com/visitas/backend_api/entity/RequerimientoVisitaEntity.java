package com.visitas.backend_api.entity;

import com.visitas.backend_api.enums.EstadoRequerimiento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "requerimientovisita")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequerimientoVisitaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_requerimiento")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_visita", nullable = false)
    private VisitaInopinadaEntity visita;

    @Column(name = "descripcion", columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoRequerimiento estado = EstadoRequerimiento.PENDIENTE;

    @Column(name = "respuesta", columnDefinition = "TEXT")
    private String respuesta;

    @Column(name = "fecha_respuesta")
    private LocalDate fechaRespuesta;
}
