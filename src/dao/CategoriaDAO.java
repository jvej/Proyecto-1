package dao;
import modelo.Categoria;
import java.util.List;

public interface CategoriaDAO {
    List<Categoria> listar();
    Categoria buscarPorId(String id);
    void guardar(Categoria categoria);
    void eliminar(String id);
}
