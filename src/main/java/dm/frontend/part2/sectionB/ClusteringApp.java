package dm.frontend.part2.sectionB;

import dm.backend.Combinations;
import dm.backend.Metrics;
import dm.backend.Utils;
import dm.backend.clarans.CLARANS;
import dm.backend.table.Column;
import dm.backend.table.ColumnType;
import dm.backend.table.IntegerColumn;
import dm.backend.table.Table;
import dm.frontend.part2.DataTableCellRenderer;
import dm.frontend.part2.DataTableModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ClusteringApp {
    private JTable table1;
    private JPanel mainPanel;
    private JPanel panelChoose;
    private JRadioButton kMeansRadioButton;
    private JRadioButton kMedoidsRadioButton;
    private JRadioButton CLARANSRadioButton;
    private JButton algorithmStepButton;
    private JSlider slider1;
    private JButton a4CalculateMetricsButton;
    private JLabel precision;
    private JPanel panelSampleSize;
    private JLabel labelSampleSize;
    private JComboBox comboBoxMaxNeighbors;
    private JComboBox comboBoxNumLocal;
    private JButton normalizeDataButton;
    private JLabel labelCluster;
    private JLabel labelExecutionTime;
    private JScrollPane scrollPaneTable;
    private ButtonGroup buttonGroupAlgorithm;

    private Table table;
    private IntegerColumn classColumn;
    private Color defaultColor = new Color(238, 238, 238);
    private double timeAlgo;
    private Metrics metrics;

    private DecimalFormat format = new DecimalFormat("#.###");

    private static void setEnablePanelRec(JPanel p, boolean enabled){
        p.setEnabled(enabled);
        for (Component c :
                p.getComponents()) {
            c.setEnabled(enabled);
            if(c instanceof JPanel){
                setEnablePanelRec((JPanel) c, enabled);
            }
        }
    }

    private TableModel createTableModel(Table table){
        ArrayList<String> columnNames = new ArrayList<>();
        Object[][] data = new Object[table.height()][table.width()];
        for (int i = 0; i < table.width(); i++) {
            Column c = table.column(i);
            columnNames.add(c.getName());
            for (int j = 0; j < table.height(); j++) {
                data[j][i] = format.format(c.get(j));
            }
        }
        return new DataTableModel(data, columnNames.toArray());
    }

    private void algorithmStepButtonCheck(){
        algorithmStepButton.setEnabled(buttonGroupAlgorithm.getSelection() != null);
    }

    public ClusteringApp(){
        String[] columnNames = new String[]{"class", "t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"};
        table = Table.fromCsv("Thyroid_Dataset.txt",
                new ColumnType[]{ColumnType.INTEGER, ColumnType.INTEGER, ColumnType.DOUBLE, ColumnType.DOUBLE,
                        ColumnType.DOUBLE, ColumnType.DOUBLE},
                columnNames);
        classColumn = table.intColumn(0);
        table.removeColumn(0);
        for (int i = 0; i < table.width(); i++) {
            table.setColumn(i, table.column(i).toDoubleColumn());
        }
        table1.setModel(createTableModel(table));
        table1.setDefaultRenderer(Object.class, new DataTableCellRenderer());

        for (int i = 1; i <= 200; i++) {
            comboBoxNumLocal.addItem(i);
            comboBoxMaxNeighbors.addItem(i);
        }

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithmStepButtonCheck();
                for (Component c : panelSampleSize.getComponents()){
                    c.setEnabled(CLARANSRadioButton.isSelected());
                }
                panelSampleSize.setEnabled(CLARANSRadioButton.isSelected());
            }
        };
        kMeansRadioButton.addActionListener(al);
        kMedoidsRadioButton.addActionListener(al);
        CLARANSRadioButton.addActionListener(al);
        normalizeDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < table.width(); i++) {
                    table.setColumn(i, table.doubleColumn(i).normalization());
                }
                table1.setModel(createTableModel(table));
                normalizeDataButton.setEnabled(false);
                setEnablePanelRec(panelChoose, true);
                kMeansRadioButton.doClick();
            }
        });
        algorithmStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] clusters = new int[0];
                int[] centers;
                if(CLARANSRadioButton.isSelected()){
                    CLARANS algorithm = new CLARANS(table, 3, (int)comboBoxNumLocal.getSelectedItem(),
                            (int)comboBoxMaxNeighbors.getSelectedItem());
                    long time1 = System.nanoTime();
                    algorithm.run();
                    long time2 = System.nanoTime();
                    timeAlgo = (double)(time2 - time1)/1000000000;
                    centers = algorithm.indexMedoids;
                    clusters = algorithm.indexPoints;
                }
                labelExecutionTime.setText(labelExecutionTime.getText() + format.format(timeAlgo) + "s");
                labelExecutionTime.setForeground(Color.GREEN);
                algorithmStepButton.setText("Finished!");
                algorithmStepButton.setEnabled(false);
                a4CalculateMetricsButton.setEnabled(true);
                setEnablePanelRec(panelChoose, false);
                DataTableModel dtm = (DataTableModel) table1.getModel();
                HashMap<Integer, Color> colors = new HashMap<>();
                colors.put(0, Color.RED);
                colors.put(1, Color.BLUE);
                colors.put(2, Color.GREEN);

                for (int i = 0; i < table.height(); i++) {
                    dtm.setRowColour(i, colors.get(clusters[i]));
                }
                metrics = Utils.computeMetrics(classColumn.getData(), clusters);

                scrollPaneTable.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Class 1 : " +
                        colors.get(metrics.interpretation[0]).toString() +
                        "   Class 2 : " + colors.get(metrics.interpretation[1]).toString()
                        + "    Class 3 : " + colors.get(metrics.interpretation[2]).toString(),
                        TitledBorder.CENTER, TitledBorder.TOP));
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Clustering");
        frame.setContentPane(new ClusteringApp().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
