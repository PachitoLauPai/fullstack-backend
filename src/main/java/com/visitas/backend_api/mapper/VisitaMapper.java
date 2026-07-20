package com.visitas.backend_api.mapper;

import com.visitas.backend_api.dto.EvaluacionAsistenciaEstudiantesDTO;
import com.visitas.backend_api.dto.EvaluacionAvanceSilabicoDTO;
import com.visitas.backend_api.dto.EvaluacionControlDocenteDTO;
import com.visitas.backend_api.dto.EvaluacionGuiaPracticaDTO;
import com.visitas.backend_api.dto.EvaluacionMaterialVirtualDTO;
import com.visitas.backend_api.dto.RequerimientoVisitaDTO;
import com.visitas.backend_api.dto.VisitaCreateDTO;
import com.visitas.backend_api.dto.VisitaResponseDTO;
import com.visitas.backend_api.entity.EvaluacionAsistenciaEstudiantesEntity;
import com.visitas.backend_api.entity.EvaluacionAvanceSilabicoEntity;
import com.visitas.backend_api.entity.EvaluacionControlDocenteEntity;
import com.visitas.backend_api.entity.EvaluacionGuiaPracticaEntity;
import com.visitas.backend_api.entity.EvaluacionMaterialVirtualEntity;
import com.visitas.backend_api.entity.RequerimientoVisitaEntity;
import com.visitas.backend_api.entity.VisitaInopinadaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface VisitaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "estadoVisita", ignore = true)
    @Mapping(target = "firmaDocenteHash", source = "firmaDocente")
    @Mapping(target = "firmaResponsableHash", source = "firmaResponsable")
    @Mapping(target = "evidenciaImagenHash", source = "evidenciaImagen")
    @Mapping(target = "fechaFirmaDocente", ignore = true)
    @Mapping(target = "fechaFirmaResponsable", ignore = true)
    @Mapping(target = "fechaRegistro", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "evaluacionControlDocente", ignore = true)
    @Mapping(target = "evaluacionMaterialVirtual", ignore = true)
    @Mapping(target = "evaluacionAsistenciaEstudiantes", ignore = true)
    @Mapping(target = "evaluacionAvanceSilabico", ignore = true)
    @Mapping(target = "evaluacionGuiaPractica", ignore = true)
    @Mapping(target = "requerimientos", ignore = true)
    @Mapping(target = "sede", ignore = true)
    @Mapping(target = "docente", ignore = true)
    @Mapping(target = "asignatura", ignore = true)
    @Mapping(target = "responsable", ignore = true)
    @Mapping(target = "usuarioAuditor", ignore = true)
    VisitaInopinadaEntity toEntity(VisitaCreateDTO dto);

    @Mapping(target = "nombreSede", source = "sede.nombre")
    @Mapping(target = "nombreDocente", source = "docente.nombres")
    @Mapping(target = "apellidosDocente", source = "docente.apellidos")
    @Mapping(target = "nombreAsignatura", source = "asignatura.nombre")
    @Mapping(target = "nombreResponsable", expression = "java(entity.getResponsable() != null ? entity.getResponsable().getNombres() + \" \" + entity.getResponsable().getApellidos() : null)")
    @Mapping(target = "idUsuarioAuditor", source = "usuarioAuditor.id")
    @Mapping(target = "nombreAuditor", expression = "java(entity.getUsuarioAuditor() != null ? entity.getUsuarioAuditor().getNombres() + \" \" + entity.getUsuarioAuditor().getApellidos() : null)")
    @Mapping(target = "idSede", source = "sede.id")
    @Mapping(target = "idDocente", source = "docente.id")
    @Mapping(target = "idAsignatura", source = "asignatura.id")
    @Mapping(target = "idResponsable", source = "responsable.id")
    @Mapping(target = "evaluacionControlDocente", source = "evaluacionControlDocente", qualifiedByName = "toDTO")
    @Mapping(target = "evaluacionMaterialVirtual", source = "evaluacionMaterialVirtual", qualifiedByName = "toDTO")
    @Mapping(target = "evaluacionAsistenciaEstudiantes", source = "evaluacionAsistenciaEstudiantes", qualifiedByName = "toDTO")
    @Mapping(target = "evaluacionAvanceSilabico", source = "evaluacionAvanceSilabico", qualifiedByName = "toDTO")
    @Mapping(target = "evaluacionGuiaPractica", source = "evaluacionGuiaPractica", qualifiedByName = "toDTO")
    @Mapping(target = "requerimientos", source = "requerimientos", qualifiedByName = "toRequerimientoListDTO")
    VisitaResponseDTO toResponseDTO(VisitaInopinadaEntity entity);

    @Named("toDTO")
    EvaluacionControlDocenteEntity toEntity(EvaluacionControlDocenteDTO dto);

    @Named("toDTO")
    EvaluacionControlDocenteDTO toDTO(EvaluacionControlDocenteEntity entity);

    @Named("toDTO")
    EvaluacionMaterialVirtualEntity toEntity(EvaluacionMaterialVirtualDTO dto);

    @Named("toDTO")
    EvaluacionMaterialVirtualDTO toDTO(EvaluacionMaterialVirtualEntity entity);

    @Named("toDTO")
    EvaluacionAsistenciaEstudiantesEntity toEntity(EvaluacionAsistenciaEstudiantesDTO dto);

    @Named("toDTO")
    EvaluacionAsistenciaEstudiantesDTO toDTO(EvaluacionAsistenciaEstudiantesEntity entity);

    @Named("toDTO")
    EvaluacionAvanceSilabicoEntity toEntity(EvaluacionAvanceSilabicoDTO dto);

    @Named("toDTO")
    EvaluacionAvanceSilabicoDTO toDTO(EvaluacionAvanceSilabicoEntity entity);

    @Named("toDTO")
    EvaluacionGuiaPracticaEntity toEntity(EvaluacionGuiaPracticaDTO dto);

    @Named("toDTO")
    EvaluacionGuiaPracticaDTO toDTO(EvaluacionGuiaPracticaEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visita", ignore = true)
    void updateEntityFromDTO(EvaluacionControlDocenteDTO dto, @MappingTarget EvaluacionControlDocenteEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visita", ignore = true)
    void updateEntityFromDTO(EvaluacionMaterialVirtualDTO dto, @MappingTarget EvaluacionMaterialVirtualEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visita", ignore = true)
    void updateEntityFromDTO(EvaluacionAsistenciaEstudiantesDTO dto, @MappingTarget EvaluacionAsistenciaEstudiantesEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visita", ignore = true)
    void updateEntityFromDTO(EvaluacionAvanceSilabicoDTO dto, @MappingTarget EvaluacionAvanceSilabicoEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visita", ignore = true)
    void updateEntityFromDTO(EvaluacionGuiaPracticaDTO dto, @MappingTarget EvaluacionGuiaPracticaEntity entity);

    @Named("toRequerimientoListDTO")
    default List<RequerimientoVisitaDTO> toRequerimientoListDTO(List<RequerimientoVisitaEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toRequerimientoDTO)
                .collect(Collectors.toList());
    }

    RequerimientoVisitaDTO toRequerimientoDTO(RequerimientoVisitaEntity entity);
}
