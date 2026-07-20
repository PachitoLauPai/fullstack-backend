package com.visitas.backend_api.entity;

import com.visitas.backend_api.enums.ResultadoControl;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "EvaluacionGuiaPractica")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionGuiaPracticaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_visita", unique = true, nullable = false)
    private VisitaInopinadaEntity visita;

    @Enumerated(EnumType.STRING)
    @Column(name = "tema_programado_cumple")
    private ResultadoControl temaProgramadoCumple = ResultadoControl.NO_APLICA;

    @Enumerated(EnumType.STRING)
    @Column(name = "logro_evidenciado")
    private ResultadoControl logroEvidenciado = ResultadoControl.NO_APLICA;

    @Enumerated(EnumType.STRING)
    @Column(name = "rubrica_evaluacion")
    private ResultadoControl rubricaEvaluacion = ResultadoControl.NO_APLICA;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
