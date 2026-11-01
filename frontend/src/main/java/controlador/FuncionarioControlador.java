package controlador;

import modelo.Funcionario;
import modelo.ValidacionException;
import util.FuncionarioService;

import java.util.List;

public class FuncionarioControlador {
    private final FuncionarioService funcionarioService = new FuncionarioService();

    public List<Funcionario> listar() { return funcionarioService.listar(); }
    public List<Funcionario> buscar(String texto) { return funcionarioService.buscar(texto); }
    public void crear(String id, String nombre, String telefono, String clave) throws ValidacionException { funcionarioService.crear(id, nombre, telefono, clave); }
    public void modificar(String id, String nombre, String telefono) throws ValidacionException { funcionarioService.modificar(id, nombre, telefono); }
    public void guardar(String id, String nombre, String telefono, String clave) throws ValidacionException { funcionarioService.guardar(id, nombre, telefono, clave); }
    public void eliminar(String id) throws ValidacionException { funcionarioService.eliminar(id); }
}