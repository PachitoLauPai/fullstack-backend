package com.visitas.backend_api.service;


import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.visitas.backend_api.entity.VisitaInopinadaEntity;
import com.visitas.backend_api.exception.ResourceNotFoundException;
import com.visitas.backend_api.repository.VisitaInopinadaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final VisitaInopinadaEntityRepository visitaRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT  = DateTimeFormatter.ofPattern("HH:mm");

    // Gris exacto del formato oficial VRA-FR-040
    private static final DeviceRgb GRAY_BG    = new DeviceRgb(217, 217, 217);
    private static final String    CHECK_MARK = "X";

    // Altura estándar de fila
    private static final float ROW_H      = 12.5f;
    private static final float ROW_H_TALL = 18f;

    public byte[] generarPdfVisita(Integer id) throws Exception {

        VisitaInopinadaEntity visita = visitaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visita", id));

        System.out.println("DEBUG - Generando PDF para visita ID: " + id);
        System.out.println("DEBUG - Estado visita: " + visita.getEstadoVisita());
        System.out.println("DEBUG - Docente: " + (visita.getDocente() != null ? visita.getDocente().getNombres() + " " + visita.getDocente().getApellidos() : "NULL"));
        System.out.println("DEBUG - Asignatura: " + (visita.getAsignatura() != null ? visita.getAsignatura().getNombre() : "NULL"));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter  writer  = new PdfWriter(baos);
             PdfDocument pdfDoc  = new PdfDocument(writer);
             Document   document = new Document(pdfDoc, PageSize.A4)) {

            // Márgenes ajustados para caber en 1 página A4
            document.setMargins(14, 18, 14, 18);

            // ── Fuentes: usar estándar embebido para compatibilidad cross-OS ──
            // Si el servidor tiene Arial disponible, se puede cambiar a la ruta real.
            PdfFont font     = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

            // ═══════════════════════════════════════════════
            // 1. ENCABEZADO INSTITUCIONAL con logo a la izquierda
            // ═══════════════════════════════════════════════
            Table headerTable = new Table(UnitValue.createPercentArray(new float[]{18, 64, 18}))
                    .setWidth(UnitValue.createPercentValue(100))
                    .setMarginBottom(1);

            // Columna izquierda: logo UTP
            Cell logoCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setHorizontalAlignment(HorizontalAlignment.CENTER);

            try {
                java.io.InputStream logoStream = getClass().getClassLoader().getResourceAsStream("logo_utp.png");
                if (logoStream == null) {
                    // intentar con iconoutp.jpg si no existe el PNG
                    logoStream = getClass().getClassLoader().getResourceAsStream("iconoutp.jpg");
                }
                if (logoStream != null) {
                    byte[] logoBytes = logoStream.readAllBytes();
                    Image logoImg = new Image(ImageDataFactory.create(logoBytes))
                            .setHeight(45)
                            .setAutoScaleWidth(true)
                            .setHorizontalAlignment(HorizontalAlignment.CENTER);
                    logoCell.add(logoImg);
                } else {
                    // Placeholder si no se encuentra la imagen
                    logoCell.add(new Paragraph("UTP").setFont(boldFont).setFontSize(10)
                            .setTextAlignment(TextAlignment.CENTER));
                }
            } catch (Exception imgEx) {
                System.out.println("WARN - No se pudo cargar el logo: " + imgEx.getMessage());
                logoCell.add(new Paragraph("UTP").setFont(boldFont).setFontSize(10)
                        .setTextAlignment(TextAlignment.CENTER));
            }
            headerTable.addCell(logoCell);

            // Columna central: texto institucional centrado en la página
            Cell textCell = new Cell()
                    .setBorder(Border.NO_BORDER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
            textCell.add(new Paragraph("UNIVERSIDAD PRIVADA DEL NORTE")
                    .setFont(boldFont).setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(1));
            textCell.add(new Paragraph("VICERRECTORADO ACADÉMICO")
                    .setFont(boldFont).setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER).setMarginBottom(1));
            textCell.add(new Paragraph("FACULTAD DE INGENIERÍAS  ESCUELA PROFESIONAL DE INGENIERÍA DE SISTEMAS")
                    .setFont(boldFont).setFontSize(7.5f)
                    .setTextAlignment(TextAlignment.CENTER));
            headerTable.addCell(textCell);

            // Columna derecha vacía: balancea la anchura del logo para centrar el texto
            headerTable.addCell(new Cell().setBorder(Border.NO_BORDER));

            // Fila 2: título centrado en TODA la anchura (colspan 3)
            Cell titleCell = new Cell(1, 3)
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER);
            titleCell.add(new Paragraph("VISITA INOPINADA – CLASES PRESENCIALES")
                    .setFont(boldFont).setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setUnderline(0.5f, -2f)
                    .setMarginTop(1).setMarginBottom(0));
            headerTable.addCell(titleCell);

            document.add(headerTable);

            // ═══════════════════════════════════════════════
            // 2. TABLA DATOS GENERALES
            //    Col widths (%) matchean la imagen:
            //    Label(16) | Value(14) | Label(16) | Value(13) | Label(20) | Value(10) | Label(?) …
            //    Agrupamos en 3 pares → 6 columnas
            // ═══════════════════════════════════════════════
            // Proporciones: etiqueta-ancha | valor | etiqueta | valor | etiqueta-ancha | valor
            Table genTable = createBaseTable(new float[]{18, 13, 16, 12, 24, 17}, font, 7);
            genTable.setMarginBottom(0);

            // Fila 1: FECHA DE VISITA | valor | HORA DE INICIO VISITA: | valor | HORA DE TÉRMINO DE VISITA: | valor
            addLabelCell(genTable, "FECHA DE VISITA", boldFont);
            addValueCell(genTable, safeDate(visita.getFechaVisita()), font);
            addLabelCell(genTable, "HORA DE INICIO VISITA:", boldFont);
            addValueCell(genTable, safeTime(visita.getHoraInicio()), font);
            addLabelCell(genTable, "HORA DE TÉRMINO DE VISITA:", boldFont);
            addValueCell(genTable, safeTime(visita.getHoraTermino()), font);

            // Fila 2: SEDE O FILIAL | valor | CICLO: | valor | TURNO: | valor
            addLabelCell(genTable, "SEDE O FILIAL:", boldFont);
            addValueCell(genTable, safeStr(visita.getSede() != null ? visita.getSede().getNombre() : ""), font);
            addLabelCell(genTable, "CICLO:", boldFont);
            addValueCell(genTable, safeStr(visita.getAsignatura() != null ? visita.getAsignatura().getCicloAcademico() : ""), font);
            addLabelCell(genTable, "TURNO:", boldFont);
            addValueCell(genTable, safeStr(visita.getAsignatura() != null ? visita.getAsignatura().getTurno() : ""), font);

            // Fila 3: ASIGNATURA (colspan 2) | CAMPO FORMATIVO (colspan 2)
            addLabelCell(genTable, "ASIGNATURA:", boldFont);
            genTable.addCell(new Cell(1, 3)
                    .add(new Paragraph(safeStr(visita.getAsignatura() != null ? visita.getAsignatura().getNombre() : ""))
                            .setFont(font).setFontSize(7))
                    .setPaddingLeft(3).setMinHeight(ROW_H));
            addLabelCell(genTable, "CAMPO FORMATIVO:", boldFont);
            genTable.addCell(new Cell(1, 1)
                    .add(new Paragraph(safeStr(visita.getAsignatura() != null ? visita.getAsignatura().getCampoFormativo() : ""))
                            .setFont(font).setFontSize(7))
                    .setPaddingLeft(3).setMinHeight(ROW_H));

            // Fila 4: SEMANA Nº (colspan 2) | HORA PRÁCTICA/HORA TEORÍA (colspan 2)
            addLabelCell(genTable, "SEMANA Nº:", boldFont);
            genTable.addCell(new Cell(1, 3)
                    .add(new Paragraph(safeStr(visita.getSemanaNumero() != null ? visita.getSemanaNumero().toString() : ""))
                            .setFont(font).setFontSize(7))
                    .setPaddingLeft(3).setMinHeight(ROW_H));
            addLabelCell(genTable, "HORA PRÁCTICA/HORA TEORÍA", boldFont);
            genTable.addCell(new Cell(1, 1)
                    .add(new Paragraph(safeStr(visita.getAsignatura() != null ? visita.getAsignatura().getTipoHorario() : ""))
                            .setFont(font).setFontSize(7))
                    .setPaddingLeft(3).setMinHeight(ROW_H));

            // Fila 5: LUGAR DE LA VISITA (colspan 5)
            addLabelCell(genTable, "LUGAR DE LA VISITA:", boldFont);
            genTable.addCell(new Cell(1, 5)
                    .add(new Paragraph(safeStr(visita.getLugarVisita())).setFont(font).setFontSize(7))
                    .setPaddingLeft(3).setMinHeight(ROW_H));

            document.add(genTable);

            // ═══════════════════════════════════════════════
            // 3. SECCIÓN 1 – CONTROL DOCENTE
            //    Imagen: 8 columnas
            //    DOCENTE(20) | Nombre(28) | SI(8) | NO(8) | Cumple(8) | NoCumple(9) | SI(8) | NO(8) (total ~97, resto borde)
            //    Primero hay un header de sección span 8
            //    Luego fila de sub-encabezados: DOCENTE | nombre | [PRESENTE] | [HORARIO PROG] | [INTERACCIÓN]
            //    Luego ACTIVIDAD con checkboxes anidados
            //    Luego OBSERVACIONES span 8
            // ═══════════════════════════════════════════════
            // 8 columnas: label | nombre | SI | NO | Cumple | NoCumple | SI | NO
            Table s1 = createBaseTable(new float[]{14, 28, 7, 7, 8, 10, 7, 7}, font, 7);

            // Header sección
            s1.addCell(new Cell(1, 8)
                    .add(new Paragraph("1.   CONTROL DOCENTE (ASISTENCIA, HORARIO, COMPORTAMIENTO)")
                            .setFont(boldFont).setFontSize(8))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));

            // Sub-encabezados: DOCENTE | nombre | PRESENTE(2 cols) | HORARIO PROGRAMADO(2 cols) | INTERACCIÓN(2 cols)
            s1.addCell(new Cell(2, 1)
                    .add(new Paragraph("DOCENTE:").setFont(boldFont).setFontSize(7))
                    .setBackgroundColor(GRAY_BG).setPadding(2)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE).setMinHeight(ROW_H_TALL));

            String docenteNombre = visita.getDocente() != null
                    ? visita.getDocente().getNombres() + " " + visita.getDocente().getApellidos() : "";
            s1.addCell(new Cell(2, 1)
                    .add(new Paragraph(docenteNombre).setFont(font).setFontSize(7))
                    .setPaddingLeft(3).setVerticalAlignment(VerticalAlignment.MIDDLE).setMinHeight(ROW_H_TALL));

            // PRESENTE header (2 cols)
            s1.addCell(new Cell(1, 2)
                    .add(new Paragraph("PRESENTE").setFont(boldFont).setFontSize(7).setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));
            // HORARIO PROGRAMADO header (2 cols)
            s1.addCell(new Cell(1, 2)
                    .add(new Paragraph("HORARIO\nPROGRAMADO").setFont(boldFont).setFontSize(7).setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));
            // INTERACCIÓN header (2 cols)
            s1.addCell(new Cell(1, 2)
                    .add(new Paragraph("INTERACCIÓN").setFont(boldFont).setFontSize(7).setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));

            // Sub-fila de SI / NO / Cumple / NoCumple / SI / NO
            s1.addCell(makeSubHeaderCell("SI",         boldFont));
            s1.addCell(makeSubHeaderCell("NO",         boldFont));
            s1.addCell(makeSubHeaderCell("Cumple",     boldFont));
            s1.addCell(makeSubHeaderCell("No\nCumple", boldFont));
            s1.addCell(makeSubHeaderCell("SI",         boldFont));
            s1.addCell(makeSubHeaderCell("NO",         boldFont));

            // Fila ACTIVIDAD con checkboxes
            boolean pres = false, hor = false, intc = false;
            String act = "";
            if (visita.getEvaluacionControlDocente() != null) {
                act  = safeStr(visita.getEvaluacionControlDocente().getActividadDesarrollada());
                pres = Boolean.TRUE.equals(visita.getEvaluacionControlDocente().getDocentePresente());
                hor  = Boolean.TRUE.equals(visita.getEvaluacionControlDocente().getHorarioCumplido());
                intc = Boolean.TRUE.equals(visita.getEvaluacionControlDocente().getInteraccionAdecuada());
            }

            s1.addCell(new Cell()
                    .add(new Paragraph("ACTIVIDAD:").setFont(boldFont).setFontSize(7))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H_TALL));
            s1.addCell(new Cell()
                    .add(new Paragraph(act).setFont(font).setFontSize(7))
                    .setPaddingLeft(3).setMinHeight(ROW_H_TALL));
            s1.addCell(createCheckCell(pres,  font));
            s1.addCell(createCheckCell(!pres, font));
            s1.addCell(createCheckCell(hor,   font));
            s1.addCell(createCheckCell(!hor,  font));
            s1.addCell(createCheckCell(intc,  font));
            s1.addCell(createCheckCell(!intc, font));

            // OBSERVACIONES S1
            String obs1 = visita.getEvaluacionControlDocente() != null
                    ? safeStr(visita.getEvaluacionControlDocente().getObservaciones()) : "";
            String obs1Display = obs1.isEmpty() ? "No hay observaciones" : obs1;
            s1.addCell(new Cell(1, 8)
                    .add(new Paragraph("OBSERVACIONES: " + obs1Display).setFont(font).setFontSize(7))
                    .setPadding(2).setMinHeight(13));
            document.add(s1);

            // ═══════════════════════════════════════════════
            // 4. SECCIÓN 2 – MATERIAL VIRTUAL
            //    3 columnas: CUMPLE(25) | SI(37.5) | NO(37.5)
            // ═══════════════════════════════════════════════
            Table s2 = createBaseTable(new float[]{25, 37.5f, 37.5f}, font, 7);
            
            // Header sección 2
            s2.addCell(new Cell(1, 3)
                    .add(new Paragraph("2.   REGISTRO DE MATERIAL A UTILIZAR CARGADO EN AULA VIRTUAL ANTES DEL INICIO DE CLASES")
                            .setFont(boldFont).setFontSize(8))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));

            s2.addCell(new Cell().add(new Paragraph("CUMPLE").setFont(boldFont).setFontSize(7))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));
            s2.addCell(new Cell().add(new Paragraph("SI").setFont(boldFont).setFontSize(7)
                    .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(GRAY_BG).setTextAlignment(TextAlignment.CENTER).setPadding(2).setMinHeight(ROW_H));
            s2.addCell(new Cell().add(new Paragraph("NO").setFont(boldFont).setFontSize(7)
                    .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(GRAY_BG).setTextAlignment(TextAlignment.CENTER).setPadding(2).setMinHeight(ROW_H));

            boolean matC = visita.getEvaluacionMaterialVirtual() != null
                    && Boolean.TRUE.equals(visita.getEvaluacionMaterialVirtual().getCumple());
            s2.addCell(new Cell().setBackgroundColor(GRAY_BG).setMinHeight(ROW_H));
            s2.addCell(createCheckCell(matC,  font));
            s2.addCell(createCheckCell(!matC, font));

            String obs2 = visita.getEvaluacionMaterialVirtual() != null
                    ? safeStr(visita.getEvaluacionMaterialVirtual().getObservaciones()) : "";
            String obs2Display = obs2.isEmpty() ? "No hay observaciones" : obs2;
            s2.addCell(new Cell(1, 3)
                    .add(new Paragraph("OBSERVACIONES: " + obs2Display).setFont(font).setFontSize(7))
                    .setPadding(2).setMinHeight(13));
            document.add(s2);

            // ═══════════════════════════════════════════════
            // 5. SECCIÓN 3 – ASISTENCIA DE ESTUDIANTES
            //    Imagen: 7 columnas
            //    CONTROL ASISTENCIA (rowspan 3) |
            //    CONTROL EN AMBIENTE (colspan 3) | CONTROL EN INTRANET (colspan 3)
            //    Sub: Cumple | No cumple | Observaciones | Cumple | No cumple | Observaciones
            //    Fila datos: ASISTENCIA label | check | check | obs | check | check | obs
            // ═══════════════════════════════════════════════
            // 7 columnas: Control(13) | Cumple(12) | NoCumple(12) | Obs(13) | Cumple(12) | NoCumple(12) | Obs(13) = ~87, rest borders
            Table s3 = createBaseTable(new float[]{13, 12, 12, 13, 12, 12, 13}, font, 7);

            // Header sección 3
            s3.addCell(new Cell(1, 7)
                    .add(new Paragraph("3.   CONTROL DE REGISTRO DE ASISTENCIA DE ESTUDIANTES")
                            .setFont(boldFont).setFontSize(8))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));

            // Fila 1 de encabezado: celda rowspan-2 "CONTROL ASISTENCIA" + CONTROL EN AMBIENTE (3) + CONTROL EN INTRANET (3)
            s3.addCell(new Cell(2, 1)
                    .add(new Paragraph("CONTROL\nASISTENCIA").setFont(boldFont).setFontSize(7)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(GRAY_BG)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(2).setMinHeight(ROW_H_TALL));
            s3.addCell(new Cell(1, 3)
                    .add(new Paragraph("CONTROL EN AMBIENTE").setFont(boldFont).setFontSize(7)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));
            s3.addCell(new Cell(1, 3)
                    .add(new Paragraph("CONTROL EN INTRANET").setFont(boldFont).setFontSize(7)
                            .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));

            // Fila 2: sub-encabezados (6 celdas, la primera está cubierta por rowspan)
            for (String lbl : new String[]{"Cumple", "No cumple", "Observaciones", "Cumple", "No cumple", "Observaciones"}) {
                s3.addCell(new Cell()
                        .add(new Paragraph(lbl).setFont(boldFont).setFontSize(6.5f)
                                .setTextAlignment(TextAlignment.CENTER))
                        .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));
            }

            // Fila datos
            String ambCumple = "", ambObs = "", intCumple = "", intObs = "";
            if (visita.getEvaluacionAsistenciaEstudiantes() != null) {
                ambCumple = safeStr(visita.getEvaluacionAsistenciaEstudiantes().getAmbienteCumple());
                ambObs    = safeStr(visita.getEvaluacionAsistenciaEstudiantes().getAmbienteObservaciones());
                intCumple = safeStr(visita.getEvaluacionAsistenciaEstudiantes().getIntranetCumple());
                intObs    = safeStr(visita.getEvaluacionAsistenciaEstudiantes().getIntranetObservaciones());
            }

            // Label "ASISTENCIA" en col 1 (la tabla ya tiene rowspan corrido — aquí es col normal)
            s3.addCell(new Cell()
                    .add(new Paragraph("ASISTENCIA").setFont(boldFont).setFontSize(7))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));
            s3.addCell(createCheckCell("CUMPLE".equalsIgnoreCase(ambCumple) || "cumple".equalsIgnoreCase(ambCumple), font));
            s3.addCell(createCheckCell("NO_CUMPLE".equalsIgnoreCase(ambCumple) || "no_cumple".equalsIgnoreCase(ambCumple), font));
            s3.addCell(new Cell().add(new Paragraph(ambObs.isEmpty() ? "No hay observaciones" : ambObs).setFont(font).setFontSize(7))
                    .setPadding(2).setMinHeight(ROW_H));
            s3.addCell(createCheckCell("CUMPLE".equalsIgnoreCase(intCumple) || "cumple".equalsIgnoreCase(intCumple), font));
            s3.addCell(createCheckCell("NO_CUMPLE".equalsIgnoreCase(intCumple) || "no_cumple".equalsIgnoreCase(intCumple), font));
            s3.addCell(new Cell().add(new Paragraph(intObs.isEmpty() ? "No hay observaciones" : intObs).setFont(font).setFontSize(7))
                    .setPadding(2).setMinHeight(ROW_H));

            String obs3 = visita.getEvaluacionAsistenciaEstudiantes() != null
                    ? safeStr(visita.getEvaluacionAsistenciaEstudiantes().getObservacionesGenerales()) : "";
            String obs3Display = obs3.isEmpty() ? "No hay observaciones" : obs3;
            s3.addCell(new Cell(1, 6)
                    .add(new Paragraph("OBSERVACIONES: " + obs3Display).setFont(font).setFontSize(7))
                    .setPadding(2).setMinHeight(13));
            document.add(s3);

            // ═══════════════════════════════════════════════
            // 6. SECCIÓN 4 – AVANCE SILÁBICO
            //    3 columnas: descripción(60) | CUMPLE(20) | NO CUMPLE(20)
            // ═══════════════════════════════════════════════
            Table s4 = createBaseTable(new float[]{60, 20, 20}, font, 7);

            // Header sección 4
            s4.addCell(new Cell(1, 3)
                    .add(new Paragraph("4.   CONTROL DEL AVANCE SILÁBICO")
                            .setFont(boldFont).setFontSize(8))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));

            // Encabezado de columnas (primera celda vacía)
            s4.addCell(new Cell().setBackgroundColor(GRAY_BG).setMinHeight(ROW_H));
            s4.addCell(new Cell().add(new Paragraph("CUMPLE").setFont(boldFont).setFontSize(7)
                    .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));
            s4.addCell(new Cell().add(new Paragraph("NO CUMPLE").setFont(boldFont).setFontSize(7)
                    .setTextAlignment(TextAlignment.CENTER))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));

            String[] items4 = {
                "EL TEMA DEL SÍLABO COINCIDE CON LA CLASE DESARROLLADA EN LA FECHA DE LA VISITA",
                "EL TEMA DESARROLLADO EN LA FECHA ANTERIOR A LA VISITA COINCIDE CON EL SÍLABO",
                "INGRESO DEL AVANCE SILABICO EN EL AULA VIRTUAL"
            };
            Boolean[] chk4 = new Boolean[3];
            if (visita.getEvaluacionAvanceSilabico() != null) {
                chk4[0] = visita.getEvaluacionAvanceSilabico().getTemaCoincideVisita();
                chk4[1] = visita.getEvaluacionAvanceSilabico().getTemaCoincideAnterior();
                chk4[2] = visita.getEvaluacionAvanceSilabico().getIngresoAulaVirtual();
            }
            for (int i = 0; i < 3; i++) {
                s4.addCell(new Cell()
                        .add(new Paragraph(items4[i]).setFont(boldFont).setFontSize(7))
                        .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H_TALL));
                s4.addCell(createCheckCell(Boolean.TRUE.equals(chk4[i]),                       font));
                s4.addCell(createCheckCell(chk4[i] != null && !chk4[i],                        font));
            }
            String obs4 = visita.getEvaluacionAvanceSilabico() != null
                    ? safeStr(visita.getEvaluacionAvanceSilabico().getObservaciones()) : "";
            String obs4Display = obs4.isEmpty() ? "No hay observaciones" : obs4;
            s4.addCell(new Cell(1, 8)
                    .add(new Paragraph("OBSERVACIONES: " + obs4Display).setFont(font).setFontSize(7))
                    .setPadding(2).setMinHeight(13));
            document.add(s4);

            // ═══════════════════════════════════════════════
            // 7. SECCIÓN 5 – GUÍA DE PRÁCTICA
            //    4 columnas: descripción(50) | CUMPLE(17) | NO CUMPLE(17) | NO APLICA(16)
            // ═══════════════════════════════════════════════
            Table s5 = createBaseTable(new float[]{50, 17, 17, 16}, font, 7);

            // Header sección 5
            s5.addCell(new Cell(1, 4)
                    .add(new Paragraph("5.   CUMPLE CON EL DESARROLLO DE LA GUÍA DE PRÁCTICA")
                            .setFont(boldFont).setFontSize(8))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));

            s5.addCell(new Cell().setBackgroundColor(GRAY_BG).setMinHeight(ROW_H));
            for (String lbl : new String[]{"CUMPLE", "NO CUMPLE", "NO APLICA"}) {
                s5.addCell(new Cell().add(new Paragraph(lbl).setFont(boldFont).setFontSize(7)
                        .setTextAlignment(TextAlignment.CENTER))
                        .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));
            }

            String[] items5 = {
                "CUMPLE CON EL TEMA PROGRAMADO EN LA GUÍA DE PRÁCTICA PARA EL DESARROLLO DE LA CLASE PRÁCTICA",
                "SE EVIDENCIA EL LOGRO A MEDIR EN LA PRÁCTICA DESARROLLADA",
                "CUENTA CON UNA RÚBRICA DE EVALUACIÓN"
            };

            // Extraer valores enum de forma segura
            Boolean[] chk5Cumple   = new Boolean[3];
            Boolean[] chk5NoAplica = new Boolean[3];
            if (visita.getEvaluacionGuiaPractica() != null) {
                chk5Cumple[0]   = isEnumValue(visita.getEvaluacionGuiaPractica().getTemaProgramadoCumple(),   "CUMPLE");
                chk5Cumple[1]   = isEnumValue(visita.getEvaluacionGuiaPractica().getLogroEvidenciado(),       "CUMPLE");
                chk5Cumple[2]   = isEnumValue(visita.getEvaluacionGuiaPractica().getRubricaEvaluacion(),      "CUMPLE");
                chk5NoAplica[0] = isEnumValue(visita.getEvaluacionGuiaPractica().getTemaProgramadoCumple(),   "NO_APLICA");
                chk5NoAplica[1] = isEnumValue(visita.getEvaluacionGuiaPractica().getLogroEvidenciado(),       "NO_APLICA");
                chk5NoAplica[2] = isEnumValue(visita.getEvaluacionGuiaPractica().getRubricaEvaluacion(),      "NO_APLICA");
            }
            for (int i = 0; i < 3; i++) {
                boolean cumple   = Boolean.TRUE.equals(chk5Cumple[i]);
                boolean noAplica = Boolean.TRUE.equals(chk5NoAplica[i]);
                boolean noCumple = !cumple && !noAplica
                        && chk5Cumple[i] != null; // solo marca NO CUMPLE si hay valor definido

                s5.addCell(new Cell()
                        .add(new Paragraph(items5[i]).setFont(boldFont).setFontSize(7))
                        .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H_TALL));
                s5.addCell(createCheckCell(cumple,   font));
                s5.addCell(createCheckCell(noCumple, font));
                s5.addCell(createCheckCell(noAplica, font));
            }
            String obs5 = visita.getEvaluacionGuiaPractica() != null
                    ? safeStr(visita.getEvaluacionGuiaPractica().getObservaciones()) : "";
            String obs5Display = obs5.isEmpty() ? "No hay observaciones" : obs5;
            s5.addCell(new Cell(1, 8)
                    .add(new Paragraph("OBSERVACIONES: " + obs5Display).setFont(font).setFontSize(7))
                    .setPadding(2).setMinHeight(13));
            document.add(s5);

            // ═══════════════════════════════════════════════
            // 8. PIE: RESPONSABLE + REQUERIMIENTOS
            // ═══════════════════════════════════════════════
            Table footer = createBaseTable(new float[]{42, 58}, font, 7);
            footer.addCell(new Cell()
                    .add(new Paragraph("RESPONSABLE DE REALIZAR LA ACTIVIDAD:").setFont(boldFont).setFontSize(7))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));
            String resp = visita.getResponsable() != null
                    ? visita.getResponsable().getNombres() + " " + visita.getResponsable().getApellidos() : "";
            footer.addCell(new Cell()
                    .add(new Paragraph(resp).setFont(font).setFontSize(7))
                    .setPadding(2).setMinHeight(ROW_H));
            document.add(footer);

            // Tabla de REQUERIMIENTOS
            Table requerimientos = createBaseTable(new float[]{100}, font, 7);
            requerimientos.addCell(new Cell()
                    .add(new Paragraph("REQUERIMIENTOS SOLICITADOS EN LA VISITA INOPINADA:")
                            .setFont(boldFont).setFontSize(7))
                    .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));

            // Iterar sobre los requerimientos y agregarlos
            if (visita.getRequerimientos() != null && !visita.getRequerimientos().isEmpty()) {
                for (int i = 0; i < visita.getRequerimientos().size(); i++) {
                    String reqDesc = safeStr(visita.getRequerimientos().get(i).getDescripcion());
                    requerimientos.addCell(new Cell()
                            .add(new Paragraph((i + 1) + ". " + reqDesc).setFont(font).setFontSize(7))
                            .setPadding(2).setMinHeight(13));
                }
            } else {
                requerimientos.addCell(new Cell()
                        .add(new Paragraph("Sin requerimientos registrados").setFont(font).setFontSize(7))
                        .setPadding(2).setMinHeight(13));
            }
            document.add(requerimientos);

            // ═══════════════════════════════════════════════
            // 9. FIRMAS
            // ═══════════════════════════════════════════════
            Table sigTable = createBaseTable(new float[]{50, 50}, font, 8);
            sigTable.setBorder(Border.NO_BORDER);
            sigTable.setMarginTop(2);

            sigTable.addCell(createSignatureCell(visita.getFirmaDocenteHash(), "FIRMA DEL DOCENTE", font, boldFont));
            sigTable.addCell(createSignatureCell(visita.getFirmaResponsableHash(), "FIRMA DEL RESPONSABLE DE LA VISITA", font, boldFont));

            document.add(sigTable);

            // Tabla de EVIDENCIA
            if (visita.getEvidenciaImagenHash() != null && !visita.getEvidenciaImagenHash().trim().isEmpty()) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));

                Table evidenceTable = createBaseTable(new float[]{100}, font, 7);
                evidenceTable.addCell(new Cell()
                        .add(new Paragraph("EVIDENCIA FOTOGRÁFICA").setFont(boldFont).setFontSize(7))
                        .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));

                try {
                    String cleanB64 = visita.getEvidenciaImagenHash().trim();
                    if (cleanB64.startsWith("\"") && cleanB64.endsWith("\"")) {
                        cleanB64 = cleanB64.substring(1, cleanB64.length() - 1);
                    }
                    if (cleanB64.contains(",")) {
                        cleanB64 = cleanB64.split(",")[1];
                    }
                    cleanB64 = cleanB64.replaceAll("\\s", "");
                    byte[] imgBytes = Base64.getDecoder().decode(cleanB64);
                    Image img = new Image(ImageDataFactory.create(imgBytes));
                    img.setAutoScale(true);
                    img.setMaxWidth(420);
                    img.setMaxHeight(220);
                    img.setHorizontalAlignment(HorizontalAlignment.CENTER);
                    evidenceTable.addCell(new Cell()
                            .add(img)
                            .setPadding(4)
                            .setBorder(Border.NO_BORDER));
                } catch (Exception e) {
                    evidenceTable.addCell(new Cell()
                            .add(new Paragraph("No se pudo cargar la imagen de evidencia").setFont(font).setFontSize(7))
                            .setPadding(2)
                            .setMinHeight(ROW_H));
                }
                document.add(evidenceTable);
            }

            // ═══════════════════════════════════════════════
            // 10. PIE OFICIAL (fijo abajo)
            // ═══════════════════════════════════════════════
            Paragraph officialFooter = new Paragraph("VRA-FR-040          V.2.0          26/09/2025")
                    .setFont(font).setFontSize(6).setTextAlignment(TextAlignment.LEFT);
            document.showTextAligned(officialFooter, 18, 12, TextAlignment.LEFT);

        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF VRA-FR-040: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    public byte[] generarPdfVisitas(List<VisitaInopinadaEntity> visitas) throws Exception {
        if (visitas == null || visitas.isEmpty()) {
            throw new IllegalArgumentException("No hay visitas para generar el PDF");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument mergedPdf = new PdfDocument(writer)) {

            for (VisitaInopinadaEntity visita : visitas) {
                byte[] visitaPdf = generarPdfVisita(visita.getId());
                try (PdfDocument sourcePdf = new PdfDocument(new PdfReader(new ByteArrayInputStream(visitaPdf)))) {
                    sourcePdf.copyPagesTo(1, sourcePdf.getNumberOfPages(), mergedPdf);
                }
            }
        }

        return baos.toByteArray();
    }

    // ═══════════════════════════════════════════════
    // MÉTODOS AUXILIARES
    // ═══════════════════════════════════════════════

    /** Tabla base con bordes sólidos finos y ancho 100 %. */
    private Table createBaseTable(float[] colWidthsPercent, PdfFont font, int fontSize) {
        Table t = new Table(UnitValue.createPercentArray(colWidthsPercent));
        t.setWidth(UnitValue.createPercentValue(100));
        t.setFont(font).setFontSize(fontSize);
        t.setMarginBottom(0).setMarginTop(0);
        return t;
    }

    /** Celda de etiqueta con fondo gris. */
    private void addLabelCell(Table table, String text, PdfFont boldFont) {
        table.addCell(new Cell()
                .add(new Paragraph(text).setFont(boldFont).setFontSize(7))
                .setBackgroundColor(GRAY_BG).setPadding(2).setMinHeight(ROW_H));
    }

    /** Celda de valor sin fondo. */
    private void addValueCell(Table table, String text, PdfFont font) {
        table.addCell(new Cell()
                .add(new Paragraph(text).setFont(font).setFontSize(7))
                .setPaddingLeft(3).setMinHeight(ROW_H));
    }

    /** Celda de sub-encabezado (gris, centrado, letra pequeña). */
    private Cell makeSubHeaderCell(String text, PdfFont boldFont) {
        return new Cell()
                .add(new Paragraph(text).setFont(boldFont).setFontSize(6.5f)
                        .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(GRAY_BG).setPadding(1).setMinHeight(ROW_H);
    }

    /** Celda con "X" centrado si checked es true, vacía si false. */
    private Cell createCheckCell(Boolean checked, PdfFont font) {
        Cell c = new Cell()
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setPadding(1).setMinHeight(ROW_H);
        if (Boolean.TRUE.equals(checked)) {
            c.add(new Paragraph(CHECK_MARK).setFont(font).setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER));
        }
        return c;
    }

    private void addCenteredParagraph(Document doc, String text, PdfFont font, float size) {
        doc.add(new Paragraph(text).setFont(font).setFontSize(size)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(0).setMarginTop(0));
    }

    /** Crea una celda de firma con la imagen (si existe) y la línea de firma. */
    private Cell createSignatureCell(String b64, String label, PdfFont font, PdfFont boldFont) {
        Cell cell = new Cell().setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.CENTER);
        cell.setMinHeight(35); // Garantizar espacio para la firma

        if (b64 != null && !b64.isEmpty()) {
            try {
                // Limpiar la cadena Base64: quitar comillas, espacios y prefijos
                String cleanB64 = b64.trim();
                if (cleanB64.startsWith("\"") && cleanB64.endsWith("\"")) {
                    cleanB64 = cleanB64.substring(1, cleanB64.length() - 1);
                }
                if (cleanB64.contains(",")) {
                    cleanB64 = cleanB64.split(",")[1];
                }
                cleanB64 = cleanB64.replaceAll("\\s", ""); // Quitar cualquier espacio o salto de línea

                byte[] imgBytes = Base64.getDecoder().decode(cleanB64);
                Image img = new Image(ImageDataFactory.create(imgBytes));
                img.setMaxWidth(100);
                img.setMaxHeight(30);
                img.setHorizontalAlignment(HorizontalAlignment.CENTER);
                cell.add(img);
            } catch (Exception e) {
                // Si falla la decodificación, imprimimos error y dejamos el espacio
                System.err.println("Error decodificando firma para " + label + ": " + e.getMessage());
            }
        }

        cell.add(new Paragraph("__________________________")
                .setFont(font).setFontSize(8).setMarginTop(0));
        cell.add(new Paragraph(label)
                .setFont(boldFont).setFontSize(7).setMarginTop(-2));
        
        return cell;
    }

    /** Evalúa si un enum (pasado como Object) tiene el nombre indicado. */
    private boolean isEnumValue(Object enumVal, String name) {
        if (enumVal == null) return false;
        return ((Enum<?>) enumVal).name().equalsIgnoreCase(name);
    }

    private String safeDate(java.time.LocalDate d)  { return d != null ? d.format(DATE_FMT) : ""; }
    private String safeTime(java.time.LocalTime t)   { return t != null ? t.format(TIME_FMT) : ""; }
    private String safeStr(String s)                  { return s != null ? s : ""; }
}