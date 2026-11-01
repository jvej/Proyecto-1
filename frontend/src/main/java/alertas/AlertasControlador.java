package alertas;

import modelo.Alerta;
import proxy.ServiceProxy;

import javax.swing.*;

// CONTROLADOR (MVC) del area de alertas.
//  - Recibe las alertas que llegan por la red (desde el hilo del Proxy).
//  - Atiende los clicks de "ver" y "descartar" de la vista.
// El descarte es SOLO local: no se le pide nada al Backend.
public class AlertasControlador implements ServiceProxy.AlertaListener {

    private final AlertasModelo modelo;
    private final AlertasVista vista;

    public AlertasControlador(AlertasModelo modelo, AlertasVista vista) {
        this.modelo = modelo;
        this.vista = vista;
        vista.setControlador(this);
    }

    // Llega desde el hilo "Escucha-Alertas", NO desde el hilo de Swing.
    // Swing solo se puede tocar desde su propio hilo (EDT), por eso invokeLater.
    @Override
    public void alertaRecibida(Alerta alerta) {
        SwingUtilities.invokeLater(() -> modelo.agregar(alerta));
    }

    public void ver(int fila) {
        Alerta a = modelo.getAlerta(fila);
        if (a != null) vista.mostrarDetalle(a);
    }

    public void descartar(int fila) {
        Alerta a = modelo.getAlerta(fila);
        if (a != null && vista.confirmarDescarte(a)) modelo.remover(fila);
    }

    public AlertasModelo getModelo() { return modelo; }
}