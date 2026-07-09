DROP DATABASE IF EXISTS db_visitas_inopinadas;
CREATE DATABASE db_visitas_inopinadas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE db_visitas_inopinadas;

SET FOREIGN_KEY_CHECKS = 0;

-- ============================================================================
-- 2.1 TABLAS MAESTRAS Y CATÁLOGOS
-- ============================================================================

CREATE TABLE Rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol ENUM('ADMIN', 'AUDITOR', 'DOCENTE') NOT NULL UNIQUE
);

CREATE TABLE Universidad (
    id_universidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL DEFAULT 'UNIVERSIDAD PRIVADA...',
    vicerrectorado VARCHAR(255) DEFAULT 'VICERRECTORADO ACADÉMICO',
    facultad VARCHAR(255) DEFAULT 'FACULTAD DE INGENIERÍAS',
    escuela_profesional VARCHAR(255) DEFAULT 'ESCUELA PROFESIONAL DE INGENIERÍA DE SISTEMAS',
    codigo_formulario VARCHAR(100) DEFAULT 'VRA-FR-040',
    version VARCHAR(50) DEFAULT 'V.2.0',
    fecha_version DATE DEFAULT '2025-09-26'
);

CREATE TABLE Sede (
    id_sede INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    id_universidad INT DEFAULT 1,
    FOREIGN KEY (id_universidad) REFERENCES Universidad(id_universidad) ON DELETE SET NULL
);

CREATE TABLE Docente (
    id_docente INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(255) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    estado_activo BOOLEAN DEFAULT TRUE,
    INDEX idx_docente_nombre (apellidos, nombres)
);

CREATE TABLE Asignatura (
    id_asignatura INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    campo_formativo VARCHAR(255),
    ciclo_academico VARCHAR(50),
    turno VARCHAR(50),
    tipo_horario VARCHAR(50)
);

CREATE TABLE ResponsableVisita (
    id_responsable INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(255) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    cargo VARCHAR(255),
    email VARCHAR(255) UNIQUE
);

-- ============================================================================
-- 2.2 GESTIÓN DE USUARIOS (con columna firma_hash integrada)
-- ============================================================================

CREATE TABLE UsuarioSistema (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombres VARCHAR(255) NOT NULL,
    apellidos VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    id_docente INT NULL,
    id_responsable INT NULL,
    estado BOOLEAN DEFAULT TRUE,
    firma_hash LONGTEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol) ON DELETE RESTRICT,
    FOREIGN KEY (id_docente) REFERENCES Docente(id_docente) ON DELETE SET NULL,
    FOREIGN KEY (id_responsable) REFERENCES ResponsableVisita(id_responsable) ON DELETE SET NULL
);

-- ============================================================================
-- 2.3 TABLA PRINCIPAL: VISITA INOPINADA (con hashes en TEXT)
-- ============================================================================

CREATE TABLE VisitaInopinada (
    id_visita INT AUTO_INCREMENT PRIMARY KEY,
    fecha_visita DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_termino TIME NOT NULL,
    semana_numero INT,
    lugar_visita VARCHAR(255),
    tipo_clase ENUM('TEORICA', 'PRACTICA', 'MIXTA') DEFAULT 'TEORICA',
    
    id_sede INT,
    id_docente INT,
    id_asignatura INT,
    id_responsable INT,
    id_usuario_auditor INT,
    
    estado_visita ENUM('BORRADOR', 'FIRMADA_DOCENTE', 'COMPLETADA', 'AUDITADA') DEFAULT 'BORRADOR',
    
    firma_docente_hash LONGTEXT NULL,
    firma_responsable_hash LONGTEXT NULL,
    evidencia_imagen_hash LONGTEXT NULL,
    fecha_firma_docente DATETIME NULL,
    fecha_firma_responsable DATETIME NULL,
    
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (id_sede) REFERENCES Sede(id_sede) ON DELETE SET NULL,
    FOREIGN KEY (id_docente) REFERENCES Docente(id_docente) ON DELETE RESTRICT,
    FOREIGN KEY (id_asignatura) REFERENCES Asignatura(id_asignatura) ON DELETE RESTRICT,
    FOREIGN KEY (id_responsable) REFERENCES ResponsableVisita(id_responsable) ON DELETE SET NULL,
    FOREIGN KEY (id_usuario_auditor) REFERENCES UsuarioSistema(id_usuario) ON DELETE SET NULL
);

