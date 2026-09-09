package util;

import modelo.Recurso;
import modelo.ValidacionException;
import persistence.RecursoXMLPersistence;

import java.util.ArrayList;
import java.util.List;

public class RecursoService {

    private final RecursoXMLPersistence persistence = new RecursoXMLPersistence();

    public List<Recurso> listar() { return persistence.readAll(); }

    public List<Recurso> buscar(String categoriaId, String descripcionParcial) {
        List<Recurso> resultado = new ArrayList<>();
        for (Recurso r : listar()) {
            boolean coincideCategoria = (categoriaId == null || categoriaId.isEmpty()
                    || r.getCategoriaId().equalsIgnoreCase(categoriaId));
            boolean coincideDescripcion = (descripcionParcial == null || descripcionParcial.isEmpty()
                    || r.getDescripcion().toLowerCase().contains(descripcionParcial.toLowerCase()));
            if (coincideCategoria && coincideDescripcion) resultado.add(r);
        }
        return resultado;
    }

    public void guardar(String id, String categoriaId, String descripcion) throws ValidacionException {
        if (id == null || id.trim().isEmpty()) throw new ValidacionException("El id/número de activo es obligatorio.");
        if (categoriaId == null || categoriaId.isEmpty()) throw new ValidacionException("Debe seleccionar una categoría.");
        if (descripcion == null || descripcion.trim().isEmpty()) throw new ValidacionException("La descripción es obligatoria.");

        List<Recurso> lista = persistence.readAll();
        lista.removeIf(r -> r.getId().equalsIgnoreCase(id));
        lista.add(new Recurso(id.trim(), categoriaId, descripcion.trim()));
        persistence.writeAll(lista);
    }

    public void borrar(String id) throws ValidacionException {
        if (id == null || id.isEmpty()) throw new ValidacionException("Debe seleccionar un recurso de la lista.");
        List<Recurso> lista = persistence.readAll();
        boolean encontrado = lista.removeIf(r -> r.getId().equalsIgnoreCase(id));
        if (!encontrado) throw new ValidacionException("No se encontró el recurso con id: " + id);
        persistence.writeAll(lista);
    }
}