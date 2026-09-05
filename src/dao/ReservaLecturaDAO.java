package dao;

import java.util.List;

public interface ReservaLecturaDAO {
    List<ReservaInfo> listarPorFecha(String fecha, List<String> idsRecurso);

    class ReservaInfo {
        public final String idRecurso;
        public final String horaInicio;
        public final String horaFin;
        public final String actividad;
        public final String idFuncionario;

        public ReservaInfo(String idRecurso, String horaInicio, String horaFin,
                           String actividad, String idFuncionario) {
            this.idRecurso = idRecurso;
            this.horaInicio = horaInicio;
            this.horaFin = horaFin;
            this.actividad = actividad;
            this.idFuncionario = idFuncionario;
        }
    }
}