package util;

import modelo.Funcionario;
import modelo.Rol;
import modelo.ValidacionException;
import persistence.FuncionarioXMLPersistence;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioService {

    private final FuncionarioXMLPersistence persistence = new FuncionarioXMLPersistence();

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

    public void crear(String id, String nombre, String telefono) throws ValidacionException {
        if (id == null || id.trim().isEmpty()) throw new ValidacionException("El ID es obligatorio.");
        if (nombre == null || nombre.trim().isEmpty()) throw new ValidacionException("El nombre es obligatorio.");

        List<Funcionario> lista = persistence.readAll();
        for (Funcionario f : lista) {
            if (f.getId().equalsIgnoreCase(id)) throw new ValidacionException("Ya existe un funcionario con ese ID.");
        }
        lista.add(new Funcionario(id.trim(), id.trim(), Rol.FUNCIONARIO, nombre.trim(),
                telefono == null ? "" : telefono.trim()));
        persistence.writeAll(lista);
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

    public void eliminar(String id) {
        List<Funcionario> lista = persistence.readAll();
        lista.removeIf(f -> f.getId().equalsIgnoreCase(id));
        persistence.writeAll(lista);
    }
}