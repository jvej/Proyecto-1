package modelo;

// Se lanza cuando se busca/modifica/elimina un registro que no existe.
public class RegistroNoEncontradoException extends Exception {
    public RegistroNoEncontradoException(String mensaje) { super(mensaje); }
}