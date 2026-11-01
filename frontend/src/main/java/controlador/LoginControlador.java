package controlador;

import modelo.Usuario;

public class LoginControlador {

    public Usuario login(String id, String clave) throws Exception {
        return Remoto.service().login(id, clave);
    }

    public void logout() {
        try { Remoto.service().logout(); } catch (Exception ignorado) { }
    }

    public void cambiarClave(String id, String claveActual, String claveNueva, String confirmacion) throws Exception {
        Remoto.service().cambiarClave(id, claveActual, claveNueva, confirmacion);
    }
}