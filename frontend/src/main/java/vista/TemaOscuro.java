package vista;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class TemaOscuro {
    public static final Color FONDO = new Color(30, 30, 30);
    public static final Color FONDO_SECCION = new Color(45, 45, 45);
    public static final Color FONDO_CAMPO = new Color(55, 55, 55);
    public static final Color FONDO_BOTON = new Color(70, 70, 70);
    public static final Color TEXTO = new Color(230, 230, 230);
    public static final Color BORDE = new Color(90, 90, 90);
    public static final Color SELECCION = new Color(60, 90, 130);

    public static void aplicarAPanel(JPanel panel) { panel.setBackground(FONDO); }

    public static void aplicarASeccion(JPanel panel) {
        panel.setBackground(FONDO_SECCION);
        if (panel.getBorder() instanceof javax.swing.border.TitledBorder titulo) {
            titulo.setTitleColor(TEXTO);
        }
    }

    public static void aplicarAEtiqueta(JLabel label) { label.setForeground(TEXTO); }

    public static void aplicarACampoTexto(JTextField campo) {
        campo.setBackground(FONDO_CAMPO);
        campo.setForeground(TEXTO);
        campo.setCaretColor(TEXTO);
        campo.setBorder(BorderFactory.createLineBorder(BORDE));
    }

    public static void aplicarABoton(JButton boton) {
        boton.setBackground(FONDO_BOTON);
        boton.setForeground(TEXTO);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createLineBorder(BORDE));
    }

    public static void aplicarATabla(JTable tabla) {
        tabla.setBackground(FONDO_CAMPO);
        tabla.setForeground(TEXTO);
        tabla.setGridColor(BORDE);
        tabla.setSelectionBackground(SELECCION);
        tabla.setSelectionForeground(Color.white);
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(FONDO_BOTON);
        header.setForeground(TEXTO);
    }

    public static void aplicarAComboBox(JComboBox<?> combo) {
        combo.setBackground(FONDO_CAMPO);
        combo.setForeground(TEXTO);
    }

    public static void aplicarATabs(JTabbedPane tabs) {
        tabs.setBackground(FONDO_SECCION);
        tabs.setForeground(TEXTO);
    }
}