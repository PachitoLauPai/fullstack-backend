package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.AsignaturaDTO;
import com.visitas.backend_api.service.AsignaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaturas")
@RequiredArgsConstructor
public class AsignaturaController {

    private final AsignaturaService asignaturaService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<AsignaturaDTO>> listarTodas() {
        return ResponseEntity.ok(asignaturaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<AsignaturaDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(asignaturaService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AsignaturaDTO> crear(@Valid @RequestBody AsignaturaDTO dto) {
        return ResponseEntity.ok(asignaturaService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AsignaturaDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody AsignaturaDTO dto) {
        return ResponseEntity.ok(asignaturaService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        asignaturaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
