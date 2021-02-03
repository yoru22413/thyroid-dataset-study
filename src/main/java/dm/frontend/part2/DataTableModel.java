package dm.frontend.part2;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;

class DataTableModel extends DefaultTableModel {

    Color[] rowColors;

    public DataTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        this.rowColors = new Color[data.length];
        Arrays.fill(rowColors, Color.WHITE);
    }

    public void setRowColour(int row, Color c) {
        rowColors[row] = c;
        fireTableRowsUpdated(row, row);
    }

    public Color getRowColour(int row) {
        return rowColors[row];
    }

}
