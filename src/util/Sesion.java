package util;

import modelo.Rol;
import modelo.Usuario;

public class Sesion {
    private static Sesion instancia;
    private Usuario usuarioActual;

    private Sesion() {}

    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    public Usuario getUsuarioActual() { return usuarioActual; }
    public void setUsuarioActual(Usuario usuario) { this.usuarioActual = usuario; }

    public boolean isAdministrador() {
        return usuarioActual != null && usuarioActual.getRol() == Rol.ADMINISTRADOR;
    }

    public void cerrarSesion() { usuarioActual = null; }
}