package com.visitas.backend_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluacioncontroldocente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionControlDocenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_visita", unique = true, nullable = false)
    private VisitaInopinadaEntity visita;

    @Column(name = "docente_presente")
    private Boolean docentePresente = false;

    @Column(name = "horario_cumplido")
    private Boolean horarioCumplido = false;

    @Column(name = "interaccion_adecuada")
    private Boolean interaccionAdecuada = false;

    @Column(name = "actividad_desarrollada", columnDefinition = "TEXT")
    private String actividadDesarrollada;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
