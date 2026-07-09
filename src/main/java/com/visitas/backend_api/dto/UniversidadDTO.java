package com.visitas.backend_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UniversidadDTO {

    private Integer id;

    @NotBlank(message = "El nombre de la universidad es obligatorio")
    private String nombreUniversidad;

    private String direccion;
}
