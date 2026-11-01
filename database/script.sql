-- =====================================================================
--  PROYECTO #2 - SISTEMA DE RESERVA DE RECURSOS  (EIF206 Programacion 3)
--  Script de creacion de la base de datos MySQL
--
--  Como ejecutarlo:
--    * MySQL Workbench: File > Open SQL Script... > este archivo > rayo (Execute)
--    * Consola:  mysql -u root -p < script.sql
--
--  OJO: borra y vuelve a crear la base "reservas_db" (se pierden los datos).
-- =====================================================================

DROP DATABASE IF EXISTS reservas_db;
CREATE DATABASE reservas_db CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;
USE reservas_db;

-- ---------------------------------------------------------------------
-- Usuarios del sistema (login). rol = ADMINISTRADOR o FUNCIONARIO
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
                         id     VARCHAR(30)  NOT NULL,
                         clave  VARCHAR(100) NOT NULL,
                         rol    VARCHAR(20)  NOT NULL,
                         CONSTRAINT pk_usuario PRIMARY KEY (id),
                         CONSTRAINT ck_usuario_rol CHECK (rol IN ('ADMINISTRADOR', 'FUNCIONARIO'))
);

-- ---------------------------------------------------------------------
-- Datos personales del funcionario. Su id ES el id de su usuario (1 a 1).
-- ---------------------------------------------------------------------
CREATE TABLE funcionario (
                             id        VARCHAR(30)  NOT NULL,
                             nombre    VARCHAR(100) NOT NULL,
                             telefono  VARCHAR(30)  NOT NULL DEFAULT '',
                             CONSTRAINT pk_funcionario PRIMARY KEY (id),
                             CONSTRAINT fk_funcionario_usuario FOREIGN KEY (id) REFERENCES usuario (id)
                                 ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- Categorias de recursos ("Sala para 10 personas", "Laptop Windows", ...)
-- ---------------------------------------------------------------------
CREATE TABLE categoria (
                           id           VARCHAR(20)  NOT NULL,
                           descripcion  VARCHAR(100) NOT NULL,
                           CONSTRAINT pk_categoria PRIMARY KEY (id)
);

-- ---------------------------------------------------------------------
-- Recursos concretos (cada laptop, cada sala). Pertenecen a una categoria.
-- No se puede borrar una categoria que tenga recursos (RESTRICT).
-- ---------------------------------------------------------------------
CREATE TABLE recurso (
                         id            VARCHAR(30)  NOT NULL,
                         categoria_id  VARCHAR(20)  NOT NULL,
                         descripcion   VARCHAR(150) NOT NULL,
                         CONSTRAINT pk_recurso PRIMARY KEY (id),
                         CONSTRAINT fk_recurso_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id)
                             ON DELETE RESTRICT
);

-- ---------------------------------------------------------------------
-- Reservas. estado = ACTIVA o CANCELADA
-- ---------------------------------------------------------------------
CREATE TABLE reserva (
                         id              VARCHAR(20)  NOT NULL,
                         funcionario_id  VARCHAR(30)  NOT NULL,
                         actividad       VARCHAR(150) NOT NULL,
                         fecha           DATE         NOT NULL,
                         hora_inicio     TIME         NOT NULL,
                         hora_fin        TIME         NOT NULL,
                         estado          VARCHAR(15)  NOT NULL DEFAULT 'ACTIVA',
                         CONSTRAINT pk_reserva PRIMARY KEY (id),
                         CONSTRAINT fk_reserva_usuario FOREIGN KEY (funcionario_id) REFERENCES usuario (id)
                             ON DELETE RESTRICT,
                         CONSTRAINT ck_reserva_estado CHECK (estado IN ('ACTIVA', 'CANCELADA')),
                         CONSTRAINT ck_reserva_horas  CHECK (hora_inicio < hora_fin)
);

CREATE INDEX ix_reserva_fecha ON reserva (fecha);

-- ---------------------------------------------------------------------
-- Una reserva pide VARIAS categorias y se le asignan VARIOS recursos.
-- Eso es una relacion "muchos a muchos", por eso van en tablas aparte.
-- ---------------------------------------------------------------------
CREATE TABLE reserva_categoria (
                                   reserva_id    VARCHAR(20) NOT NULL,
                                   categoria_id  VARCHAR(20) NOT NULL,
                                   CONSTRAINT pk_reserva_categoria PRIMARY KEY (reserva_id, categoria_id),
                                   CONSTRAINT fk_rc_reserva   FOREIGN KEY (reserva_id)   REFERENCES reserva (id)   ON DELETE CASCADE,
                                   CONSTRAINT fk_rc_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id) ON DELETE RESTRICT
);

CREATE TABLE reserva_recurso (
                                 reserva_id  VARCHAR(20) NOT NULL,
                                 recurso_id  VARCHAR(30) NOT NULL,
                                 CONSTRAINT pk_reserva_recurso PRIMARY KEY (reserva_id, recurso_id),
                                 CONSTRAINT fk_rr_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id) ON DELETE CASCADE,
                                 CONSTRAINT fk_rr_recurso FOREIGN KEY (recurso_id) REFERENCES recurso (id) ON DELETE RESTRICT
);

