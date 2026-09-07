package controlador;

import dao.FuncionarioDAO;
import modelo.Funcionario;
import modelo.Rol;

import java.util.ArrayList;
import java.util.List;

public class FuncionarioControlador {

    private final FuncionarioDAO funcionarioDAO = new FuncionarioDAO();

    public List<Funcionario> listar() {
        return funcionarioDAO.listar();
    }

    public List<Funcionario> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listar();
        }
        String t = texto.trim().toLowerCase();
        List<Funcionario> resultado = new ArrayList<>();
        for (Funcionario f : listar()) {
            if (f.getId().equalsIgnoreCase(texto.trim()) || f.getNombre().toLowerCase().contains(t)) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    // Crea un funcionario nuevo. La clave inicial queda igual al id (lo pide el enunciado).
    public void crear(String id, String nombre, String telefono) throws Exception {
        if (id == null || id.trim().isEmpty()) throw new Exception("El ID es obligatorio.");
        if (nombre == null || nombre.trim().isEmpty()) throw new Exception("El nombre es obligatorio.");
        if (funcionarioDAO.buscarPorId(id.trim()) != null) throw new Exception("Ya existe un funcionario con ese ID.");

        Funcionario f = new Funcionario(id.trim(), id.trim(), Rol.FUNCIONARIO, nombre.trim(),
                telefono == null ? "" : telefono.trim());
        funcionarioDAO.guardar(f);
    }

    // Modifica un funcionario existente, conservando su clave actual (no la pisa).
    public void modificar(String id, String nombre, String telefono) throws Exception {
        Funcionario existente = funcionarioDAO.buscarPorId(id);
        if (existente == null) throw new Exception("No existe un funcionario con ese ID.");
        if (nombre == null || nombre.trim().isEmpty()) throw new Exception("El nombre es obligatorio.");

        existente.setNombre(nombre.trim());
        existente.setTelefono(telefono == null ? "" : telefono.trim());
        funcionarioDAO.guardar(existente);
    }

    public void eliminar(String id) {
        funcionarioDAO.eliminar(id);
    }
}