package com.visitas.backend_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvidenciaRequerimientoDTO {
    private Integer id;
    private Integer idRequerimiento;
    private String nombreArchivo;
    private String tipoArchivo;
    private String rutaArchivo;
    private Long tamañoBytes;
    private String descripcion;
    private LocalDateTime fechaCarga;
}
