-- Ejecutar UNA vez conectado como SYSTEM (en SQL Developer, con F5).
-- Crea el usuario/esquema "reservas" donde vive la base del proyecto.
-- Si la primera linea da error, ignorarlo (depende de la version de Oracle).
ALTER SESSION SET "_ORACLE_SCRIPT" = true;
CREATE USER reservas IDENTIFIED BY reservas;
GRANT CONNECT, RESOURCE TO reservas;
ALTER USER reservas QUOTA UNLIMITED ON USERS;