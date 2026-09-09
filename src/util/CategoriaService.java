package util;

import modelo.Categoria;
import modelo.ValidacionException;
import persistence.CategoriaXMLPersistence;

import java.util.ArrayList;
import java.util.List;

public class CategoriaService {

    private final CategoriaXMLPersistence persistence = new CategoriaXMLPersistence();

    public List<Categoria> listar() { return persistence.readAll(); }

    public List<Categoria> buscar(String texto) {
        if (texto == null || texto.trim().isEmpty()) return listar();
        String t = texto.trim().toLowerCase();
        List<Categoria> resultado = new ArrayList<>();
        for (Categoria c : listar()) {
            if (c.getDescripcion().toLowerCase().contains(t)) resultado.add(c);
        }
        return resultado;
    }

    public Categoria buscarPorId(String id) {
        for (Categoria c : listar()) if (c.getId().equalsIgnoreCase(id)) return c;
        return null;
    }

    public void crear(String descripcion) throws ValidacionException {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ValidacionException("La descripción es obligatoria.");
        }
        List<Categoria> lista = persistence.readAll();
        lista.add(new Categoria(generarSiguienteId(lista), descripcion.trim()));
        persistence.writeAll(lista);
    }

    public void modificar(String id, String descripcion) throws ValidacionException {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new ValidacionException("La descripción es obligatoria.");
        }
        List<Categoria> lista = persistence.readAll();
        for (Categoria c : lista) {
            if (c.getId().equalsIgnoreCase(id)) {
                c.setDescripcion(descripcion.trim());
                persistence.writeAll(lista);
                return;
            }
        }
        throw new ValidacionException("No existe una categoría con ese ID.");
    }

    public void eliminar(String id) {
        List<Categoria> lista = persistence.readAll();
        lista.removeIf(c -> c.getId().equalsIgnoreCase(id));
        persistence.writeAll(lista);
    }

    private String generarSiguienteId(List<Categoria> lista) {
        int maximo = 0;
        for (Categoria c : lista) {
            try {
                int numero = Integer.parseInt(c.getId().replace("CAT-", ""));
                if (numero > maximo) maximo = numero;
            } catch (NumberFormatException ignorado) { /* id con otro formato, se ignora */ }
        }
        return String.format("CAT-%06d", maximo + 1);
    }
}