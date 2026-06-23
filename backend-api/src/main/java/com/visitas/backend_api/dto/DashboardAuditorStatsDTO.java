package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAuditorStatsDTO {
    
    private long visitasEsteMes;
    private long docentesEvaluados;
    private long requerimientosPendientes;
    private List<VisitaResumenDTO> proximasVisitas;
    private List<VisitaResumenDTO> visitasRecientes;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VisitaResumenDTO {
        private Integer id;
        private String docenteNombre;
        private String asignaturaNombre;
        private String fechaVisita;
        private String horaInicio;
        private String sedeNombre;
        private String estadoVisita;
    }
}
