package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.RequerimientoCreateDTO;
import com.visitas.backend_api.dto.RequerimientoUpdateDTO;
import com.visitas.backend_api.dto.RequerimientoVisitaDTO;
import com.visitas.backend_api.service.RequerimientoVisitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requerimientos")
@RequiredArgsConstructor
public class RequerimientoVisitaController {

    private final RequerimientoVisitaService requerimientoService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'DOCENTE')")
    public ResponseEntity<List<RequerimientoVisitaDTO>> listarTodos() {
        return ResponseEntity.ok(requerimientoService.listarTodos());
    }

    @GetMapping("/visita/{idVisita}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'DOCENTE')")
    public ResponseEntity<List<RequerimientoVisitaDTO>> listarPorVisita(@PathVariable Integer idVisita) {
        return ResponseEntity.ok(requerimientoService.listarPorVisita(idVisita));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'DOCENTE')")
    public ResponseEntity<RequerimientoVisitaDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(requerimientoService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('AUDITOR', 'DOCENTE')")
    public ResponseEntity<RequerimientoVisitaDTO> crear(@Valid @RequestBody RequerimientoCreateDTO dto) {
        return ResponseEntity.ok(requerimientoService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<RequerimientoVisitaDTO> responder(
            @PathVariable Integer id,
            @Valid @RequestBody RequerimientoUpdateDTO dto) {
        return ResponseEntity.ok(requerimientoService.responder(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        requerimientoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Nuevos endpoints para flujo de requerimientos
    
    @GetMapping("/mis-requerimientos")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<List<RequerimientoVisitaDTO>> listarMisRequerimientosComoDocente() {
        return ResponseEntity.ok(requerimientoService.listarMisRequerimientosComoDocente());
    }
    
    @GetMapping("/requerimientos-de-mis-visitas")
    @PreAuthorize("hasRole('AUDITOR')")
    public ResponseEntity<List<RequerimientoVisitaDTO>> listarRequerimientosDeMisVisitas() {
        return ResponseEntity.ok(requerimientoService.listarRequerimientosDeMisVisitas());
    }
    
    @PostMapping("/{id}/atender")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<RequerimientoVisitaDTO> atenderRequerimiento(
            @PathVariable Integer id,
            @RequestBody String respuesta) {
        return ResponseEntity.ok(requerimientoService.atenderRequerimientoComoDocente(id, respuesta));
    }
}
