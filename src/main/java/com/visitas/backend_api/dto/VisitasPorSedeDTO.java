package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitasPorSedeDTO {

    private String sede;
    private Integer cantidad;
    private Double porcentaje;
}
