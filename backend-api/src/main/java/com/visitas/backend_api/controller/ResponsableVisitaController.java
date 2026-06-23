package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.ResponsableVisitaDTO;
import com.visitas.backend_api.service.ResponsableVisitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/responsables")
@RequiredArgsConstructor
public class ResponsableVisitaController {

    private final ResponsableVisitaService responsableService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<ResponsableVisitaDTO>> listarTodos() {
        return ResponseEntity.ok(responsableService.listarTodos());
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<ResponsableVisitaDTO>> listarActivos() {
        return ResponseEntity.ok(responsableService.listarActivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<ResponsableVisitaDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(responsableService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponsableVisitaDTO> crear(@Valid @RequestBody ResponsableVisitaDTO dto) {
        return ResponseEntity.ok(responsableService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponsableVisitaDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody ResponsableVisitaDTO dto) {
        return ResponseEntity.ok(responsableService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        responsableService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
