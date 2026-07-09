package com.visitas.backend_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "evaluacionmaterialvirtual")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionMaterialVirtualEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evaluacion")
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_visita", unique = true, nullable = false)
    private VisitaInopinadaEntity visita;

    @Column(name = "cumple")
    private Boolean cumple = false;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
}
