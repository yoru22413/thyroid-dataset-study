package dm.frontend;

import dm.backend.apriori.AprioriStruct;
import dm.backend.apriori.IntValueSet;
import dm.backend.table.ColumnType;
import dm.backend.table.Table;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Apriori {
    private JTable table1;
    private JPanel mainPanel;
    private JScrollPane scroll;
    private JButton buttonDiscretize;
    private JSlider slider1;
    private JButton buildItemsetTableButton;
    private JPanel panelStep1;
    private JPanel panelStep2;
    private JPanel panelStep3;
    private JButton firstCountButton;
    private JButton nextStepButton;

    private AprioriStruct aps;

    private TableModel createTableModel(Table table, String[] columnNames){
        Object[][] data = new Object[table.height()][table.width()];
        for (int i = 0; i < table.height(); i++) {
            for (int j = 0; j < table.width(); j++) {
                data[i][j] = table.column(j).get(i);
            }
        }
        return new DefaultTableModel(data, columnNames);
    }

    private TableModel createTableModel(AprioriStruct aps){
        Object[][] data = new Object[aps.length][2];
        String s;
        for (int i = 0; i < aps.length; i++) {
            IntValueSet set = aps.get(i);
            data[i][0] = i;
            s = set.toString();
            s = s.substring(1, s.length()-1);
            data[i][1] = s;
        }
        DefaultTableModel tm = new DefaultTableModel(data, new String[]{"N°", "Itemset"});
        return tm;
    }

    private void nextStep(JPanel p1, JPanel p2){
        setEnabledPanel(p1, false);
        setEnabledPanel(p2, true);
    }

    private void setEnabledPanel(JPanel p, boolean e){
        p.setEnabled(e);
        for (Component c :
                p.getComponents()) {
            c.setEnabled(e);
        }
    }

    public Apriori(){
        setEnabledPanel(panelStep1, true);
        setEnabledPanel(panelStep2, false);
        setEnabledPanel(panelStep3, false);
        String[] columnNames = new String[]{"class", "t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"};
        Table table = Table.fromCsv("Thyroid_Dataset.txt",
                new ColumnType[]{ColumnType.INTEGER, ColumnType.INTEGER, ColumnType.DOUBLE, ColumnType.DOUBLE,
                        ColumnType.DOUBLE, ColumnType.DOUBLE},
                columnNames);
        table1.setModel(createTableModel(table, columnNames));

        buttonDiscretize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < table.width(); i++) {
                    table.setColumn(i, table.column(i).discretization(slider1.getValue()));
                }
                table1.setModel(createTableModel(table, columnNames));
                nextStep(panelStep1, panelStep2);
            }
        });
        buildItemsetTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                aps = new AprioriStruct(table);
                table1.setModel(createTableModel(aps));
                table1.getColumnModel().getColumn(0).setMaxWidth(50);
                nextStep(panelStep2, panelStep3);
            }
        });
        firstCountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Apriori");
        frame.setContentPane(new Apriori().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        frame.setVisible(true);
    }

}
