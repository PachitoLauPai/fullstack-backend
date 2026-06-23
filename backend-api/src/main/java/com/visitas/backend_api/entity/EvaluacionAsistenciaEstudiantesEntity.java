package com.visitas.backend_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluacionasistenciaestudiantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionAsistenciaEstudiantesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_visita", unique = true, nullable = false)
    private VisitaInopinadaEntity visita;

    @Column(name = "ambiente_cumple")
    private String ambienteCumple;

    @Column(name = "ambiente_observaciones", columnDefinition = "TEXT")
    private String ambienteObservaciones;

    @Column(name = "intranet_cumple")
    private String intranetCumple;

    @Column(name = "intranet_observaciones", columnDefinition = "TEXT")
    private String intranetObservaciones;

    @Column(name = "observaciones_generales", columnDefinition = "TEXT")
    private String observacionesGenerales;
}
