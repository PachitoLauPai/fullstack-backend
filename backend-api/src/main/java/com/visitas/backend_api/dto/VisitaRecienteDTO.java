package com.visitas.backend_api.dto;

import com.visitas.backend_api.enums.EstadoVisita;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitaRecienteDTO {

    private String id; // VIS-001, VIS-002, etc.
    private String docente;
    private String asignatura;
    private String sede;
    private LocalDate fecha;
    private LocalTime hora;
    private EstadoVisita estado;
    private String estadoFormateado; // "Cumple", "Parcial", "No Cumple"
}
