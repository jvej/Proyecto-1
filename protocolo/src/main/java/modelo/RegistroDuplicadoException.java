package modelo;

// Se lanza cuando se intenta crear un registro cuyo id/clave única ya existe.
public class RegistroDuplicadoException extends Exception {
    public RegistroDuplicadoException(String mensaje) { super(mensaje); }
}