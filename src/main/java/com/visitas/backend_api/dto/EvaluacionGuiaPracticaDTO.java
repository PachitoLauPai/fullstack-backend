package com.visitas.backend_api.dto;

import com.visitas.backend_api.enums.ResultadoControl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionGuiaPracticaDTO {
    private ResultadoControl temaProgramadoCumple = ResultadoControl.NO_APLICA;
    private ResultadoControl logroEvidenciado = ResultadoControl.NO_APLICA;
    private ResultadoControl rubricaEvaluacion = ResultadoControl.NO_APLICA;
    private String observaciones;
}
