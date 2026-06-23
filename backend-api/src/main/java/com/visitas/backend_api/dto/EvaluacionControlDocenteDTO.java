package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluacionControlDocenteDTO {
    private Boolean docentePresente = false;
    private Boolean horarioCumplido = false;
    private Boolean interaccionAdecuada = false;
    private String actividadDesarrollada;
    private String observaciones;
}
