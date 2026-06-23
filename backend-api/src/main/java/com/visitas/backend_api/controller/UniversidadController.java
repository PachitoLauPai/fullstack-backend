package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.UniversidadDTO;
import com.visitas.backend_api.service.UniversidadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/universidades")
@RequiredArgsConstructor
public class UniversidadController {

    private final UniversidadService universidadService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<UniversidadDTO>> listarTodas() {
        return ResponseEntity.ok(universidadService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<UniversidadDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(universidadService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UniversidadDTO> crear(@Valid @RequestBody UniversidadDTO dto) {
        return ResponseEntity.ok(universidadService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UniversidadDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody UniversidadDTO dto) {
        return ResponseEntity.ok(universidadService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        universidadService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
