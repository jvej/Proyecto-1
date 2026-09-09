package controlador;

import modelo.Usuario;
import modelo.ValidacionException;
import util.UsuarioService;

public class LoginControlador {
    private final UsuarioService usuarioService = new UsuarioService();

    public Usuario login(String id, String clave) throws ValidacionException {
        return usuarioService.autenticar(id, clave);
    }

    public void cambiarClave(String id, String claveActual, String claveNueva) throws ValidacionException {
        usuarioService.cambiarClave(id, claveActual, claveNueva);
    }
}