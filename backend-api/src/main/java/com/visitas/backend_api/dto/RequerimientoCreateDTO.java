package com.visitas.backend_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequerimientoCreateDTO {
    private Integer idVisita; // Opcional, se asigna automáticamente al crear la visita

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
}
