package dao;

import java.util.List;

public interface RecursoLecturaDAO {
    List<RecursoInfo> listarPorCategoria(String idCategoria);

    // Representación mínima de un recurso, solo con lo que necesita Calendarización.
    class RecursoInfo {
        public final String id;
        public final String idCategoria;
        public final String descripcion;

        public RecursoInfo(String id, String idCategoria, String descripcion) {
            this.id = id;
            this.idCategoria = idCategoria;
            this.descripcion = descripcion;
        }
    }
}