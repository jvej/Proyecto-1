package controlador;

import modelo.Funcionario;

import java.util.List;

public class FuncionarioControlador {

    public List<Funcionario> listar() {
        try { return Remoto.service().funcionarioListar(); } catch (Exception e) { throw Remoto.error(e); }
    }
    public List<Funcionario> buscar(String texto) {
        try { return Remoto.service().funcionarioBuscar(texto); } catch (Exception e) { throw Remoto.error(e); }
    }
    public void guardar(String id, String nombre, String telefono, String clave) throws Exception {
        Remoto.service().funcionarioGuardar(id, nombre, telefono, clave);
    }
    public void eliminar(String id) throws Exception { Remoto.service().funcionarioEliminar(id); }
}