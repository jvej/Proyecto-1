package alertas;

import modelo.Alerta;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertasModeloTest {

    private Alerta alerta(String actividad) {
        return new Alerta("RES-1", "Juan Mora", actividad, LocalDate.now(),
                LocalTime.of(8, 0), LocalTime.of(9, 0), List.of("Laptop #1"));
    }

    @Test
    void laAlertaMasNuevaQuedaArriba() {
        AlertasModelo m = new AlertasModelo();
        m.agregar(alerta("Primera"));
        m.agregar(alerta("Segunda"));
        assertEquals(2, m.getRowCount());
        assertEquals("Juan Mora - Segunda", m.getValueAt(0, AlertasModelo.COL_ACTIVIDAD));
    }

    @Test
    void descartarQuitaSoloEsaFila() {
        AlertasModelo m = new AlertasModelo();
        m.agregar(alerta("Primera"));
        m.agregar(alerta("Segunda"));
        m.remover(0);
        assertEquals(1, m.getRowCount());
        assertEquals("Primera", m.getAlerta(0).getActividad());
    }

    @Test
    void columnasDeIconos() {
        AlertasModelo m = new AlertasModelo();
        m.agregar(alerta("X"));
        assertSame(Iconos.VER, m.getValueAt(0, AlertasModelo.COL_VER));
        assertSame(Iconos.DESCARTAR, m.getValueAt(0, AlertasModelo.COL_DESCARTAR));
    }
}