-- ============================================================================
-- 2.4 TABLAS DE EVALUACIÓN (Hijas 1:1)
-- ============================================================================

CREATE TABLE EvaluacionControlDocente (
    id_evaluacion INT AUTO_INCREMENT PRIMARY KEY,
    id_visita INT UNIQUE NOT NULL,
    docente_presente BOOLEAN DEFAULT FALSE,
    horario_cumplido BOOLEAN DEFAULT FALSE,
    interaccion_adecuada BOOLEAN DEFAULT FALSE,
    actividad_desarrollada TEXT,
    observaciones TEXT,
    FOREIGN KEY (id_visita) REFERENCES VisitaInopinada(id_visita) ON DELETE CASCADE
);

CREATE TABLE EvaluacionMaterialVirtual (
    id_evaluacion INT AUTO_INCREMENT PRIMARY KEY,
    id_visita INT UNIQUE NOT NULL, 
    cumple BOOLEAN DEFAULT FALSE,
    observaciones TEXT,
    FOREIGN KEY (id_visita) REFERENCES VisitaInopinada(id_visita) ON DELETE CASCADE
);

CREATE TABLE EvaluacionAsistenciaEstudiantes (
    id_evaluacion INT AUTO_INCREMENT PRIMARY KEY,
    id_visita INT UNIQUE NOT NULL,
    ambiente_cumple ENUM('CUMPLE', 'NO_CUMPLE') DEFAULT NULL,
    ambiente_observaciones TEXT,
    intranet_cumple ENUM('CUMPLE', 'NO_CUMPLE') DEFAULT NULL,
    intranet_observaciones TEXT,
    observaciones_generales TEXT,
    FOREIGN KEY (id_visita) REFERENCES VisitaInopinada(id_visita) ON DELETE CASCADE
);

CREATE TABLE EvaluacionAvanceSilabico (
    id_evaluacion INT AUTO_INCREMENT PRIMARY KEY,
    id_visita INT UNIQUE NOT NULL,
    tema_coincide_visita BOOLEAN DEFAULT FALSE,
    tema_coincide_anterior BOOLEAN DEFAULT FALSE,
    ingreso_aula_virtual BOOLEAN DEFAULT FALSE,
    cumple BOOLEAN GENERATED ALWAYS AS (tema_coincide_visita AND tema_coincide_anterior AND ingreso_aula_virtual) STORED,
    observaciones TEXT,
    FOREIGN KEY (id_visita) REFERENCES VisitaInopinada(id_visita) ON DELETE CASCADE
);

CREATE TABLE EvaluacionGuiaPractica (
    id_evaluacion INT AUTO_INCREMENT PRIMARY KEY,
    id_visita INT UNIQUE NOT NULL,
    tema_programado_cumple ENUM('CUMPLE', 'NO_CUMPLE', 'NO_APLICA') DEFAULT 'NO_APLICA',
    logro_evidenciado ENUM('CUMPLE', 'NO_CUMPLE', 'NO_APLICA') DEFAULT 'NO_APLICA',
    rubrica_evaluacion ENUM('CUMPLE', 'NO_CUMPLE', 'NO_APLICA') DEFAULT 'NO_APLICA',
    observaciones TEXT,
    FOREIGN KEY (id_visita) REFERENCES VisitaInopinada(id_visita) ON DELETE CASCADE
);

-- ============================================================================
-- 2.5 REQUERIMIENTOS (Hija 1:N)
-- ============================================================================

CREATE TABLE RequerimientoVisita (
    id_requerimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_visita INT NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_solicitud DATE DEFAULT (CURDATE()),
    estado ENUM('PENDIENTE', 'EN_PROCESO', 'ATENDIDO', 'RECHAZADO') DEFAULT 'PENDIENTE',
    respuesta TEXT,
    fecha_respuesta DATE NULL,
    FOREIGN KEY (id_visita) REFERENCES VisitaInopinada(id_visita) ON DELETE CASCADE
);

-- ============================================================================
-- 2.6 EVIDENCIAS DE REQUERIMIENTOS (Hija 1:N)
-- ============================================================================

