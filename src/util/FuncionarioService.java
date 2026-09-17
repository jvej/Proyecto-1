package util;

import modelo.Funcionario;
import modelo.Rol;
import modelo.Usuario;
import modelo.ValidacionException;
import persistence.FuncionarioXMLPersistence;
import persistence.UsuarioXMLPersistence;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioService {

    private final FuncionarioXMLPersistence persistence = new FuncionarioXMLPersistence();
    private final UsuarioXMLPersistence usuarioPersistence = new UsuarioXMLPersistence();

    public List<Funcionario> listar() { return persistence.readAll(); }

    public List<Funcionario> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) return listar();
        String t = texto.trim().toLowerCase();
        List<Funcionario> resultado = new ArrayList<>();
        for (Funcionario f : listar()) {
            if (f.getId().equalsIgnoreCase(texto.trim()) || f.getNombre().toLowerCase().contains(t)) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    public void crear(String id, String nombre, String telefono, String clave) throws ValidacionException {
        if (id == null || id.trim().isEmpty()) throw new ValidacionException("El ID es obligatorio.");
        if (nombre == null || nombre.trim().isEmpty()) throw new ValidacionException("El nombre es obligatorio.");
        if (clave == null || clave.trim().isEmpty()) throw new ValidacionException("La clave es obligatoria.");

        String idLimpio = id.trim();

        List<Funcionario> lista = persistence.readAll();
        for (Funcionario f : lista) {
            if (f.getId().equalsIgnoreCase(idLimpio)) throw new ValidacionException("Ya existe un funcionario con ese ID.");
        }

        List<Usuario> usuarios = usuarioPersistence.readAll();
        for (Usuario u : usuarios) {
            if (u.getId().equalsIgnoreCase(idLimpio)) throw new ValidacionException("Ya existe un usuario con ese ID.");
        }

        lista.add(new Funcionario(idLimpio, "", Rol.FUNCIONARIO, nombre.trim(),
                telefono == null ? "" : telefono.trim()));
        persistence.writeAll(lista);

        usuarios.add(new Usuario(idLimpio, clave.trim(), Rol.FUNCIONARIO));
        usuarioPersistence.writeAll(usuarios);
    }

    public void modificar(String id, String nombre, String telefono) throws ValidacionException {
        if (nombre == null || nombre.trim().isEmpty()) throw new ValidacionException("El nombre es obligatorio.");

        List<Funcionario> lista = persistence.readAll();
        for (Funcionario f : lista) {
            if (f.getId().equalsIgnoreCase(id)) {
                f.setNombre(nombre.trim());
                f.setTelefono(telefono == null ? "" : telefono.trim());
                persistence.writeAll(lista);
                return;
            }
        }
        throw new ValidacionException("No existe un funcionario con ese ID.");
    }

    public void guardar(String id, String nombre, String telefono, String clave) throws ValidacionException {
        List<Funcionario> lista = persistence.readAll();
        boolean existe = lista.stream().anyMatch(f -> f.getId().equalsIgnoreCase(id));
        if (existe) {
            modificar(id, nombre, telefono);
        } else {
            crear(id, nombre, telefono, clave);
        }
    }

    public void eliminar(String id) throws ValidacionException {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidacionException("Seleccione un funcionario de la lista.");
        }
        List<Funcionario> lista = persistence.readAll();
        boolean encontrado = lista.removeIf(f -> f.getId().equalsIgnoreCase(id));
        if (!encontrado) {
            throw new ValidacionException("No se encontró el funcionario con id: " + id);
        }
        persistence.writeAll(lista);

        List<Usuario> usuarios = usuarioPersistence.readAll();
        usuarios.removeIf(u -> u.getId().equalsIgnoreCase(id));
        usuarioPersistence.writeAll(usuarios);
    }
}