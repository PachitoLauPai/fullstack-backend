package com.visitas.backend_api.dto;

import com.visitas.backend_api.enums.EstadoVisita;
import com.visitas.backend_api.enums.TipoClase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitaResponseDTO {
    private Integer id;
    private LocalDate fechaVisita;
    private LocalTime horaInicio;
    private LocalTime horaTermino;
    private Integer semanaNumero;
    private String lugarVisita;
    private TipoClase tipoClase;
    private Integer idSede;
    private String nombreSede;
    private Integer idDocente;
    private String nombreDocente;
    private String apellidosDocente;
    private Integer idAsignatura;
    private String nombreAsignatura;
    private Integer idResponsable;
    private String nombreResponsable;
    private Integer idUsuarioAuditor;
    private String nombreAuditor;
    private EstadoVisita estadoVisita;
    private String firmaDocenteHash;
    private String firmaResponsableHash;
    private String evidenciaImagenHash;
    private LocalDateTime fechaFirmaDocente;
    private LocalDateTime fechaFirmaResponsable;
    private LocalDateTime fechaRegistro;
    private LocalDateTime updatedAt;
    private EvaluacionControlDocenteDTO evaluacionControlDocente;
    private EvaluacionMaterialVirtualDTO evaluacionMaterialVirtual;
    private EvaluacionAsistenciaEstudiantesDTO evaluacionAsistenciaEstudiantes;
    private EvaluacionAvanceSilabicoDTO evaluacionAvanceSilabico;
    private EvaluacionGuiaPracticaDTO evaluacionGuiaPractica;
    private List<RequerimientoVisitaDTO> requerimientos;
}