CREATE TABLE EvidenciaRequerimiento (
    id_evidencia INT AUTO_INCREMENT PRIMARY KEY,
    id_requerimiento INT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    tipo_archivo VARCHAR(100) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tamaño_bytes BIGINT DEFAULT 0,
    descripcion TEXT,
    fecha_carga DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_requerimiento) REFERENCES RequerimientoVisita(id_requerimiento) ON DELETE CASCADE
);

-- Reactivar verificación de FK
SET FOREIGN_KEY_CHECKS = 1;

USE db_visitas_inopinadas;

-- ============================================================================
-- 3. INSERCIÓN DE CATÁLOGOS BÁSICOS
-- ============================================================================

INSERT INTO Rol (nombre_rol) VALUES 
('ADMIN'), 
('AUDITOR'), 
('DOCENTE');

INSERT INTO Universidad (
    nombre, vicerrectorado, facultad, escuela_profesional, 
    codigo_formulario, version, fecha_version
) VALUES (
    'UNIVERSIDAD PRIVADA SAN PEDRO', 'VICERRECTORADO ACADÉMICO', 
    'FACULTAD DE INGENIERÍAS', 'ESCUELA PROFESIONAL DE INGENIERÍA DE SISTEMAS', 
    'VRA-FR-040', 'V.2.0', '2025-09-26'
);

INSERT INTO Sede (nombre, id_universidad) VALUES 
('LIMA-CHORRILLOS', 1),
('LIMA-CENTRO', 1),
('AREQUIPA', 1);

INSERT INTO Asignatura (nombre, campo_formativo, ciclo_academico, turno, tipo_horario) VALUES
('INGENIERÍA DE SOFTWARE I', 'FORMACIÓN ESPECIALIZADA', 'VI CICLO', 'NOCHE', 'TEORIA'),
('BASE DE DATOS II', 'FORMACIÓN ESPECIALIZADA', 'V CICLO', 'TARDE', 'PRACTICA'),
('GESTIÓN DE PROYECTOS TI', 'FORMACIÓN GENERAL', 'VII CICLO', 'NOCHE', 'TEORIA'),
('DESARROLLO WEB FULLSTACK', 'FORMACIÓN ESPECIALIZADA', 'VIII CICLO', 'SABADO', 'PRACTICA');

-- ============================================================================
-- 4. INSERCIÓN DE ENTIDADES ACADÉMICAS Y USUARIOS
-- ============================================================================

INSERT INTO Docente (nombres, apellidos, email, estado_activo) VALUES
('MIGUEL ANGEL', 'HUERTA ROJAS', 'm.huerta@universidad.edu.pe', TRUE);

INSERT INTO ResponsableVisita (nombres, apellidos, cargo, email) VALUES
('VICTOR', 'GUADALUPE MORI', 'VICERRECTOR ACADÉMICO', 'v.guadalupe@universidad.edu.pe');

-- Usuarios (Contraseña prueba: 123456 | Hash: $2a$10$JnWtYwpitWzonOVCuqGWvuzTGCJVJFHyNXks5d.BkQTaJxmBU5cMS)
INSERT INTO UsuarioSistema (email, password_hash, nombres, apellidos, id_rol, id_docente, id_responsable, estado, firma_hash) VALUES
('admin@universidad.edu.pe', '$2a$10$JnWtYwpitWzonOVCuqGWvuzTGCJVJFHyNXks5d.BkQTaJxmBU5cMS', 'ADMINISTRADOR', 'SISTEMAS', 1, NULL, NULL, TRUE, NULL),
('v.guadalupe@universidad.edu.pe', '$2a$10$JnWtYwpitWzonOVCuqGWvuzTGCJVJFHyNXks5d.BkQTaJxmBU5cMS', 'VICTOR', 'GUADALUPE MORI', 2, NULL, 1, TRUE, NULL),
('m.huerta@universidad.edu.pe', '$2a$10$JnWtYwpitWzonOVCuqGWvuzTGCJVJFHyNXks5d.BkQTaJxmBU5cMS', 'MIGUEL ANGEL', 'HUERTA ROJAS', 3, 1, NULL, TRUE, NULL);


-- datos nuevos para "Visitas inopinadas"

-- Visita 1 (Abril - Semana 2)
INSERT INTO VisitaInopinada (fecha_visita, hora_inicio, hora_termino, semana_numero, lugar_visita, tipo_clase, id_sede, id_docente, id_asignatura, id_responsable, id_usuario_auditor, estado_visita, fecha_registro)
VALUES ('2026-04-15', '09:00:00', '09:18:00', 2, 'Aula 302 - Pabellón A', 'TEORICA', 2, 1, 1, 1, 2, 'COMPLETADA', '2026-04-15 09:20:00');
SET @visita_id_1 = LAST_INSERT_ID();

