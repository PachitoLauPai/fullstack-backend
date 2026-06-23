package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionAsistenciaEstudiantesDTO {
    private String ambienteCumple;
    private String ambienteObservaciones;
    private String intranetCumple;
    private String intranetObservaciones;
    private String observacionesGenerales;
}
