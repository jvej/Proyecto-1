package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BarChartPanel extends JPanel {

    private Map<String, Integer> datos = new LinkedHashMap<>();
    private String titulo = "";
    private boolean truncarEtiquetas = false;

    private final List<int[]> zonasBarras = new ArrayList<>();
    private final List<String> etiquetasCompletas = new ArrayList<>();

    private static final Color COLOR_FONDO       = new Color(248, 250, 252);
    private static final Color COLOR_BARRA_TOP   = new Color(99, 160, 230);
    private static final Color COLOR_BARRA_BOT   = new Color(50, 100, 180);
    private static final Color COLOR_CUADRICULA  = new Color(220, 225, 232);
    private static final Color COLOR_TITULO      = new Color(40, 55, 80);
    private static final Color COLOR_ETIQUETA    = new Color(60, 70, 90);
    private static final Color COLOR_VALOR       = new Color(30, 80, 160);
    private static final Color COLOR_EJE         = new Color(150, 160, 175);

    private static final int MARGEN_IZQ  = 48;
    private static final int MARGEN_DER  = 20;
    private static final int MARGEN_SUP  = 36;
    private static final int MARGEN_INF  = 36;

    public BarChartPanel(String titulo, boolean truncarEtiquetas) {
        this.titulo = titulo;
        this.truncarEtiquetas = truncarEtiquetas;
        setBackground(COLOR_FONDO);
        setBorder(BorderFactory.createLineBorder(new Color(210, 218, 228), 1));
        setToolTipText(""); // activa el sistema de tooltips
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                for (int i = 0; i < zonasBarras.size(); i++) {
                    int[] zona = zonasBarras.get(i);
                    if (e.getX() >= zona[0] && e.getX() <= zona[0] + zona[1]) {
                        setToolTipText(etiquetasCompletas.get(i));
                        return;
                    }
                }
                setToolTipText(null);
            }
        });
    }

    public BarChartPanel(String titulo) {
        this(titulo, false);
    }

    public void setDatos(Map<String, Integer> datos) {
        this.datos = datos;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,        RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,   RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,           RenderingHints.VALUE_RENDER_QUALITY);

        int ancho = getWidth();
        int alto  = getHeight();

        // Fondo redondeado
        g2.setColor(COLOR_FONDO);
        g2.fillRoundRect(0, 0, ancho - 1, alto - 1, 12, 12);

        // Título centrado
        Font fuenteTitulo = new Font("Segoe UI", Font.BOLD, 13);
        g2.setFont(fuenteTitulo);
        g2.setColor(COLOR_TITULO);
        FontMetrics fmTitulo = g2.getFontMetrics();
        g2.drawString(titulo, (ancho - fmTitulo.stringWidth(titulo)) / 2, 22);

        if (datos.isEmpty()) {
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            g2.setColor(COLOR_EJE);
            String msg = "Sin datos para el rango seleccionado";
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(msg, (ancho - fm.stringWidth(msg)) / 2, alto / 2);
            return;
        }

        Font fuenteEtiqueta = new Font("Segoe UI", Font.PLAIN, 11);
        g2.setFont(fuenteEtiqueta);
        FontMetrics fmEtiq = g2.getFontMetrics();

        int cantidadBarras = datos.size();
        int areaAncho = ancho - MARGEN_IZQ - MARGEN_DER;
        int espacioPorBarra = areaAncho / cantidadBarras;
        int margenInfDinamico = MARGEN_INF;

        int areaAlto = alto - MARGEN_SUP - margenInfDinamico;
        int origenY  = MARGEN_SUP + areaAlto;

        int maximo = datos.values().stream().max(Integer::compareTo).orElse(1);
        if (maximo == 0) maximo = 1;

        int divisiones = Math.min(4, maximo);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        FontMetrics fmY = g2.getFontMetrics();
        for (int i = 1; i <= divisiones; i++) {
            int yLinea = origenY - (int) ((double) i / divisiones * areaAlto);
            g2.setColor(COLOR_CUADRICULA);
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4, 3}, 0));
            g2.drawLine(MARGEN_IZQ, yLinea, ancho - MARGEN_DER, yLinea);

            int valorY = (int) Math.round((double) i / divisiones * maximo);
            String labelY = String.valueOf(valorY);
            g2.setColor(COLOR_ETIQUETA);
            g2.setStroke(new BasicStroke(1f));
            g2.drawString(labelY, MARGEN_IZQ - fmY.stringWidth(labelY) - 4,
                    yLinea + fmY.getAscent() / 2 - 1);
        }

        // Ejes
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(COLOR_EJE);
        g2.drawLine(MARGEN_IZQ, MARGEN_SUP, MARGEN_IZQ, origenY);
        g2.drawLine(MARGEN_IZQ, origenY, ancho - MARGEN_DER, origenY);

        // Barras
        int anchoBarra = Math.max(18, Math.min(52, espacioPorBarra - 16));
        int x = MARGEN_IZQ + (espacioPorBarra - anchoBarra) / 2;

        Font fuenteValor = new Font("Segoe UI", Font.BOLD, 11);
        int maxEtiqDisponible = espacioPorBarra - 4;

        zonasBarras.clear();
        etiquetasCompletas.clear();

        for (Map.Entry<String, Integer> entrada : datos.entrySet()) {
            int valor       = entrada.getValue();
            int alturaBarra = (int) ((double) valor / maximo * areaAlto);
            int yBarra      = origenY - alturaBarra;

            // Gradiente vertical en la barra
            GradientPaint gradiente = new GradientPaint(
                    x, yBarra,  COLOR_BARRA_TOP,
                    x, origenY, COLOR_BARRA_BOT);
            g2.setPaint(gradiente);
            g2.fill(new RoundRectangle2D.Float(x, yBarra, anchoBarra, alturaBarra, 6, 6));

            // Borde sutil
            g2.setColor(new Color(40, 90, 160, 80));
            g2.setStroke(new BasicStroke(1f));
            g2.draw(new RoundRectangle2D.Float(x, yBarra, anchoBarra, alturaBarra, 6, 6));

            // Valor encima de la barra
            g2.setFont(fuenteValor);
            g2.setColor(COLOR_VALOR);
            FontMetrics fmVal = g2.getFontMetrics();
            String sValor = String.valueOf(valor);
            g2.drawString(sValor, x + (anchoBarra - fmVal.stringWidth(sValor)) / 2, yBarra - 5);

            g2.setFont(fuenteEtiqueta);
            g2.setColor(COLOR_ETIQUETA);
            String etiquetaCompleta = entrada.getKey();
            String etiqueta = truncarEtiquetas
                    ? truncar(etiquetaCompleta, fmEtiq, maxEtiqDisponible)
                    : etiquetaCompleta;
            int etiqX = x + (espacioPorBarra - fmEtiq.stringWidth(etiqueta)) / 2;
            g2.drawString(etiqueta, etiqX, origenY + 16);

            zonasBarras.add(new int[]{x, anchoBarra});
            etiquetasCompletas.add(etiquetaCompleta);

            x += espacioPorBarra;
        }
    }

    private String truncar(String texto, FontMetrics fm, int maxAncho) {
        if (fm.stringWidth(texto) <= maxAncho) return texto;
        String puntos = "...";
        while (!texto.isEmpty() && fm.stringWidth(texto + puntos) > maxAncho)
            texto = texto.substring(0, texto.length() - 1);
        return texto + puntos;
    }
}
