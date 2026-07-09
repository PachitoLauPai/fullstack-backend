package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequerimientoPendienteDTO {

    private Integer id;
    private String descripcion;
    private String docente;
    private LocalDate fecha;
    private String tipo; // "urgente", "normal", "bajo"
}
