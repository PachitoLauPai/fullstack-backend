package com.visitas.backend_api.dto;

import com.visitas.backend_api.enums.TipoClase;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitaCreateDTO {

    @NotNull(message = "La fecha de visita es obligatoria")
    private LocalDate fechaVisita;

    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime horaInicio;

    @NotNull(message = "La hora de término es obligatoria")
    private LocalTime horaTermino;

    private Integer semanaNumero;
    private String lugarVisita;
    private TipoClase tipoClase = TipoClase.TEORICA;

    @NotNull(message = "El ID de sede es obligatorio")
    private Integer idSede;

    @NotNull(message = "El ID de docente es obligatorio")
    private Integer idDocente;

    @NotNull(message = "El ID de asignatura es obligatorio")
    private Integer idAsignatura;

    @NotNull(message = "El ID de responsable es obligatorio")
    private Integer idResponsable;

    private EvaluacionControlDocenteDTO evaluacionControlDocente;
    private EvaluacionMaterialVirtualDTO evaluacionMaterialVirtual;
    private EvaluacionAsistenciaEstudiantesDTO evaluacionAsistenciaEstudiantes;
    private EvaluacionAvanceSilabicoDTO evaluacionAvanceSilabico;
    private EvaluacionGuiaPracticaDTO evaluacionGuiaPractica;

    private List<RequerimientoCreateDTO> requerimientos;
    private String firmaDocente;
    private String firmaResponsable;
    private String evidenciaImagen;
}
