package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteDataDTO {

    private ReportesStatsDTO estadisticas;
    private List<CumplimientoAreaDTO> cumplimientoPorArea;
    private List<VisitasPorSedeDTO> visitasPorSede;
    private List<EvolucionCumplimientoDTO> evolucionCumplimiento;
    private List<TopDocenteDTO> topDocentes;
    private List<RequerimientoPendienteDTO> requerimientosPendientes;
}
