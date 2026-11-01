package logica;

import datos.CategoriaDao;
import modelo.Categoria;
import modelo.ValidacionException;

import java.sql.SQLException;
import java.util.List;

public class CategoriaService {

    private final CategoriaDao dao = new CategoriaDao();

    public List<Categoria> listar() throws SQLException { return dao.listar(); }

    public List<Categoria> buscar(String texto) throws SQLException {
        if (Validaciones.vacio(texto)) return listar();
        return dao.buscar(texto.trim());
    }

    public Categoria buscarPorId(String id) throws SQLException { return dao.buscarPorId(id); }

    // synchronized: si dos administradores crean una categoria al mismo tiempo,
    // no deben quedar con el mismo id.
    public synchronized void crear(String descripcion) throws ValidacionException, SQLException {
        if (Validaciones.vacio(descripcion)) throw new ValidacionException("La descripción es obligatoria.");
        String id = String.format("CAT-%06d", dao.ultimoNumero() + 1);
        dao.crear(new Categoria(id, descripcion.trim()));
    }

    public void modificar(String id, String descripcion) throws ValidacionException, SQLException {
        if (Validaciones.vacio(descripcion)) throw new ValidacionException("La descripción es obligatoria.");
        if (dao.modificar(id, descripcion.trim()) == 0) throw new ValidacionException("No existe una categoría con ese ID.");
    }

    public void guardar(String id, String descripcion) throws ValidacionException, SQLException {
        if (Validaciones.vacio(id)) crear(descripcion);
        else modificar(id.trim(), descripcion);
    }

    public void eliminar(String id) throws ValidacionException, SQLException {
        if (Validaciones.vacio(id)) throw new ValidacionException("Seleccione una categoría de la lista.");
        try {
            if (dao.eliminar(id.trim()) == 0) throw new ValidacionException("No se encontró la categoría con id: " + id);
        } catch (SQLException e) {
            if (Validaciones.esViolacionDeLlave(e)) {
                throw new ValidacionException("No se puede eliminar: la categoría tiene recursos o reservas asociadas.");
            }
            throw e;
        }
    }
}