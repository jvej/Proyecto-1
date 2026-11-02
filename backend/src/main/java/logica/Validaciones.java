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

    // Oracle avisa con estos codigos cuando se viola una llave:
    //   ORA-00001 = PRIMARY KEY / UNIQUE duplicada
    //   ORA-02291 = FOREIGN KEY: el padre no existe
    //   ORA-02292 = FOREIGN KEY: no se puede borrar porque tiene hijos (ej: categoria con recursos)
    static boolean esViolacionDeLlave(SQLException e) {
        int codigo = e.getErrorCode();
        return e instanceof SQLIntegrityConstraintViolationException
                || codigo == 1 || codigo == 2291 || codigo == 2292;
    }
}