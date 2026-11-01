package logica;

import modelo.ValidacionException;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

// Validaciones que se repetian en varios services.
final class Validaciones {
    private Validaciones() { }

    static boolean vacio(String s) { return s == null || s.trim().isEmpty(); }

    static LocalDate fecha(String texto, String mensaje) throws ValidacionException {
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new ValidacionException(mensaje);
        }
    }

    static LocalTime hora(String texto, String mensaje) throws ValidacionException {
        try {
            return LocalTime.parse(texto.trim());
        } catch (DateTimeParseException | NullPointerException e) {
            throw new ValidacionException(mensaje);
        }
    }

    // MySQL avisa con esta excepcion cuando se viola una PRIMARY KEY (duplicado)
    // o una FOREIGN KEY (ej: borrar una categoria que tiene recursos).
    static boolean esViolacionDeLlave(SQLException e) {
        return e instanceof SQLIntegrityConstraintViolationException;
    }
}