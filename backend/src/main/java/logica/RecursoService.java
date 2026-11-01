package logica;

import datos.CategoriaDao;
import datos.RecursoDao;
import modelo.Recurso;
import modelo.ValidacionException;

import java.sql.SQLException;
import java.util.List;

public class RecursoService {

    private final RecursoDao dao = new RecursoDao();
    private final CategoriaDao categoriaDao = new CategoriaDao();

    public List<Recurso> listar() throws SQLException { return dao.listar(); }

    public List<Recurso> buscar(String categoriaId, String descripcionParcial) throws SQLException {
        return dao.buscar(categoriaId, descripcionParcial);
    }

    public void guardar(String id, String categoriaId, String descripcion) throws ValidacionException, SQLException {
        if (Validaciones.vacio(id)) throw new ValidacionException("El id/número de activo es obligatorio.");
        if (Validaciones.vacio(categoriaId)) throw new ValidacionException("Debe seleccionar una categoría.");
        if (Validaciones.vacio(descripcion)) throw new ValidacionException("La descripción es obligatoria.");
        if (categoriaDao.buscarPorId(categoriaId) == null) throw new ValidacionException("La categoría seleccionada no existe.");
        dao.guardar(new Recurso(id.trim(), categoriaId, descripcion.trim()));
    }

    public void borrar(String id) throws ValidacionException, SQLException {
        if (Validaciones.vacio(id)) throw new ValidacionException("Debe seleccionar un recurso de la lista.");
        try {
            if (dao.eliminar(id.trim()) == 0) throw new ValidacionException("No se encontró el recurso con id: " + id);
        } catch (SQLException e) {
            if (Validaciones.esViolacionDeLlave(e)) {
                throw new ValidacionException("No se puede borrar: el recurso está asignado a reservas.");
            }
            throw e;
        }
    }
}