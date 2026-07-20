package com.visitas.backend_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Asignatura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignaturaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_asignatura")
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "campo_formativo")
    private String campoFormativo;

    @Column(name = "ciclo_academico")
    private String cicloAcademico;

    @Column(name = "turno")
    private String turno;

    @Column(name = "tipo_horario")
    private String tipoHorario;
}
