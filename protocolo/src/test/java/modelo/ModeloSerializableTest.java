package modelo;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Todo lo que viaja por el socket debe poder "serializarse" (convertirse a bytes y volver).
class ModeloSerializableTest {

    @SuppressWarnings("unchecked")
    private <T> T idaYVuelta(T objeto) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) { out.writeObject(objeto); }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (T) in.readObject();
        }
    }

    @Test
    void reservaSobreviveSerializacion() throws Exception {
        Reserva r = new Reserva("RES-000001", "111", "Reunion", LocalDate.of(2026, 11, 2),
                LocalTime.of(9, 0), LocalTime.of(10, 0), List.of("CAT-000001"), List.of("S1-001"), "ACTIVA");
        Reserva copia = idaYVuelta(r);
        assertEquals("RES-000001", copia.getId());
        assertEquals(LocalTime.of(9, 0), copia.getHoraInicio());
        assertEquals("S1-001", copia.getRecursosIds().get(0));
    }

    @Test
    void alertaArmaElTituloConEncargadoYActividad() throws Exception {
        Alerta a = new Alerta("RES-1", "Juan Mora", "Reunion clientes", LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(10, 0), List.of("Laptop #1"));
        assertEquals("Juan Mora - Reunion clientes", a.getTitulo());
        assertEquals("Juan Mora - Reunion clientes", idaYVuelta(a).getTitulo());
    }

    @Test
    void resultadoReservaFalloGuardaCategorias() throws Exception {
        ResultadoReserva r = idaYVuelta(ResultadoReserva.fallo(List.of("CAT-1", "CAT-2")));
        assertFalse(r.isExito());
        assertEquals(2, r.getCategoriasNoDisponibles().size());
    }
}