package com.visitas.backend_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitaProgramarDTO {
    
    @NotNull(message = "El id del docente es obligatorio")
    private Integer idDocente;
    
    @NotNull(message = "El id de la asignatura es obligatorio")
    private Integer idAsignatura;
    
    @NotNull(message = "El id de la sede es obligatorio")
    private Integer idSede;
    
    @NotNull(message = "La fecha de la visita es obligatoria")
    private LocalDate fechaVisita;
    
    @NotNull(message = "El id del auditor es obligatorio")
    private Integer idAuditor;
}
