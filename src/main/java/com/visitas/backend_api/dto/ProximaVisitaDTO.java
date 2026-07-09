package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProximaVisitaDTO {

    private Integer id;
    private String docente;
    private String asignatura;
    private LocalDate fecha;
    private LocalTime hora;
    private String sede;
    private String horarioFormateado; // "Hoy 10:00" o "Mañana 09:00"
}
