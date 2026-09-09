package controlador;

import modelo.Categoria;
import modelo.ValidacionException;
import util.CategoriaService;

import java.util.List;

public class CategoriaControlador {
    private final CategoriaService categoriaService = new CategoriaService();

    public List<Categoria> listar() { return categoriaService.listar(); }
    public List<Categoria> buscar(String texto) { return categoriaService.buscar(texto); }
    public void crear(String descripcion) throws ValidacionException { categoriaService.crear(descripcion); }
    public void modificar(String id, String descripcion) throws ValidacionException { categoriaService.modificar(id, descripcion); }
    public void eliminar(String id) { categoriaService.eliminar(id); }
}