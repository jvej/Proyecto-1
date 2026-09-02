package controlador;
import dao.CategoriaDAO;
import dao.CategoriaDAOImpl;
import modelo.Categoria;

import java.util.ArrayList;
import java.util.List;

public class CategoriaControlador {
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();

    public List<Categoria> listar() {
        return categoriaDAO.listar();
    }

    public List<Categoria> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return listar();
        }
        String t = texto.trim().toLowerCase();
        List<Categoria> resultado = new ArrayList<>();
        for (Categoria c : listar()) {
            if (c.getDescripcion().toLowerCase().contains(t)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    public Categoria buscarPorId(String id) {
        return categoriaDAO.buscarPorId(id);
    }

    public void crear(String descripcion) throws Exception {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new Exception("La descripción es obligatoria.");
        }
        categoriaDAO.guardar(new Categoria(null, descripcion.trim()));
    }

    public void modificar(String id, String descripcion) throws Exception {
        Categoria existente = categoriaDAO.buscarPorId(id);
        if (existente == null) {
            throw new Exception("No existe una categoría con ese ID.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new Exception("La descripción es obligatoria.");
        }
        existente.setDescripcion(descripcion.trim());
        categoriaDAO.guardar(existente);
    }

    public void eliminar(String id) {
        categoriaDAO.eliminar(id);
    }
}
