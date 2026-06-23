package com.visitas.backend_api.dto;

import com.visitas.backend_api.enums.EstadoRequerimiento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequerimientoVisitaDTO {
    private Integer id;
    private Integer idVisita;
    private String descripcion;
    private LocalDate fechaSolicitud;
    private EstadoRequerimiento estado;
    private String respuesta;
    private LocalDate fechaRespuesta;
    private String nombreDocente;
    private String nombreAsignatura;
    private String nombreSede;
}
