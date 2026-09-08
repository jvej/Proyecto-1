package vista;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;


public class BarChartPanel extends JPanel {

    private Map<String, Integer> datos = new LinkedHashMap<>();
    private String titulo = "";

    private static final int MARGEN_IZQUIERDO = 40;
    private static final int MARGEN_INFERIOR = 50;
    private static final int MARGEN_SUPERIOR = 30;
    private static final int MARGEN_DERECHO = 20;

    public BarChartPanel(String titulo) {
        this.titulo = titulo;
        setPreferredSize(new Dimension(350, 250));
        setBackground(Color.WHITE);
    }

    public void setDatos(Map<String, Integer> datos) {
        this.datos = datos;
        repaint(); // fuerza a que Swing vuelva a llamar paintComponent con los datos nuevos
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        // Titulo
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(titulo, MARGEN_IZQUIERDO, 18);

        if (datos.isEmpty()) {
            g2.drawString("Sin datos para el rango seleccionado", MARGEN_IZQUIERDO, alto / 2);
            return;
        }

        int areaAncho = ancho - MARGEN_IZQUIERDO - MARGEN_DERECHO;
        int areaAlto = alto - MARGEN_SUPERIOR - MARGEN_INFERIOR;
        int origenY = MARGEN_SUPERIOR + areaAlto;

        // Eje Y y eje X
        g2.setColor(Color.BLACK);
        g2.drawLine(MARGEN_IZQUIERDO, MARGEN_SUPERIOR, MARGEN_IZQUIERDO, origenY);
        g2.drawLine(MARGEN_IZQUIERDO, origenY, ancho - MARGEN_DERECHO, origenY);

        int maximo = datos.values().stream().max(Integer::compareTo).orElse(1);
        if (maximo == 0) maximo = 1; // evita dividir entre cero si todas las cantidades son 0

        int cantidadBarras = datos.size();
        int anchoBarra = Math.max(15, areaAncho / (cantidadBarras * 2));
        int espacio = areaAncho / cantidadBarras;

        int x = MARGEN_IZQUIERDO + (espacio - anchoBarra) / 2;
        for (Map.Entry<String, Integer> entrada : datos.entrySet()) {
            int valor = entrada.getValue();
            int alturaBarra = (int) ((double) valor / maximo * areaAlto);

            g2.setColor(new Color(70, 130, 180)); // azul acero
            g2.fillRect(x, origenY - alturaBarra, anchoBarra, alturaBarra);
            g2.setColor(Color.BLACK);
            g2.drawRect(x, origenY - alturaBarra, anchoBarra, alturaBarra);

            // Cantidad arriba de la barra
            g2.drawString(String.valueOf(valor), x + anchoBarra / 2 - 4, origenY - alturaBarra - 5);

            // Etiqueta debajo del eje X (rotada si es muy larga, para que no se amontone)
            String etiqueta = entrada.getKey();
            Graphics2D copia = (Graphics2D) g2.create();
            copia.translate(x + anchoBarra / 2 + 4, origenY + 15);
            copia.rotate(Math.toRadians(45));
            copia.drawString(etiqueta, 0, 0);
            copia.dispose();

            x += espacio;
        }
    }
}
