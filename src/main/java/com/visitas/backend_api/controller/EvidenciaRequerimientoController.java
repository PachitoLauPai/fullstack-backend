package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.EvidenciaRequerimientoDTO;
import com.visitas.backend_api.service.EvidenciaRequerimientoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/evidencias")
@RequiredArgsConstructor
public class EvidenciaRequerimientoController {

    private final EvidenciaRequerimientoService evidenciaService;

    @GetMapping("/requerimiento/{idRequerimiento}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'DOCENTE')")
    public ResponseEntity<List<EvidenciaRequerimientoDTO>> listarPorRequerimiento(
            @PathVariable Integer idRequerimiento) {
        return ResponseEntity.ok(evidenciaService.listarPorRequerimiento(idRequerimiento));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'DOCENTE')")
    public ResponseEntity<EvidenciaRequerimientoDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(evidenciaService.obtenerPorId(id));
    }

    @PostMapping(value = "/{idRequerimiento}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<EvidenciaRequerimientoDTO> subirEvidencia(
            @PathVariable Integer idRequerimiento,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam(value = "descripcion", required = false) String descripcion) throws IOException {
        
        if (archivo.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        EvidenciaRequerimientoDTO evidencia = evidenciaService.guardarEvidencia(idRequerimiento, archivo, descripcion);
        return ResponseEntity.ok(evidencia);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        evidenciaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/descargar")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'DOCENTE')")
    public ResponseEntity<byte[]> descargarArchivo(@PathVariable Integer id) throws IOException {
        EvidenciaRequerimientoDTO evidencia = evidenciaService.obtenerPorId(id);
        byte[] contenido = evidenciaService.descargarArchivo(id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + evidencia.getNombreArchivo() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, evidencia.getTipoArchivo())
                .body(contenido);
    }

    @GetMapping("/{id}/ver")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'DOCENTE')")
    public ResponseEntity<byte[]> verArchivo(@PathVariable Integer id) throws IOException {
        EvidenciaRequerimientoDTO evidencia = evidenciaService.obtenerPorId(id);
        byte[] contenido = evidenciaService.descargarArchivo(id);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, evidencia.getTipoArchivo())
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .body(contenido);
    }
}
