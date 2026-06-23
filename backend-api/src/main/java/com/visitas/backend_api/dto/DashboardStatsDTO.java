package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    private Integer visitasEsteMes;
    private Double visitasCrecimiento; // Porcentaje vs mes anterior

    private Integer docentesEvaluados;
    private Double docentesCrecimiento; // Porcentaje vs mes anterior

    private Double cumplimientoGeneral;
    private Double cumplimientoPromedio; // +5% promedio evaluaciones

    private Integer requerimientosPendientes;
    private Integer requerimientosPorAtender; // -3 por atender
}
