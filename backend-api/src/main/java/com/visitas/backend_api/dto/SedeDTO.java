package com.visitas.backend_api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SedeDTO {

    private Integer id;

    @NotBlank(message = "El nombre de la sede es obligatorio")
    private String nombre;

    @NotNull(message = "Debe especificar la universidad")
    private Integer idUniversidad;

    private String nombreUniversidad;
}