INSERT INTO EvaluacionControlDocente (id_visita, docente_presente, horario_cumplido, interaccion_adecuada, actividad_desarrollada, observaciones)
VALUES (@visita_id_1, TRUE, TRUE, TRUE, 'Desarrollo de teoría sobre diseño de software y diagramas de arquitectura.', 'El docente llegó a tiempo y resolvió dudas de los estudiantes.');

INSERT INTO EvaluacionMaterialVirtual (id_visita, cumple, observaciones)
VALUES (@visita_id_1, TRUE, 'Material de la sesión cargado 24 horas antes.');

INSERT INTO EvaluacionAsistenciaEstudiantes (id_visita, ambiente_cumple, ambiente_observaciones, intranet_cumple, intranet_observaciones, observaciones_generales)
VALUES (@visita_id_1, 'CUMPLE', '24 alumnos presentes de 30 inscritos.', 'CUMPLE', 'Asistencia registrada en los primeros 10 minutos.', 'Todo conforme en el aula.');

INSERT INTO EvaluacionAvanceSilabico (id_visita, tema_coincide_visita, tema_coincide_anterior, ingreso_aula_virtual, observaciones)
VALUES (@visita_id_1, TRUE, TRUE, TRUE, 'Coincide con la Unidad I del sílabo.');

INSERT INTO EvaluacionGuiaPractica (id_visita, tema_programado_cumple, logro_evidenciado, rubrica_evaluacion, observaciones)
VALUES (@visita_id_1, 'NO_APLICA', 'NO_APLICA', 'NO_APLICA', 'Clase puramente teórica.');


-- Visita 2 (Abril - Semana 4)
INSERT INTO VisitaInopinada (fecha_visita, hora_inicio, hora_termino, semana_numero, lugar_visita, tipo_clase, id_sede, id_docente, id_asignatura, id_responsable, id_usuario_auditor, estado_visita, fecha_registro)
VALUES ('2026-04-29', '15:30:00', '15:48:00', 4, 'Laboratorio de Computación 2', 'PRACTICA', 1, 1, 2, 1, 2, 'COMPLETADA', '2026-04-29 15:50:00');
SET @visita_id_2 = LAST_INSERT_ID();

INSERT INTO EvaluacionControlDocente (id_visita, docente_presente, horario_cumplido, interaccion_adecuada, actividad_desarrollada, observaciones)
VALUES (@visita_id_2, TRUE, TRUE, TRUE, 'Práctica calificada guiada en el diseño de esquemas relacionales.', 'Estudiantes interactúan activamente con el docente.');

INSERT INTO EvaluacionMaterialVirtual (id_visita, cumple, observaciones)
VALUES (@visita_id_2, TRUE, 'Guía de práctica cargada en el aula virtual.');

INSERT INTO EvaluacionAsistenciaEstudiantes (id_visita, ambiente_cumple, ambiente_observaciones, intranet_cumple, intranet_observaciones, observaciones_generales)
VALUES (@visita_id_2, 'CUMPLE', '18 alumnos en laboratorio.', 'CUMPLE', 'Conforme en intranet.', 'Buen comportamiento de los alumnos.');

INSERT INTO EvaluacionAvanceSilabico (id_visita, tema_coincide_visita, tema_coincide_anterior, ingreso_aula_virtual, observaciones)
VALUES (@visita_id_2, TRUE, TRUE, TRUE, 'Tema de normalización de BD coincide.');

INSERT INTO EvaluacionGuiaPractica (id_visita, tema_programado_cumple, logro_evidenciado, rubrica_evaluacion, observaciones)
VALUES (@visita_id_2, 'CUMPLE', 'CUMPLE', 'CUMPLE', 'Se utilizó la rúbrica oficial UPN.');


-- Visita 3 (Mayo - Semana 6)
INSERT INTO VisitaInopinada (fecha_visita, hora_inicio, hora_termino, semana_numero, lugar_visita, tipo_clase, id_sede, id_docente, id_asignatura, id_responsable, id_usuario_auditor, estado_visita, fecha_registro)
VALUES ('2026-05-13', '19:15:00', '19:32:00', 6, 'Aula 104', 'TEORICA', 3, 1, 3, 1, 2, 'COMPLETADA', '2026-05-13 19:35:00');
SET @visita_id_3 = LAST_INSERT_ID();

