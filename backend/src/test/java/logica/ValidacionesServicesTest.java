package logica;

import modelo.ValidacionException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ValidacionesServicesTest {

    @Test
    void categoriaSinDescripcionLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> new CategoriaService().crear("   "));
    }

    @Test
    void funcionarioSinIdLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> new FuncionarioService().crear("", "Ana", "8888", "clave"));
    }

    @Test
    void funcionarioConTelefonoInvalidoLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> new FuncionarioService().crear("55", "Ana", "abc", "clave"));
    }

    @Test
    void recursoSinCategoriaLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> new RecursoService().guardar("R1", "", "Laptop"));
    }

    @Test
    void loginConCamposVaciosLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> new UsuarioService().autenticar("", ""));
    }

    @Test
    void cambiarClaveConConfirmacionDistintaLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> new UsuarioService().cambiarClave("111", "1234", "nueva", "otra"));
    }

    @Test
    void rangoDeEstadisticasInvertidoLanzaExcepcion() {
        assertThrows(ValidacionException.class, () -> new EstadisticaService().validarRango("2026-09-30", "2026-09-01"));
    }

    @Test
    void rangoDeEstadisticasValido() throws Exception {
        LocalDate[] r = new EstadisticaService().validarRango("2026-09-01", "2026-09-30");
        assertEquals(LocalDate.of(2026, 9, 1), r[0]);
        assertEquals(LocalDate.of(2026, 9, 30), r[1]);
    }

    @Test
    void lunesDeLaSemana() {
        // 2026-11-05 es jueves -> su lunes es 2026-11-02
        assertEquals(LocalDate.of(2026, 11, 2), ActividadService.calcularLunesDeSemana(LocalDate.of(2026, 11, 5)));
        assertEquals(LocalDate.of(2026, 11, 2), ActividadService.calcularLunesDeSemana(LocalDate.of(2026, 11, 2)));
    }
}