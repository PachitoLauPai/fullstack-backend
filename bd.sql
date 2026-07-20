DROP DATABASE IF EXISTS defaultdb;
CREATE DATABASE defaultdb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE defaultdb;

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

CREATE TABLE Asignatura (
    id_asignatura INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    campo_formativo VARCHAR(255),
    ciclo_academico VARCHAR(50),
    turno VARCHAR(50),
    tipo_horario VARCHAR(50)
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
    dni VARCHAR(20) UNIQUE NULL,
    cargo VARCHAR(255) NULL,
    estado BOOLEAN DEFAULT TRUE,
    firma_hash LONGTEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol) ON DELETE RESTRICT,
    INDEX idx_usuariosistema_nombre (apellidos, nombres)
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
    FOREIGN KEY (id_docente) REFERENCES UsuarioSistema(id_usuario) ON DELETE RESTRICT,
    FOREIGN KEY (id_asignatura) REFERENCES Asignatura(id_asignatura) ON DELETE RESTRICT,
    FOREIGN KEY (id_responsable) REFERENCES UsuarioSistema(id_usuario) ON DELETE SET NULL,
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

-- Usuarios (Contraseña prueba: 123456 | Hash: $2a$10$JnWtYwpitWzonOVCuqGWvuzTGCJVJFHyNXks5d.BkQTaJxmBU5cMS)
INSERT INTO UsuarioSistema (email, password_hash, nombres, apellidos, id_rol, dni, cargo, estado, firma_hash) VALUES
('admin@universidad.edu.pe', '$2a$10$JnWtYwpitWzonOVCuqGWvuzTGCJVJFHyNXks5d.BkQTaJxmBU5cMS', 'ADMINISTRADOR', 'SISTEMAS', 1, '00000001', NULL, TRUE, NULL),
('v.guadalupe@universidad.edu.pe', '$2a$10$JnWtYwpitWzonOVCuqGWvuzTGCJVJFHyNXks5d.BkQTaJxmBU5cMS', 'VICTOR', 'GUADALUPE MORI', 2, '00000002', 'VICERRECTOR ACADÉMICO', TRUE, NULL),
('m.huerta@universidad.edu.pe', '$2a$10$JnWtYwpitWzonOVCuqGWvuzTGCJVJFHyNXks5d.BkQTaJxmBU5cMS', 'MIGUEL ANGEL', 'HUERTA ROJAS', 3, '00000003', NULL, TRUE, NULL);