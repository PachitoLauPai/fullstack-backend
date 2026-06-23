package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopDocenteDTO {

    private Integer ranking;
    private String nombre;
    private Integer totalVisitas;
    private Double cumplimiento;
}
