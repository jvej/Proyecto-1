-- =====================================================================
--  PROYECTO #2 - SISTEMA DE RESERVA DE RECURSOS  (EIF206 Programacion 3)
--  Script de creacion de las tablas en ORACLE (SQL Developer)
--
--  Como ejecutarlo:
--    1. En SQL Developer abran su conexion (la misma que pondran en backend/db.properties).
--    2. File > Open... > este archivo.
--    3. Ejecutar como SCRIPT: boton "Run Script" o tecla F5 (NO Ctrl+Enter).
--
--  OJO: borra y vuelve a crear las tablas del sistema (se pierden sus datos).
--  La primera vez los DROP pueden decir "la tabla no existe": es normal.
-- =====================================================================

-- ---------- Borrar tablas si ya existen (hijas primero) ----------
BEGIN EXECUTE IMMEDIATE 'DROP TABLE reserva_recurso CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE reserva_categoria CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE reserva CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE recurso CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE categoria CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE funcionario CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE usuario CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

-- ---------------------------------------------------------------------
-- Usuarios del sistema (login). rol = ADMINISTRADOR o FUNCIONARIO
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
                         id     VARCHAR2(30)  NOT NULL,
                         clave  VARCHAR2(100) NOT NULL,
                         rol    VARCHAR2(20)  NOT NULL,
                         CONSTRAINT pk_usuario PRIMARY KEY (id),
                         CONSTRAINT ck_usuario_rol CHECK (rol IN ('ADMINISTRADOR', 'FUNCIONARIO'))
);

-- ---------------------------------------------------------------------
-- Datos personales del funcionario. Su id ES el id de su usuario (1 a 1).
-- (telefono puede ser NULL: en Oracle un texto vacio '' se guarda como NULL)
-- ---------------------------------------------------------------------
CREATE TABLE funcionario (
                             id        VARCHAR2(30)  NOT NULL,
                             nombre    VARCHAR2(100) NOT NULL,
                             telefono  VARCHAR2(30),
                             CONSTRAINT pk_funcionario PRIMARY KEY (id),
                             CONSTRAINT fk_funcionario_usuario FOREIGN KEY (id) REFERENCES usuario (id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- Categorias de recursos ("Sala para 10 personas", "Laptop Windows", ...)
-- ---------------------------------------------------------------------
CREATE TABLE categoria (
                           id           VARCHAR2(20)  NOT NULL,
                           descripcion  VARCHAR2(100) NOT NULL,
                           CONSTRAINT pk_categoria PRIMARY KEY (id)
);

-- ---------------------------------------------------------------------
-- Recursos concretos (cada laptop, cada sala). Pertenecen a una categoria.
-- Sin ON DELETE: Oracle NO deja borrar una categoria que tenga recursos.
-- ---------------------------------------------------------------------
CREATE TABLE recurso (
                         id            VARCHAR2(30)  NOT NULL,
                         categoria_id  VARCHAR2(20)  NOT NULL,
                         descripcion   VARCHAR2(150) NOT NULL,
                         CONSTRAINT pk_recurso PRIMARY KEY (id),
                         CONSTRAINT fk_recurso_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id)
);

-- ---------------------------------------------------------------------
-- Reservas. estado = ACTIVA o CANCELADA
-- Oracle no tiene tipo TIME: las horas se guardan como texto 'HH:MM'
-- (ej. '09:00'). Al tener siempre 2 digitos, se pueden comparar con < y >.
-- ---------------------------------------------------------------------
CREATE TABLE reserva (
                         id              VARCHAR2(20)  NOT NULL,
                         funcionario_id  VARCHAR2(30)  NOT NULL,
                         actividad       VARCHAR2(150) NOT NULL,
                         fecha           DATE          NOT NULL,
                         hora_inicio     VARCHAR2(5)   NOT NULL,
                         hora_fin        VARCHAR2(5)   NOT NULL,
                         estado          VARCHAR2(15)  DEFAULT 'ACTIVA' NOT NULL,
                         CONSTRAINT pk_reserva PRIMARY KEY (id),
                         CONSTRAINT fk_reserva_usuario FOREIGN KEY (funcionario_id) REFERENCES usuario (id),
                         CONSTRAINT ck_reserva_estado CHECK (estado IN ('ACTIVA', 'CANCELADA')),
                         CONSTRAINT ck_reserva_horas  CHECK (hora_inicio < hora_fin)
);

CREATE INDEX ix_reserva_fecha ON reserva (fecha);

-- ---------------------------------------------------------------------
-- Una reserva pide VARIAS categorias y se le asignan VARIOS recursos.
-- Eso es una relacion "muchos a muchos", por eso van en tablas aparte.
-- ---------------------------------------------------------------------
CREATE TABLE reserva_categoria (
                                   reserva_id    VARCHAR2(20) NOT NULL,
                                   categoria_id  VARCHAR2(20) NOT NULL,
                                   CONSTRAINT pk_reserva_categoria PRIMARY KEY (reserva_id, categoria_id),
                                   CONSTRAINT fk_rc_reserva   FOREIGN KEY (reserva_id)   REFERENCES reserva (id) ON DELETE CASCADE,
                                   CONSTRAINT fk_rc_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id)
);

