package controlador;

import modelo.Recurso;

import java.util.List;

public class RecursoControlador {

    public List<Recurso> listar() {
        try { return Remoto.service().recursoListar(); } catch (Exception e) { throw Remoto.error(e); }
    }
    public List<Recurso> buscar(String categoriaId, String descripcion) {
        try { return Remoto.service().recursoBuscar(categoriaId, descripcion); } catch (Exception e) { throw Remoto.error(e); }
    }
    public void guardar(String id, String categoriaId, String descripcion) throws Exception {
        Remoto.service().recursoGuardar(id, categoriaId, descripcion);
    }
    public void borrar(String id) throws Exception { Remoto.service().recursoBorrar(id); }
}