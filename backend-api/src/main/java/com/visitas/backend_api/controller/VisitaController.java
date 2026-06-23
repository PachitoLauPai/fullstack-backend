package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.DashboardAuditorStatsDTO;
import com.visitas.backend_api.dto.VisitaCreateDTO;
import com.visitas.backend_api.dto.VisitaFilterDTO;
import com.visitas.backend_api.dto.VisitaProgramarDTO;
import com.visitas.backend_api.dto.VisitaResponseDTO;
import com.visitas.backend_api.entity.VisitaInopinadaEntity;
import com.visitas.backend_api.repository.VisitaInopinadaEntityRepository;
import com.visitas.backend_api.service.PdfService;
import com.visitas.backend_api.service.VisitaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/visitas")
@RequiredArgsConstructor
public class VisitaController {

    private final VisitaService visitaService;
    private final VisitaInopinadaEntityRepository visitaRepository;
    private final PdfService pdfService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VisitaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(visitaService.listarTodas());
    }

    @GetMapping("/mis-visitas-docente")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<List<VisitaResponseDTO>> listarMisVisitasComoDocente() {
        return ResponseEntity.ok(visitaService.listarMisVisitasComoDocente());
    }

    @GetMapping("/mis-visitas-auditor")
    @PreAuthorize("hasRole('AUDITOR')")
    public ResponseEntity<List<VisitaResponseDTO>> listarMisVisitasComoAuditor() {
        return ResponseEntity.ok(visitaService.listarMisVisitasComoAuditor());
    }

    @PostMapping("/filtrar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VisitaResponseDTO>> filtrarVisitas(@RequestBody VisitaFilterDTO filter) {
        return ResponseEntity.ok(visitaService.filtrarVisitas(filter));
    }

    @PostMapping("/filtrar-auditor")
    @PreAuthorize("hasRole('AUDITOR')")
    public ResponseEntity<List<VisitaResponseDTO>> filtrarVisitasAuditor(@RequestBody VisitaFilterDTO filter) {
        return ResponseEntity.ok(visitaService.filtrarVisitasAuditor(filter));
    }

    @PostMapping("/filtrar-docente")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<List<VisitaResponseDTO>> filtrarVisitasDocente(@RequestBody VisitaFilterDTO filter) {
        return ResponseEntity.ok(visitaService.filtrarVisitasDocente(filter));
    }

    // Endpoint temporal para depuración - REMOVER EN PRODUCCION
    @GetMapping("/debug-all")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<VisitaInopinadaEntity>> debugAllVisitas() {
        List<VisitaInopinadaEntity> visitas = visitaRepository.findAll();
        System.out.println("TOTAL VISITAS: " + visitas.size());
        for (VisitaInopinadaEntity v : visitas) {
            System.out.println("Visita ID: " + v.getId() + 
                ", Auditor ID: " + (v.getUsuarioAuditor() != null ? v.getUsuarioAuditor().getId() : "NULL") +
                ", Estado: " + v.getEstadoVisita());
        }
        return ResponseEntity.ok(visitas);
    }

    @GetMapping("/docente/{idDocente}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUDITOR')")
    public ResponseEntity<List<VisitaResponseDTO>> listarPorDocente(@PathVariable Integer idDocente) {
        return ResponseEntity.ok(visitaService.listarPorDocente(idDocente));
    }

    @PostMapping
    @PreAuthorize("hasRole('AUDITOR')")
    public ResponseEntity<VisitaResponseDTO> crearVisita(@Valid @RequestBody VisitaCreateDTO dto) {
        VisitaResponseDTO response = visitaService.crearVisita(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUDITOR', 'DOCENTE', 'ADMIN')")
    public ResponseEntity<VisitaResponseDTO> obtenerVisitaPorId(@PathVariable Integer id) {
        VisitaResponseDTO response = visitaService.obtenerVisitaPorId(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AUDITOR')")
    public ResponseEntity<VisitaResponseDTO> actualizarVisita(
            @PathVariable Integer id,
            @Valid @RequestBody VisitaCreateDTO dto) {
        VisitaResponseDTO response = visitaService.actualizarVisita(id, dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/evaluaciones")
    @PreAuthorize("hasRole('AUDITOR')")
    public ResponseEntity<VisitaResponseDTO> actualizarEvaluaciones(
            @PathVariable Integer id,
            @Valid @RequestBody VisitaCreateDTO dto) {
        VisitaResponseDTO response = visitaService.actualizarEvaluaciones(id, dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/firma-docente")
    @PreAuthorize("hasRole('DOCENTE')")
    public ResponseEntity<VisitaResponseDTO> firmarPorDocente(
            @PathVariable Integer id,
            @RequestBody String firmaHash) {
        VisitaResponseDTO response = visitaService.firmarPorDocente(id, firmaHash);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/firma-auditor")
    @PreAuthorize("hasRole('AUDITOR')")
    public ResponseEntity<VisitaResponseDTO> firmarPorAuditor(
            @PathVariable Integer id,
            @RequestBody String firmaHash) {
        VisitaResponseDTO response = visitaService.firmarPorAuditor(id, firmaHash);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/auditor-stats")
    @PreAuthorize("hasRole('AUDITOR')")
    public ResponseEntity<DashboardAuditorStatsDTO> obtenerEstadisticasDashboard() {
        DashboardAuditorStatsDTO stats = visitaService.obtenerEstadisticasDashboardAuditor();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAnyRole('AUDITOR', 'DOCENTE', 'ADMIN')")
    public ResponseEntity<byte[]> generarPdfVisita(@PathVariable Integer id) throws Exception {
        byte[] pdfBytes = pdfService.generarPdfVisita(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "visita-" + id + ".pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/exportar/pdf")
    @PreAuthorize("hasAnyRole('AUDITOR', 'DOCENTE', 'ADMIN')")
    public ResponseEntity<byte[]> exportarVisitasPdf(@RequestBody VisitaFilterDTO filter) throws Exception {
        byte[] pdfBytes = pdfService.generarPdfVisitas(visitaService.filtrarVisitasEntitiesPorRol(filter));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "visitas_filtradas.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @PostMapping("/programar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VisitaResponseDTO> programarVisitaParaAuditor(@Valid @RequestBody VisitaProgramarDTO dto) {
        VisitaResponseDTO response = visitaService.programarVisitaParaAuditor(dto);
        return ResponseEntity.ok(response);
    }
}