CREATE TABLE reserva_recurso (
                                 reserva_id  VARCHAR2(20) NOT NULL,
                                 recurso_id  VARCHAR2(30) NOT NULL,
                                 CONSTRAINT pk_reserva_recurso PRIMARY KEY (reserva_id, recurso_id),
                                 CONSTRAINT fk_rr_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id) ON DELETE CASCADE,
                                 CONSTRAINT fk_rr_recurso FOREIGN KEY (recurso_id) REFERENCES recurso (id)
);

-- =====================================================================
--  DATOS INICIALES (migrados de los XML del Proyecto #1)
-- =====================================================================

INSERT INTO usuario (id, clave, rol) VALUES ('admin', 'admin', 'ADMINISTRADOR');
INSERT INTO usuario (id, clave, rol) VALUES ('111', '1234', 'FUNCIONARIO');
INSERT INTO usuario (id, clave, rol) VALUES ('123', '123', 'FUNCIONARIO');
INSERT INTO usuario (id, clave, rol) VALUES ('121212', '11', 'FUNCIONARIO');
INSERT INTO usuario (id, clave, rol) VALUES ('1234', 'abcd', 'FUNCIONARIO');
INSERT INTO usuario (id, clave, rol) VALUES ('1243', '0000', 'FUNCIONARIO');
INSERT INTO usuario (id, clave, rol) VALUES ('9999', '4444', 'FUNCIONARIO');
INSERT INTO usuario (id, clave, rol) VALUES ('5555', 'tren', 'FUNCIONARIO');
INSERT INTO usuario (id, clave, rol) VALUES ('7777', 'luqu3', 'FUNCIONARIO');
INSERT INTO usuario (id, clave, rol) VALUES ('2222', 'k1l0', 'FUNCIONARIO');
INSERT INTO usuario (id, clave, rol) VALUES ('7575', 'f13st4', 'FUNCIONARIO');

INSERT INTO funcionario (id, nombre, telefono) VALUES ('123', 'Coso', '87892950');
INSERT INTO funcionario (id, nombre, telefono) VALUES ('121212', 'Coso', '88888888');
INSERT INTO funcionario (id, nombre, telefono) VALUES ('1234', 'Axel', '12345678');
INSERT INTO funcionario (id, nombre, telefono) VALUES ('1243', 'Mariano', '85541676');
INSERT INTO funcionario (id, nombre, telefono) VALUES ('9999', 'Sidney', '87654321');
INSERT INTO funcionario (id, nombre, telefono) VALUES ('5555', 'Daniela', '76541238');
INSERT INTO funcionario (id, nombre, telefono) VALUES ('7777', 'Samuel', '87659124');
INSERT INTO funcionario (id, nombre, telefono) VALUES ('2222', 'Valeria', '21345698');
INSERT INTO funcionario (id, nombre, telefono) VALUES ('7575', 'Nicole', '21345213');
INSERT INTO funcionario (id, nombre, telefono) VALUES ('111', 'Funcionario 111', NULL);

INSERT INTO categoria (id, descripcion) VALUES ('CAT-000001', 'Sala para 10 personas');
INSERT INTO categoria (id, descripcion) VALUES ('CAT-000002', 'Laptop Windows');
INSERT INTO categoria (id, descripcion) VALUES ('CAT-000003', 'Laptop Linux');
INSERT INTO categoria (id, descripcion) VALUES ('CAT-000004', 'Sala para 15 personas');
INSERT INTO categoria (id, descripcion) VALUES ('CAT-000005', 'Proyetor con HDMI');

INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('S1-001', 'CAT-000001', 'Sala 1, primer piso');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('S1-002', 'CAT-000001', 'Sala 1, segundo piso');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('S1-003', 'CAT-000001', 'Sala 1, tercer piso');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('S1-004', 'CAT-000001', 'Sala 1, edificio anexo');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('S2-002', 'CAT-000004', 'Sala 2, Edificio B');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('S2-003', 'CAT-000004', 'Sala 2, biblioteca');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('S2-004', 'CAT-000004', 'Sala 2, auditorio');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('S2-001', 'CAT-000001', 'Sala 2, Edificio A');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('238715', 'CAT-000002', 'Laptop  #238715');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('238716', 'CAT-000002', 'Laptop  #238716');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('238717', 'CAT-000002', 'Laptop  #238717');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('238718', 'CAT-000002', 'Laptop  #238718');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('45238', 'CAT-000003', 'Laptop Linux #45238');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('45239', 'CAT-000003', 'Laptop Linux #45239');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('45240', 'CAT-000003', 'Laptop Linux #45240');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('45241', 'CAT-000003', 'Laptop Linux #45241');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('PRY-001', 'CAT-000005', 'Proyector sala de juntas');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('PRY-002', 'CAT-000005', 'Proyector auditorio');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('PRY-003', 'CAT-000005', 'Proyector móvil 1');
INSERT INTO recurso (id, categoria_id, descripcion) VALUES ('PRY-004', 'CAT-000005', 'Proyector móvil 2');

INSERT INTO reserva (id, funcionario_id, actividad, fecha, hora_inicio, hora_fin, estado) VALUES ('RES-000001', '111', 'capacitación', DATE '2026-09-20', '14:00', '16:00', 'ACTIVA');
INSERT INTO reserva (id, funcionario_id, actividad, fecha, hora_inicio, hora_fin, estado) VALUES ('RES-000002', '111', 'presentación a clientes', DATE '2026-09-21', '08:00', '10:00', 'ACTIVA');
INSERT INTO reserva (id, funcionario_id, actividad, fecha, hora_inicio, hora_fin, estado) VALUES ('RES-000003', '111', 'taller de programación', DATE '2026-09-25', '10:00', '12:00', 'ACTIVA');
INSERT INTO reserva (id, funcionario_id, actividad, fecha, hora_inicio, hora_fin, estado) VALUES ('RES-000004', '111', 'entrevista de trabajo', DATE '2026-09-30', '13:00', '15:00', 'ACTIVA');
INSERT INTO reserva (id, funcionario_id, actividad, fecha, hora_inicio, hora_fin, estado) VALUES ('RES-000005', '111', 'Reunion de planificacion semanal', DATE '2026-09-16', '08:00', '09:00', 'ACTIVA');
INSERT INTO reserva (id, funcionario_id, actividad, fecha, hora_inicio, hora_fin, estado) VALUES ('RES-000006', '111', 'Reunion', DATE '2026-09-15', '14:00', '16:00', 'ACTIVA');

INSERT INTO reserva_categoria (reserva_id, categoria_id) VALUES ('RES-000001', 'CAT-000002');
INSERT INTO reserva_categoria (reserva_id, categoria_id) VALUES ('RES-000002', 'CAT-000004');
INSERT INTO reserva_categoria (reserva_id, categoria_id) VALUES ('RES-000002', 'CAT-000005');
INSERT INTO reserva_categoria (reserva_id, categoria_id) VALUES ('RES-000003', 'CAT-000003');
INSERT INTO reserva_categoria (reserva_id, categoria_id) VALUES ('RES-000004', 'CAT-000001');
INSERT INTO reserva_categoria (reserva_id, categoria_id) VALUES ('RES-000005', 'CAT-000001');
INSERT INTO reserva_categoria (reserva_id, categoria_id) VALUES ('RES-000006', 'CAT-000001');
INSERT INTO reserva_categoria (reserva_id, categoria_id) VALUES ('RES-000006', 'CAT-000003');

INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES ('RES-000001', '238715');
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES ('RES-000002', 'S2-002');
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES ('RES-000002', 'PRY-001');
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES ('RES-000003', '45238');
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES ('RES-000004', 'S1-001');
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES ('RES-000005', 'S1-001');
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES ('RES-000006', 'S1-001');
INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES ('RES-000006', '45238');
COMMIT;