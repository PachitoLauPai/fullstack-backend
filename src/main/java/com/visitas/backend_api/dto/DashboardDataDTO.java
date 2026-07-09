package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataDTO {

    private DashboardStatsDTO estadisticas;
    private List<VisitaSemanaDTO> visitasPorSemana;
    private List<ProximaVisitaDTO> proximasVisitas;
    private List<VisitaRecienteDTO> visitasRecientes;
}