INSERT INTO EvaluacionControlDocente (id_visita, docente_presente, horario_cumplido, interaccion_adecuada, actividad_desarrollada, observaciones)
VALUES (@visita_id_3, TRUE, TRUE, TRUE, 'Clase de metodologías ágiles y estimación de costos en proyectos TI.', 'Interacción dinámica con preguntas rápidas.');

INSERT INTO EvaluacionMaterialVirtual (id_visita, cumple, observaciones)
VALUES (@visita_id_3, TRUE, 'Diapositivas y material de lectura disponibles.');

INSERT INTO EvaluacionAsistenciaEstudiantes (id_visita, ambiente_cumple, ambiente_observaciones, intranet_cumple, intranet_observaciones, observaciones_generales)
VALUES (@visita_id_3, 'CUMPLE', '28 alumnos en aula física.', 'CUMPLE', 'Registrado a tiempo.', 'Sin inconvenientes.');

INSERT INTO EvaluacionAvanceSilabico (id_visita, tema_coincide_visita, tema_coincide_anterior, ingreso_aula_virtual, observaciones)
VALUES (@visita_id_3, TRUE, TRUE, TRUE, 'Sílabo coincide.');

INSERT INTO EvaluacionGuiaPractica (id_visita, tema_programado_cumple, logro_evidenciado, rubrica_evaluacion, observaciones)
VALUES (@visita_id_3, 'NO_APLICA', 'NO_APLICA', 'NO_APLICA', 'Clase teórica.');


-- Visita 4 (Mayo - Semana 8)
INSERT INTO VisitaInopinada (fecha_visita, hora_inicio, hora_termino, semana_numero, lugar_visita, tipo_clase, id_sede, id_docente, id_asignatura, id_responsable, id_usuario_auditor, estado_visita, fecha_registro)
VALUES ('2026-05-27', '08:45:00', '09:03:00', 8, 'Laboratorio de Computación 3', 'PRACTICA', 2, 1, 4, 1, 2, 'COMPLETADA', '2026-05-27 09:05:00');
SET @visita_id_4 = LAST_INSERT_ID();

INSERT INTO EvaluacionControlDocente (id_visita, docente_presente, horario_cumplido, interaccion_adecuada, actividad_desarrollada, observaciones)
VALUES (@visita_id_4, TRUE, TRUE, TRUE, 'Explicación práctica de enrutamiento dinámico en Next.js y API routes.', 'Estudiantes codifican al mismo tiempo que el docente.');

INSERT INTO EvaluacionMaterialVirtual (id_visita, cumple, observaciones)
VALUES (@visita_id_4, TRUE, 'Repositorio de GitHub compartido con antelación.');

INSERT INTO EvaluacionAsistenciaEstudiantes (id_visita, ambiente_cumple, ambiente_observaciones, intranet_cumple, intranet_observaciones, observaciones_generales)
VALUES (@visita_id_4, 'CUMPLE', '20 estudiantes presentes en laboratorio.', 'CUMPLE', 'Conforme en intranet.', 'Uso adecuado de los equipos.');

INSERT INTO EvaluacionAvanceSilabico (id_visita, tema_coincide_visita, tema_coincide_anterior, ingreso_aula_virtual, observaciones)
VALUES (@visita_id_4, TRUE, TRUE, TRUE, 'Avance silábico al día.');

INSERT INTO EvaluacionGuiaPractica (id_visita, tema_programado_cumple, logro_evidenciado, rubrica_evaluacion, observaciones)
VALUES (@visita_id_4, 'CUMPLE', 'CUMPLE', 'CUMPLE', 'Guía de práctica número 8 completada.');


-- Visita 5 (Junio - Semana 10)
INSERT INTO VisitaInopinada (fecha_visita, hora_inicio, hora_termino, semana_numero, lugar_visita, tipo_clase, id_sede, id_docente, id_asignatura, id_responsable, id_usuario_auditor, estado_visita, fecha_registro)
VALUES ('2026-06-10', '10:00:00', '10:18:00', 10, 'Aula 201 - Pabellón B', 'TEORICA', 1, 1, 1, 1, 2, 'COMPLETADA', '2026-06-10 10:20:00');
SET @visita_id_5 = LAST_INSERT_ID();

