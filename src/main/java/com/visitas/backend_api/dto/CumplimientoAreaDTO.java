package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CumplimientoAreaDTO {

    private String area;
    private Double porcentajeCumplimiento;
}
