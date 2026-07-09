package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.DocenteDTO;
import com.visitas.backend_api.dto.EmailRequestDTO;
import com.visitas.backend_api.service.DocenteService;
import com.visitas.backend_api.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/docentes")
@RequiredArgsConstructor
public class DocenteController {

    private final DocenteService docenteService;
    private final EmailService emailService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<DocenteDTO>> listarTodos() {
        return ResponseEntity.ok(docenteService.listarTodos());
    }

    @GetMapping("/activos")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<DocenteDTO>> listarActivos() {
        return ResponseEntity.ok(docenteService.listarActivos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR', 'DOCENTE')")
    public ResponseEntity<DocenteDTO> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(docenteService.obtenerPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocenteDTO> crear(@Valid @RequestBody DocenteDTO dto) {
        return ResponseEntity.ok(docenteService.crear(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DocenteDTO> actualizar(@PathVariable Integer id, @Valid @RequestBody DocenteDTO dto) {
        return ResponseEntity.ok(docenteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        docenteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/email")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> enviarEmail(@Valid @RequestBody EmailRequestDTO emailRequest) {
        emailService.enviarEmail(emailRequest);
        return ResponseEntity.ok().build();
    }
}
