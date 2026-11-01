package alertas;

import javax.swing.*;
import java.awt.*;

// Iconos dibujados con codigo (asi no dependemos de archivos de imagen).
public final class Iconos {
    private Iconos() { }

    // Icono "ver": una carpetita/documento azul
    public static final Icon VER = new Icon() {
        @Override public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(30, 110, 210));
            g.fillRoundRect(x + 1, y + 4, 14, 10, 3, 3);
            g.fillRoundRect(x + 1, y + 2, 6, 4, 2, 2);
            g.setColor(Color.WHITE);
            g.fillRect(x + 4, y + 7, 8, 1);
            g.fillRect(x + 4, y + 10, 6, 1);
            g.dispose();
        }
        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 16; }
    };

    // Icono "descartar": circulo rojo con una raya blanca
    public static final Icon DESCARTAR = new Icon() {
        @Override public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(220, 40, 40));
            g.fillOval(x + 1, y + 1, 14, 14);
            g.setColor(Color.WHITE);
            g.fillRect(x + 4, y + 7, 8, 2);
            g.dispose();
        }
        @Override public int getIconWidth() { return 16; }
        @Override public int getIconHeight() { return 16; }
    };
}