package com.visitas.backend_api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignaturaDTO {

    private Integer id;

    @NotBlank(message = "El nombre de la asignatura es obligatorio")
    private String nombre;

    private String campoFormativo;

    private String cicloAcademico;

    private String turno;

    private String tipoHorario;
}
