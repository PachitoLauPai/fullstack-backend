package com.visitas.backend_api.controller;

import com.visitas.backend_api.dto.DashboardDataDTO;
import com.visitas.backend_api.dto.DashboardStatsDTO;
import com.visitas.backend_api.dto.ProximaVisitaDTO;
import com.visitas.backend_api.dto.VisitaRecienteDTO;
import com.visitas.backend_api.dto.VisitaSemanaDTO;
import com.visitas.backend_api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardDataDTO> obtenerDashboardCompleto() {
        return ResponseEntity.ok(dashboardService.obtenerDashboardData());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardStatsDTO> obtenerEstadisticas() {
        return ResponseEntity.ok(dashboardService.obtenerEstadisticas());
    }

    @GetMapping("/visitas-por-semana")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VisitaSemanaDTO>> obtenerVisitasPorSemana() {
        return ResponseEntity.ok(dashboardService.obtenerVisitasPorSemana());
    }

    @GetMapping("/proximas-visitas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProximaVisitaDTO>> obtenerProximasVisitas() {
        return ResponseEntity.ok(dashboardService.obtenerProximasVisitas());
    }

    @GetMapping("/visitas-recientes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VisitaRecienteDTO>> obtenerVisitasRecientes() {
        return ResponseEntity.ok(dashboardService.obtenerVisitasRecientes());
    }
}
