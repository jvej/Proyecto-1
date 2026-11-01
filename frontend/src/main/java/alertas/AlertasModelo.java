package alertas;

import modelo.Alerta;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

// MODELO (MVC) del area de alertas: guarda la lista de alertas de la sesion
// actual. Al extender AbstractTableModel, cada vez que cambia "avisa" a la
// vista (fireTable...) y la JTable se repinta sola.
public class AlertasModelo extends AbstractTableModel {

    public static final int COL_ACTIVIDAD = 0;
    public static final int COL_VER = 1;
    public static final int COL_DESCARTAR = 2;

    private static final String[] COLUMNAS = {"Actividad", "Ver", "Desc."};

    private final List<Alerta> alertas = new ArrayList<>();

    public void agregar(Alerta a) {
        alertas.add(0, a); // la mas reciente arriba
        fireTableRowsInserted(0, 0);
    }

    public void remover(int fila) {
        if (fila < 0 || fila >= alertas.size()) return;
        alertas.remove(fila);
        fireTableRowsDeleted(fila, fila);
    }

    public void limpiar() {
        alertas.clear();
        fireTableDataChanged();
    }

    public Alerta getAlerta(int fila) {
        return (fila >= 0 && fila < alertas.size()) ? alertas.get(fila) : null;
    }

    public List<Alerta> getAlertas() { return new ArrayList<>(alertas); }

    @Override public int getRowCount() { return alertas.size(); }
    @Override public int getColumnCount() { return COLUMNAS.length; }
    @Override public String getColumnName(int col) { return COLUMNAS[col]; }
    @Override public boolean isCellEditable(int fila, int col) { return false; }

    @Override
    public Class<?> getColumnClass(int col) {
        return col == COL_ACTIVIDAD ? String.class : javax.swing.Icon.class;
    }

    @Override
    public Object getValueAt(int fila, int col) {
        return switch (col) {
            case COL_ACTIVIDAD -> alertas.get(fila).getTitulo();
            case COL_VER -> Iconos.VER;
            case COL_DESCARTAR -> Iconos.DESCARTAR;
            default -> null;
        };
    }
}