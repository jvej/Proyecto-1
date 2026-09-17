package util;

import javax.swing.*;
import java.awt.Component;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class PDFReportUtil {

    private static final Charset PDF_CHARSET = Charset.forName("Cp1252"); // soporta tildes/ñ
    private static final int ANCHO_PAGINA = 612;   // Carta en puntos
    private static final int ALTO_PAGINA = 792;
    private static final int MARGEN = 40;
    private static final int FILAS_POR_PAGINA = 42;

    public static void generarReporteTabla(Component padre, String titulo, String[] columnas, List<String[]> filas) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(titulo.replace(" ", "_") + ".pdf"));
        int seleccion = chooser.showSaveDialog(padre);
        if (seleccion != JFileChooser.APPROVE_OPTION) return;

        String ruta = chooser.getSelectedFile().getAbsolutePath();
        if (!ruta.toLowerCase().endsWith(".pdf")) ruta += ".pdf";

        try {
            byte[] pdf = construirPdf(titulo, columnas, filas);
            try (FileOutputStream fos = new FileOutputStream(ruta)) {
                fos.write(pdf);
            }
            JOptionPane.showMessageDialog(padre, "Reporte generado en:\n" + ruta,
                    "PDF generado", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(padre, "Error generando el PDF: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static byte[] construirPdf(String titulo, String[] columnas, List<String[]> filas) throws Exception {
        List<String> lineasTabla = formatearTabla(columnas, filas);
        List<List<String>> paginas = paginar(lineasTabla);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        escribir(out, "%PDF-1.4\n");

        int totalPaginas = paginas.size();
        int objCatalogo = 1;
        int objPages = 2;
        int objFontTitulo = 3;
        int objFontTabla = 4;
        int primerObjPagina = 5;

        offsets.add(out.size());
        escribir(out, objCatalogo + " 0 obj\n<< /Type /Catalog /Pages " + objPages + " 0 R >>\nendobj\n");

        StringBuilder kids = new StringBuilder();
        for (int i = 0; i < totalPaginas; i++) {
            int objPagina = primerObjPagina + i * 2;
            kids.append(objPagina).append(" 0 R ");
        }
        offsets.add(out.size());
        escribir(out, objPages + " 0 obj\n<< /Type /Pages /Kids [ " + kids + "] /Count " + totalPaginas + " >>\nendobj\n");

        offsets.add(out.size());
        escribir(out, objFontTitulo + " 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>\nendobj\n");

        offsets.add(out.size());
        escribir(out, objFontTabla + " 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Courier /Encoding /WinAnsiEncoding >>\nendobj\n");

        for (int i = 0; i < totalPaginas; i++) {
            int objPagina = primerObjPagina + i * 2;
            int objContenido = objPagina + 1;

            byte[] contenido = generarContenidoPagina(titulo, paginas.get(i), i + 1, totalPaginas)
                    .getBytes(PDF_CHARSET);

            offsets.add(out.size());
            escribir(out, objPagina + " 0 obj\n<< /Type /Page /Parent " + objPages + " 0 R "
                    + "/Resources << /Font << /F1 " + objFontTitulo + " 0 R /F2 " + objFontTabla + " 0 R >> >> "
                    + "/MediaBox [0 0 " + ANCHO_PAGINA + " " + ALTO_PAGINA + "] "
                    + "/Contents " + objContenido + " 0 R >>\nendobj\n");

            offsets.add(out.size());
            escribir(out, objContenido + " 0 obj\n<< /Length " + contenido.length + " >>\nstream\n");
            out.write(contenido);
            escribir(out, "\nendstream\nendobj\n");
        }

        int xrefOffset = out.size();
        int totalObjetos = offsets.size();
        escribir(out, "xref\n0 " + (totalObjetos + 1) + "\n");
        escribir(out, "0000000000 65535 f \n");
        for (int offset : offsets) {
            escribir(out, String.format("%010d 00000 n \n", offset));
        }

        escribir(out, "trailer\n<< /Size " + (totalObjetos + 1) + " /Root " + objCatalogo + " 0 R >>\n");
        escribir(out, "startxref\n" + xrefOffset + "\n%%EOF");

        return out.toByteArray();
    }

    private static List<String> formatearTabla(String[] columnas, List<String[]> filas) {
        int[] anchos = new int[columnas.length];
        for (int i = 0; i < columnas.length; i++) {
            anchos[i] = columnas[i] == null ? 0 : columnas[i].length();
        }
        for (String[] fila : filas) {
            for (int i = 0; i < columnas.length && i < fila.length; i++) {
                String valor = fila[i] == null ? "" : fila[i];
                anchos[i] = Math.max(anchos[i], Math.min(valor.length(), 35));
            }
        }

        List<String> lineas = new ArrayList<>();
        lineas.add(formatearFila(columnas, anchos));

        StringBuilder separador = new StringBuilder();
        for (int ancho : anchos) {
            separador.append("-".repeat(ancho)).append("-+-");
        }
        lineas.add(separador.toString());

        for (String[] fila : filas) {
            lineas.add(formatearFila(fila, anchos));
        }
        return lineas;
    }

    private static String formatearFila(String[] valores, int[] anchos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < anchos.length; i++) {
            String valor = (valores != null && i < valores.length && valores[i] != null) ? valores[i] : "";
            if (valor.length() > anchos[i]) valor = valor.substring(0, anchos[i]);
            sb.append(String.format("%-" + anchos[i] + "s", valor));
            if (i < anchos.length - 1) sb.append(" | ");
        }
        return sb.toString();
    }

    private static List<List<String>> paginar(List<String> lineas) {
        List<List<String>> paginas = new ArrayList<>();
        for (int i = 0; i < lineas.size(); i += FILAS_POR_PAGINA) {
            paginas.add(lineas.subList(i, Math.min(i + FILAS_POR_PAGINA, lineas.size())));
        }
        if (paginas.isEmpty()) paginas.add(new ArrayList<>());
        return paginas;
    }

    private static String generarContenidoPagina(String titulo, List<String> lineasTabla, int numPagina, int totalPaginas) {
        StringBuilder sb = new StringBuilder();
        sb.append("BT\n");

        int y = ALTO_PAGINA - MARGEN;
        sb.append("/F1 16 Tf\n");
        sb.append("1 0 0 1 ").append(MARGEN).append(' ').append(y).append(" Tm\n");
        sb.append('(').append(escapar(titulo)).append(") Tj\n");

        if (totalPaginas > 1) {
            sb.append("/F2 8 Tf\n");
            sb.append("1 0 0 1 ").append(ANCHO_PAGINA - MARGEN - 60).append(' ').append(y).append(" Tm\n");
            sb.append('(').append("Pag. ").append(numPagina).append('/').append(totalPaginas).append(") Tj\n");
        }

        sb.append("/F2 9 Tf\n");
        int yTabla = y - 30;
        for (String linea : lineasTabla) {
            sb.append("1 0 0 1 ").append(MARGEN).append(' ').append(yTabla).append(" Tm\n");
            sb.append('(').append(escapar(linea)).append(") Tj\n");
            yTabla -= 14;
        }

        sb.append("ET");
        return sb.toString();
    }

    private static String escapar(String texto) {
        if (texto == null) return "";
        return texto.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static void escribir(ByteArrayOutputStream out, String texto) throws Exception {
        out.write(texto.getBytes(PDF_CHARSET));
    }
}