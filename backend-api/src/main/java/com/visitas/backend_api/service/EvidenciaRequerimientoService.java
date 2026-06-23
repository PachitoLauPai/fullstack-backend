package com.visitas.backend_api.service;

import com.visitas.backend_api.dto.EvidenciaRequerimientoDTO;
import com.visitas.backend_api.entity.EvidenciaRequerimientoEntity;
import com.visitas.backend_api.entity.RequerimientoVisitaEntity;
import com.visitas.backend_api.exception.ResourceNotFoundException;
import com.visitas.backend_api.repository.EvidenciaRequerimientoEntityRepository;
import com.visitas.backend_api.repository.RequerimientoVisitaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvidenciaRequerimientoService {

    private final EvidenciaRequerimientoEntityRepository evidenciaRepository;
    private final RequerimientoVisitaEntityRepository requerimientoRepository;
    private static final String UPLOAD_DIR = "uploads/evidencias/";

    @Transactional(readOnly = true)
    public List<EvidenciaRequerimientoDTO> listarPorRequerimiento(Integer idRequerimiento) {
        return evidenciaRepository.findByRequerimientoIdOrderByFechaCargaDesc(idRequerimiento).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EvidenciaRequerimientoDTO obtenerPorId(Integer id) {
        EvidenciaRequerimientoEntity evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia", id));
        return toDTO(evidencia);
    }

    @Transactional
    public EvidenciaRequerimientoDTO guardarEvidencia(Integer idRequerimiento, MultipartFile archivo, String descripcion) throws IOException {
        // Validar archivo
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El archivo es requerido y no puede estar vacío");
        }
        
        String nombreOriginal = archivo.getOriginalFilename();
        if (nombreOriginal == null || nombreOriginal.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede estar vacío");
        }
        
        // Buscar requerimiento
        RequerimientoVisitaEntity requerimiento = requerimientoRepository.findById(idRequerimiento)
                .orElseThrow(() -> new ResourceNotFoundException("Requerimiento", idRequerimiento));

        try {
            // Crear directorio si no existe
            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath();
            System.out.println("Intentando crear directorio: " + uploadPath);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Directorio creado: " + uploadPath);
            }

            // Generar nombre único para el archivo
            String nombreArchivo = System.currentTimeMillis() + "_" + nombreOriginal;
            Path filePath = uploadPath.resolve(nombreArchivo);
            System.out.println("Guardando archivo en: " + filePath);

            // Guardar archivo en disco
            Files.write(filePath, archivo.getBytes());
            System.out.println("Archivo guardado exitosamente");

            // Crear y guardar entidad de evidencia
            EvidenciaRequerimientoEntity evidencia = new EvidenciaRequerimientoEntity();
            evidencia.setRequerimiento(requerimiento);
            evidencia.setNombreArchivo(nombreOriginal);
            evidencia.setTipoArchivo(archivo.getContentType());
            evidencia.setRutaArchivo(filePath.toString());
            evidencia.setTamañoBytes(archivo.getSize());
            evidencia.setDescripcion(descripcion);
            evidencia.setFechaCarga(LocalDateTime.now());

            evidencia = evidenciaRepository.save(evidencia);
            System.out.println("Evidencia guardada en BD con ID: " + evidencia.getId());
            return toDTO(evidencia);
        } catch (Exception e) {
            System.err.println("Error al guardar evidencia: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    public void eliminar(Integer id) {
        EvidenciaRequerimientoEntity evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia", id));

        // Eliminar archivo del disco
        try {
            Files.deleteIfExists(Paths.get(evidencia.getRutaArchivo()));
        } catch (IOException e) {
            // Log de error pero continuar
            System.err.println("Error al eliminar archivo: " + e.getMessage());
        }

        evidenciaRepository.deleteById(id);
    }

    public byte[] descargarArchivo(Integer id) throws IOException {
        EvidenciaRequerimientoEntity evidencia = evidenciaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evidencia", id));

        return Files.readAllBytes(Paths.get(evidencia.getRutaArchivo()));
    }

    private EvidenciaRequerimientoDTO toDTO(EvidenciaRequerimientoEntity entity) {
        return new EvidenciaRequerimientoDTO(
                entity.getId(),
                entity.getRequerimiento().getId(),
                entity.getNombreArchivo(),
                entity.getTipoArchivo(),
                entity.getRutaArchivo(),
                entity.getTamañoBytes(),
                entity.getDescripcion(),
                entity.getFechaCarga()
        );
    }
}
