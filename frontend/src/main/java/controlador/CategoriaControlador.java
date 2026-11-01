package controlador;

import modelo.Categoria;

import java.util.List;

public class CategoriaControlador {

    public List<Categoria> listar() {
        try { return Remoto.service().categoriaListar(); } catch (Exception e) { throw Remoto.error(e); }
    }
    public List<Categoria> buscar(String texto) {
        try { return Remoto.service().categoriaBuscar(texto); } catch (Exception e) { throw Remoto.error(e); }
    }
    public void guardar(String id, String descripcion) throws Exception { Remoto.service().categoriaGuardar(id, descripcion); }
    public void eliminar(String id) throws Exception { Remoto.service().categoriaEliminar(id); }
}