package dm.frontend.part2.sectionB;

import dm.backend.Metrics;
import dm.backend.Utils;
import dm.backend.clarans.CLARANS;
import dm.backend.kMeansMedoids.PointSet;
import dm.backend.kMeansMedoids.Point;
import dm.backend.table.*;
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
    private JLabel labelPrecision;
    private JPanel panelSampleSize;
    private JLabel labelSampleSize;
    private JComboBox comboBoxMaxNeighbors;
    private JComboBox comboBoxNumLocal;
    private JButton normalizeDataButton;
    private JLabel labelCluster;
    private JLabel labelExecutionTime;
    private JScrollPane scrollPaneTable;
    private JLabel labelRecall;
    private JLabel labelF1Score;
    private JLabel labelCost;
    private ButtonGroup buttonGroupAlgorithm;

    private Table table;
    private IntegerColumn classColumn;
    private Color defaultColor = new Color(238, 238, 238);
    private double timeAlgo;
    private Metrics metrics;
    private double cost = 0;

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
        comboBoxMaxNeighbors.setSelectedItem(50);
        comboBoxNumLocal.setSelectedItem(50);
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
                    clusters = algorithm.indexPoints;
                    cost = algorithm.cost;
                }
                else if(kMeansRadioButton.isSelected()){
                    ArrayList<Point> l = new ArrayList<>();
                    for (int i = 0; i < table.height(); i++) {
                        Object[] r = table.getRow(i).getData();
                        ArrayList<Float> list = new ArrayList<>();
                        for (int j = 0; j < table.width(); j++) {
                            list.add(((Double) r[j]).floatValue());
                        }
                        Point p = new Point(list);
                        l.add(p);
                    }
                    PointSet ps = new PointSet(l, 3);
                    long time1 = System.nanoTime();
                    ps.k_means();
                    long time2 = System.nanoTime();
                    timeAlgo = (double)(time2 - time1)/1000000000;
                    clusters = ps.c_cluster_id.stream().mapToInt(Integer::intValue).toArray();
                }
                else if(kMedoidsRadioButton.isSelected()){
                    ArrayList<Point> l = new ArrayList<>();
                    for (int i = 0; i < table.height(); i++) {
                        Object[] r = table.getRow(i).getData();
                        ArrayList<Float> list = new ArrayList<>();
                        for (int j = 0; j < table.width(); j++) {
                            list.add(((Double) r[j]).floatValue());
                        }
                        Point p = new Point(list);
                        l.add(p);
                    }
                    PointSet ps = new PointSet(l, 3);
                    long time1 = System.nanoTime();
                    ps.k_medoids();
                    long time2 = System.nanoTime();
                    timeAlgo = (double)(time2 - time1)/1000000000;
                    clusters = ps.m_cluster_id.stream().mapToInt(Integer::intValue).toArray();
                }
                labelExecutionTime.setText(labelExecutionTime.getText() + format.format(timeAlgo) + "s");
                labelExecutionTime.setForeground(Color.GREEN);
                algorithmStepButton.setText("Finished!");
                algorithmStepButton.setEnabled(false);
                a4CalculateMetricsButton.setEnabled(true);
                setEnablePanelRec(panelChoose, false);

                table.addColumn(0, classColumn);
                table1.setModel(createTableModel(table));

                DataTableModel dtm = (DataTableModel) table1.getModel();
                Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.GREEN};

                for (int i = 0; i < table.height(); i++) {
                    dtm.setRowColour(i, colors[clusters[i]]);
                }
                metrics = Utils.computeMetrics(classColumn.getData(), clusters);
                HashMap<Color, String> color2str = new HashMap<>();
                color2str.put(Color.RED, "RED");
                color2str.put(Color.GREEN, "GREEN");
                color2str.put(Color.BLUE, "BLUE");

                System.out.println(metrics);

                scrollPaneTable.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "CLUSTERS: Class 1 = " +
                        color2str.get(colors[metrics.interpretation[0]]) +
                        "   Class 2 = " + color2str.get(colors[metrics.interpretation[1]])
                        + "    Class 3 = " + color2str.get(colors[metrics.interpretation[2]]),
                        TitledBorder.CENTER, TitledBorder.TOP));
            }
        });
        a4CalculateMetricsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                labelCost.setText(labelCost.getText() + cost);
                labelF1Score.setText(labelF1Score.getText() + format.format(metrics.fmeasure*100) + "%");
                labelPrecision.setText(labelPrecision.getText() + format.format(metrics.precision*100) + "%");
                labelRecall.setText(labelRecall.getText() + format.format(metrics.recall*100) + "%");
                a4CalculateMetricsButton.setEnabled(false);
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
