package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.SedeDTO;
import com.visitas.backend_api.service.SedeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sedes")
@RequiredArgsConstructor
public class SedeController {

    private final SedeService sedeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<SedeDTO>> listarTodas() {
        return ResponseEntity.ok(sedeService.listarTodas());
    }

    @GetMapping("/universidad/{idUniversidad}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<SedeDTO>> listarPorUniversidad(@PathVariable Integer idUniversidad) {
        return ResponseEntity.ok(sedeService.listarPorUniversidad(idUniversidad));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<SedeDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(sedeService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SedeDTO> crear(@Valid @RequestBody SedeDTO dto) {
        return ResponseEntity.ok(sedeService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SedeDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody SedeDTO dto) {
        return ResponseEntity.ok(sedeService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        sedeService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