-- =====================================================================
--  DATOS INICIALES (migrados de los XML del Proyecto #1)
-- =====================================================================

INSERT INTO usuario (id, clave, rol) VALUES
                                         ('admin', 'admin', 'ADMINISTRADOR'),
                                         ('111', '1234', 'FUNCIONARIO'),
                                         ('123', '123', 'FUNCIONARIO'),
                                         ('121212', '11', 'FUNCIONARIO'),
                                         ('1234', 'abcd', 'FUNCIONARIO'),
                                         ('1243', '0000', 'FUNCIONARIO'),
                                         ('9999', '4444', 'FUNCIONARIO'),
                                         ('5555', 'tren', 'FUNCIONARIO'),
                                         ('7777', 'luqu3', 'FUNCIONARIO'),
                                         ('2222', 'k1l0', 'FUNCIONARIO'),
                                         ('7575', 'f13st4', 'FUNCIONARIO');

INSERT INTO funcionario (id, nombre, telefono) VALUES
                                                   ('123', 'Coso', '87892950'),
                                                   ('121212', 'Coso', '88888888'),
                                                   ('1234', 'Axel', '12345678'),
                                                   ('1243', 'Mariano', '85541676'),
                                                   ('9999', 'Sidney', '87654321'),
                                                   ('5555', 'Daniela', '76541238'),
                                                   ('7777', 'Samuel', '87659124'),
                                                   ('2222', 'Valeria', '21345698'),
                                                   ('7575', 'Nicole', '21345213'),
                                                   ('111', 'Funcionario 111', '');

INSERT INTO categoria (id, descripcion) VALUES
                                            ('CAT-000001', 'Sala para 10 personas'),
                                            ('CAT-000002', 'Laptop Windows'),
                                            ('CAT-000003', 'Laptop Linux'),
                                            ('CAT-000004', 'Sala para 15 personas'),
                                            ('CAT-000005', 'Proyetor con HDMI');

INSERT INTO recurso (id, categoria_id, descripcion) VALUES
                                                        ('S1-001', 'CAT-000001', 'Sala 1, primer piso'),
                                                        ('S1-002', 'CAT-000001', 'Sala 1, segundo piso'),
                                                        ('S1-003', 'CAT-000001', 'Sala 1, tercer piso'),
                                                        ('S1-004', 'CAT-000001', 'Sala 1, edificio anexo'),
                                                        ('S2-002', 'CAT-000004', 'Sala 2, Edificio B'),
                                                        ('S2-003', 'CAT-000004', 'Sala 2, biblioteca'),
                                                        ('S2-004', 'CAT-000004', 'Sala 2, auditorio'),
                                                        ('S2-001', 'CAT-000001', 'Sala 2, Edificio A'),
                                                        ('238715', 'CAT-000002', 'Laptop  #238715'),
                                                        ('238716', 'CAT-000002', 'Laptop  #238716'),
                                                        ('238717', 'CAT-000002', 'Laptop  #238717'),
                                                        ('238718', 'CAT-000002', 'Laptop  #238718'),
                                                        ('45238', 'CAT-000003', 'Laptop Linux #45238'),
                                                        ('45239', 'CAT-000003', 'Laptop Linux #45239'),
                                                        ('45240', 'CAT-000003', 'Laptop Linux #45240'),
                                                        ('45241', 'CAT-000003', 'Laptop Linux #45241'),
                                                        ('PRY-001', 'CAT-000005', 'Proyector sala de juntas'),
                                                        ('PRY-002', 'CAT-000005', 'Proyector auditorio'),
                                                        ('PRY-003', 'CAT-000005', 'Proyector móvil 1'),
                                                        ('PRY-004', 'CAT-000005', 'Proyector móvil 2');

INSERT INTO reserva (id, funcionario_id, actividad, fecha, hora_inicio, hora_fin, estado) VALUES
                                                                                              ('RES-000001', '111', 'capacitación', '2026-09-20', '14:00', '16:00', 'ACTIVA'),
                                                                                              ('RES-000002', '111', 'presentación a clientes', '2026-09-21', '08:00', '10:00', 'ACTIVA'),
                                                                                              ('RES-000003', '111', 'taller de programación', '2026-09-25', '10:00', '12:00', 'ACTIVA'),
                                                                                              ('RES-000004', '111', 'entrevista de trabajo', '2026-09-30', '13:00', '15:00', 'ACTIVA'),
                                                                                              ('RES-000005', '111', 'Reunion de planificacion semanal', '2026-09-16', '08:00', '09:00', 'ACTIVA'),
                                                                                              ('RES-000006', '111', 'Reunion', '2026-09-15', '14:00', '16:00', 'ACTIVA');

INSERT INTO reserva_categoria (reserva_id, categoria_id) VALUES
                                                             ('RES-000001', 'CAT-000002'),
                                                             ('RES-000002', 'CAT-000004'),
                                                             ('RES-000002', 'CAT-000005'),
                                                             ('RES-000003', 'CAT-000003'),
                                                             ('RES-000004', 'CAT-000001'),
                                                             ('RES-000005', 'CAT-000001'),
                                                             ('RES-000006', 'CAT-000001'),
                                                             ('RES-000006', 'CAT-000003');

INSERT INTO reserva_recurso (reserva_id, recurso_id) VALUES
                                                         ('RES-000001', '238715'),
                                                         ('RES-000002', 'S2-002'),
                                                         ('RES-000002', 'PRY-001'),
                                                         ('RES-000003', '45238'),
                                                         ('RES-000004', 'S1-001'),
                                                         ('RES-000005', 'S1-001'),
                                                         ('RES-000006', 'S1-001'),
                                                         ('RES-000006', '45238');


-- =====================================================================
--  (Opcional) Usuario de MySQL exclusivo para la aplicacion.
--  Si lo crean, pongan estos datos en backend/db.properties
-- =====================================================================
-- CREATE USER IF NOT EXISTS 'reservas_app'@'%' IDENTIFIED BY 'reservas123';
-- GRANT ALL PRIVILEGES ON reservas_db.* TO 'reservas_app'@'%';
-- FLUSH PRIVILEGES;