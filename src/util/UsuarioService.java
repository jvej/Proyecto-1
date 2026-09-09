package util;

import modelo.Usuario;
import modelo.ValidacionException;
import persistence.UsuarioXMLPersistence;

public class UsuarioService {

    private final UsuarioXMLPersistence persistence = new UsuarioXMLPersistence();

    public Usuario autenticar(String id, String clave) throws ValidacionException {
        if (id == null || id.trim().isEmpty() || clave == null || clave.trim().isEmpty()) {
            throw new ValidacionException("Debe completar el ID y la clave.");
        }
        for (Usuario u : persistence.readAll()) {
            if (u.getId().equalsIgnoreCase(id) && u.getClave().equals(clave)) return u;
        }
        throw new ValidacionException("ID o clave incorrectos.");
    }

    public void cambiarClave(String id, String claveActual, String claveNueva) throws ValidacionException {
        if (id == null || claveActual == null || claveNueva == null || claveNueva.trim().isEmpty()) {
            throw new ValidacionException("Complete todos los campos.");
        }
        var lista = persistence.readAll();
        for (Usuario u : lista) {
            if (u.getId().equalsIgnoreCase(id) && u.getClave().equals(claveActual)) {
                u.setClave(claveNueva);
                persistence.writeAll(lista);
                return;
            }
        }
        throw new ValidacionException("ID o clave actual incorrectos.");
    }
}