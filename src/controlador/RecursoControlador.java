package controlador;

import modelo.Recurso;
import modelo.ValidacionException;
import util.RecursoService;

import java.util.List;

public class RecursoControlador {
    private final RecursoService recursoService = new RecursoService();

    public List<Recurso> listar() { return recursoService.listar(); }
    public List<Recurso> buscar(String categoriaId, String descripcion) { return recursoService.buscar(categoriaId, descripcion); }
    public void guardar(String id, String categoriaId, String descripcion) throws ValidacionException { recursoService.guardar(id, categoriaId, descripcion); }
    public void borrar(String id) throws ValidacionException { recursoService.borrar(id); }
}