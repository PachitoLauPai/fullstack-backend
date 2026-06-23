package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionAvanceSilabicoDTO {
    private Boolean temaCoincideVisita = false;
    private Boolean temaCoincideAnterior = false;
    private Boolean ingresoAulaVirtual = false;
    private String observaciones;
}
