package com.visitas.backend_api.entity;

import com.visitas.backend_api.enums.EstadoVisita;
import com.visitas.backend_api.enums.TipoClase;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "visitainopinada")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitaInopinadaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_visita")
    private Integer id;

    @Column(name = "fecha_visita", nullable = false)
    private LocalDate fechaVisita;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_termino", nullable = false)
    private LocalTime horaTermino;

    @Column(name = "semana_numero")
    private Integer semanaNumero;

    @Column(name = "lugar_visita")
    private String lugarVisita;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_clase")
    private TipoClase tipoClase = TipoClase.TEORICA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sede")
    @NotFound(action = NotFoundAction.IGNORE)
    private SedeEntity sede;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_docente")
    @NotFound(action = NotFoundAction.IGNORE)
    private DocenteEntity docente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_asignatura")
    @NotFound(action = NotFoundAction.IGNORE)
    private AsignaturaEntity asignatura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_responsable")
    @NotFound(action = NotFoundAction.IGNORE)
    private ResponsableVisitaEntity responsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_auditor")
    @NotFound(action = NotFoundAction.IGNORE)
    private UsuarioSistemaEntity usuarioAuditor;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_visita")
    private EstadoVisita estadoVisita = EstadoVisita.BORRADOR;

    @Column(name = "firma_docente_hash", columnDefinition = "TEXT")
    private String firmaDocenteHash;

    @Column(name = "firma_responsable_hash", columnDefinition = "TEXT")
    private String firmaResponsableHash;

    @Column(name = "evidencia_imagen_hash", columnDefinition = "TEXT")
    private String evidenciaImagenHash;

    @Column(name = "fecha_firma_docente")
    private LocalDateTime fechaFirmaDocente;

    @Column(name = "fecha_firma_responsable")
    private LocalDateTime fechaFirmaResponsable;

    @Column(name = "fecha_registro", updatable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "visita", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EvaluacionControlDocenteEntity evaluacionControlDocente;

    @OneToOne(mappedBy = "visita", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EvaluacionMaterialVirtualEntity evaluacionMaterialVirtual;

    @OneToOne(mappedBy = "visita", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EvaluacionAsistenciaEstudiantesEntity evaluacionAsistenciaEstudiantes;

    @OneToOne(mappedBy = "visita", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EvaluacionAvanceSilabicoEntity evaluacionAvanceSilabico;

    @OneToOne(mappedBy = "visita", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private EvaluacionGuiaPracticaEntity evaluacionGuiaPractica;

    @OneToMany(mappedBy = "visita", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<RequerimientoVisitaEntity> requerimientos = new ArrayList<>();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