INSERT INTO EvaluacionControlDocente (id_visita, docente_presente, horario_cumplido, interaccion_adecuada, actividad_desarrollada, observaciones)
VALUES (@visita_id_5, TRUE, TRUE, TRUE, 'Teoría sobre patrones de diseño Creacionales y Estructurales.', 'Participación fluida de los alumnos.');

INSERT INTO EvaluacionMaterialVirtual (id_visita, cumple, observaciones)
VALUES (@visita_id_5, TRUE, 'Lecturas complementarias subidas al Blackboard.');

INSERT INTO EvaluacionAsistenciaEstudiantes (id_visita, ambiente_cumple, ambiente_observaciones, intranet_cumple, intranet_observaciones, observaciones_generales)
VALUES (@visita_id_5, 'CUMPLE', '22 alumnos.', 'CUMPLE', 'Lista firmada de asistencia física e intranet.', 'Todo ordenado.');

INSERT INTO EvaluacionAvanceSilabico (id_visita, tema_coincide_visita, tema_coincide_anterior, ingreso_aula_virtual, observaciones)
VALUES (@visita_id_5, TRUE, TRUE, TRUE, 'Unidad III del sílabo.');

INSERT INTO EvaluacionGuiaPractica (id_visita, tema_programado_cumple, logro_evidenciado, rubrica_evaluacion, observaciones)
VALUES (@visita_id_5, 'NO_APLICA', 'NO_APLICA', 'NO_APLICA', 'Clase teórica.');


-- Visita 6 (Junio - Semana 12)
INSERT INTO VisitaInopinada (fecha_visita, hora_inicio, hora_termino, semana_numero, lugar_visita, tipo_clase, id_sede, id_docente, id_asignatura, id_responsable, id_usuario_auditor, estado_visita, fecha_registro)
VALUES ('2026-06-17', '16:00:00', '16:19:00', 12, 'Laboratorio de Cómputo 1', 'PRACTICA', 3, 1, 2, 1, 2, 'COMPLETADA', '2026-06-17 16:22:00');
SET @visita_id_6 = LAST_INSERT_ID();

INSERT INTO EvaluacionControlDocente (id_visita, docente_presente, horario_cumplido, interaccion_adecuada, actividad_desarrollada, observaciones)
VALUES (@visita_id_6, TRUE, TRUE, TRUE, 'Laboratorio de optimización de consultas SQL (tuning) y creación de índices.', 'El docente guió de manera proactiva a los grupos.');

INSERT INTO EvaluacionMaterialVirtual (id_visita, cumple, observaciones)
VALUES (@visita_id_6, TRUE, 'Base de datos de prueba compartida 48 horas antes.');

INSERT INTO EvaluacionAsistenciaEstudiantes (id_visita, ambiente_cumple, ambiente_observaciones, intranet_cumple, intranet_observaciones, observaciones_generales)
VALUES (@visita_id_6, 'CUMPLE', '15 alumnos presentes de 20.', 'CUMPLE', 'Conforme en intranet.', 'Correcto.');

INSERT INTO EvaluacionAvanceSilabico (id_visita, tema_coincide_visita, tema_coincide_anterior, ingreso_aula_virtual, observaciones)
VALUES (@visita_id_6, TRUE, TRUE, TRUE, 'Optimización de base de datos.');

INSERT INTO EvaluacionGuiaPractica (id_visita, tema_programado_cumple, logro_evidenciado, rubrica_evaluacion, observaciones)
VALUES (@visita_id_6, 'CUMPLE', 'CUMPLE', 'CUMPLE', 'Rúbrica de la UPN utilizada.');

-- Requerimientos de prueba para enriquecer el panel
INSERT INTO RequerimientoVisita (id_visita, descripcion, fecha_solicitud, estado)
VALUES (@visita_id_2, 'Actualizar la guía de prácticas número 4 con ejemplos de BD NoSQL.', '2026-04-29', 'PENDIENTE');

INSERT INTO RequerimientoVisita (id_visita, descripcion, fecha_solicitud, estado)
VALUES (@visita_id_4, 'Subir el enlace del repositorio GitHub de la clase al aula virtual.', '2026-05-27', 'ATENDIDO');


