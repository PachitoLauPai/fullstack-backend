package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportesStatsDTO {

    private Integer totalVisitas;
    private Double totalVisitasCrecimiento;

    private Double cumplimiento;
    private Double cumplimientoCrecimiento;

    private Integer docentesVisitados;
    private Integer totalDocentes;

    private Integer sedesActivas;
    private String sedesDescripcion;
}
