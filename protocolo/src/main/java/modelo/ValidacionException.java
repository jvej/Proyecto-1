package modelo;

// Se lanza cuando un dato no cumple una regla de validación (campo vacío, formato inválido, etc.).
public class ValidacionException extends Exception {
    public ValidacionException(String mensaje) { super(mensaje); }
}