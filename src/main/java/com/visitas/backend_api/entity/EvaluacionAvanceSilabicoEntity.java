package com.visitas.backend_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluacionavancesilabico")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionAvanceSilabicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_visita", unique = true, nullable = false)
    private VisitaInopinadaEntity visita;

    @Column(name = "tema_coincide_visita")
    private Boolean temaCoincideVisita = false;

    @Column(name = "tema_coincide_anterior")
    private Boolean temaCoincideAnterior = false;

    @Column(name = "ingreso_aula_virtual")
    private Boolean ingresoAulaVirtual = false;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
