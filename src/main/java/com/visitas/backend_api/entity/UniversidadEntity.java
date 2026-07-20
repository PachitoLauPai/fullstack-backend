package com.visitas.backend_api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Universidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniversidadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_universidad")
    private Integer id;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "vicerrectorado")
    private String vicerrectorado;

    @Column(name = "facultad")
    private String facultad;

    @Column(name = "escuela_profesional")
    private String escuelaProfesional;
}
