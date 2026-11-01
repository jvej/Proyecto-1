package datos;

import logica.CategoriaService;
import logica.RecursoService;
import logica.ReservaService;
import modelo.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// Tests de INTEGRACION contra la base de datos MySQL REAL.
// Requisitos: MySQL corriendo, script database/script.sql ejecutado y
// backend/db.properties con la clave correcta.
// Todo lo que crea usa ids "IT-..." y se borra al final de cada prueba.
class BaseDatosIT {

    private static final String CAT = "IT-CAT";
    private final String fecha = LocalDate.now().plusDays(30).toString();

    @AfterEach
    void limpiar() throws Exception {
        try (Connection cn = Database.getConnection()) {
            // 1) buscar las reservas de prueba y borrarlas (sus filas intermedias se borran en cascada)
            java.util.List<String> ids = new java.util.ArrayList<>();
            try (PreparedStatement ps = cn.prepareStatement(
                    "SELECT reserva_id FROM reserva_categoria WHERE categoria_id = ?")) {
                ps.setString(1, CAT);
                try (java.sql.ResultSet rs = ps.executeQuery()) { while (rs.next()) ids.add(rs.getString(1)); }
            }
            for (String id : ids) ejecutar(cn, "DELETE FROM reserva WHERE id = '" + id + "'");
            ejecutar(cn, "DELETE FROM recurso WHERE categoria_id = '" + CAT + "'");
            ejecutar(cn, "DELETE FROM categoria WHERE id = '" + CAT + "' OR descripcion = 'Categoria IT'");
        }
    }

    private void ejecutar(Connection cn, String sql) throws Exception {
        try (PreparedStatement ps = cn.prepareStatement(sql)) { ps.executeUpdate(); }
    }

    private void crearCategoriaYRecurso() throws Exception {
        new CategoriaDao().crear(new Categoria(CAT, "Categoria de integracion"));
        new RecursoService().guardar("IT-R1", CAT, "Recurso de integracion");
    }

    @Test
    void crearYLeerCategoria() throws Exception {
        new CategoriaService().crear("Categoria IT");
        List<Categoria> encontradas = new CategoriaService().buscar("Categoria IT");
        assertEquals(1, encontradas.size());
        assertTrue(encontradas.get(0).getId().startsWith("CAT-"));
    }

    @Test
    void noSePuedeBorrarCategoriaConRecursos() throws Exception {
        crearCategoriaYRecurso();
        assertThrows(ValidacionException.class, () -> new CategoriaService().eliminar(CAT));
    }

    @Test
    void reservaAsignaRecursoYSegundaReservaTraslapadaFalla() throws Exception {
        crearCategoriaYRecurso();
        ReservaService service = new ReservaService();

        ResultadoReserva primera = service.crearReserva("111", "Prueba IT", fecha, "09:00", "10:00", List.of(CAT));
        assertTrue(primera.isExito());
        assertEquals("IT-R1", primera.getReserva().getRecursosIds().get(0));

        ResultadoReserva segunda = service.crearReserva("123", "Prueba IT 2", fecha, "09:30", "10:30", List.of(CAT));
        assertFalse(segunda.isExito());
        assertTrue(segunda.getCategoriasNoDisponibles().contains(CAT));

        // Se lee de vuelta desde la BD con sus listas
        Reserva leida = new ReservaDao().buscarPorId(primera.getReserva().getId());
        assertNotNull(leida);
        assertEquals(List.of(CAT), leida.getCategoriasIds());
    }

    @Test
    void estadisticaCuentaLaCategoriaReservada() throws Exception {
        crearCategoriaYRecurso();
        new ReservaService().crearReserva("111", "Prueba IT", fecha, "13:00", "14:00", List.of(CAT));
        Map<String, Integer> conteo = new ReservaDao().contarPorCategoria(LocalDate.parse(fecha), LocalDate.parse(fecha));
        assertEquals(Integer.valueOf(1), conteo.get("Categoria de integracion"));
    }
}