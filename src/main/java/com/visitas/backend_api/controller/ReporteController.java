package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.*;
import com.visitas.backend_api.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReporteDataDTO> obtenerReporteCompleto(
            @RequestParam(defaultValue = "semestre") String periodo) {
        return ResponseEntity.ok(reporteService.obtenerReporteCompleto(periodo));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReportesStatsDTO> obtenerEstadisticas(
            @RequestParam(defaultValue = "semestre") String periodo) {
        return ResponseEntity.ok(reporteService.obtenerEstadisticas(periodo));
    }

    @GetMapping("/visitas-por-sede")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VisitasPorSedeDTO>> obtenerVisitasPorSede() {
        return ResponseEntity.ok(reporteService.obtenerVisitasPorSede());
    }

    @GetMapping("/top-docentes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TopDocenteDTO>> obtenerTopDocentes() {
        return ResponseEntity.ok(reporteService.obtenerTopDocentes());
    }

    @GetMapping("/requerimientos-pendientes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RequerimientoPendienteDTO>> obtenerRequerimientosPendientes() {
        return ResponseEntity.ok(reporteService.obtenerRequerimientosPendientes());
    }

    @GetMapping("/exportar/pdf")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(defaultValue = "semester") String periodo) {
        byte[] pdfBytes = reporteService.exportarPdf(periodo);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_visitas_" + periodo + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/exportar/excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(defaultValue = "semester") String periodo) {
        byte[] excelBytes = reporteService.exportarExcel(periodo);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "reporte_visitas_" + periodo + ".xlsx");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(excelBytes);
    }
}
