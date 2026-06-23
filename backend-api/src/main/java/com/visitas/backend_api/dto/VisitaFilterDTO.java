package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitaFilterDTO {
    private String busqueda; // Busca por docente, asignatura o ID de visita
    private Integer idSede;
    private String estado; // BORRADOR, FIRMADA_DOCENTE, COMPLETADA, AUDITADA
    private LocalDate fechaDesde;
    private LocalDate fechaHasta;
}
