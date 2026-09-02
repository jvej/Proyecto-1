package controlador;
import dao.RecursoDAO;
import modelo.Recurso;

import java.util.List;

public class RecursoControlador {
    private final RecursoDAO recursoDAO = new RecursoDAO();


    public List<Recurso> listar() {
        return recursoDAO.listar();
    }
    public List<Recurso> buscar(String categoriaId, String descripcion) {
        return recursoDAO.buscar(categoriaId, descripcion);
    }
    public void guardar(String id, String categoriaId, String descripcion) {
        validar(id, categoriaId, descripcion);
        Recurso r = new Recurso(id.trim(), categoriaId, descripcion.trim());
        recursoDAO.guardar(r);
    }

    public void borrar(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar un recurso de la lista.");
        }
        boolean encontrado = recursoDAO.borrar(id);
        if (!encontrado) {
            throw new IllegalArgumentException("No se encontró el recurso con id: " + id);
        }
    }

    private void validar(String id, String categoriaId, String descripcion) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El id/número de activo es obligatorio.");
        }
        if (categoriaId == null || categoriaId.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar una categoría.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }
    }
}
