package dm.frontend.part2.sectionB;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import dm.backend.Utils;
import dm.backend.clarans.CLARANS;
import dm.backend.kMeans.PointSet;
import dm.backend.kMeans.Point;
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
import java.util.Arrays;

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
    private JLabel labelExecutionTime;
    private JScrollPane scrollPaneTable;
    private JLabel labelRecall;
    private JLabel labelF1Score;
    private JLabel labelCost;
    private JButton retryButton;
    private ButtonGroup buttonGroupAlgorithm;
    private Color[] colorClusters = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.CYAN,
            Color.MAGENTA, Color.ORANGE, Color.PINK, Color.YELLOW, Color.LIGHT_GRAY, Color.GRAY};

    private Table table;
    private IntegerColumn classColumn;
    private Color defaultColor = new Color(238, 238, 238);
    private double timeAlgo;
    private double f1score = 0;
    private double cost = 0;

    private DecimalFormat format = new DecimalFormat("#.###");

    private static void setEnablePanelRec(JPanel p, boolean enabled) {
        p.setEnabled(enabled);
        for (Component c :
                p.getComponents()) {
            c.setEnabled(enabled);
            if (c instanceof JPanel) {
                setEnablePanelRec((JPanel) c, enabled);
            }
        }
    }

    private TableModel createTableModel(Table table) {
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

    private void algorithmStepButtonCheck() {
        algorithmStepButton.setEnabled(buttonGroupAlgorithm.getSelection() != null);
    }

    public ClusteringApp() {
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
        comboBoxMaxNeighbors.setSelectedItem(40);
        comboBoxNumLocal.setSelectedItem(15);
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithmStepButtonCheck();
                for (Component c : panelSampleSize.getComponents()) {
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
                int[] clusters = null;
                int numCluster = slider1.getValue();
                if (CLARANSRadioButton.isSelected()) {
                    CLARANS algorithm = new CLARANS(table, numCluster, (int) comboBoxNumLocal.getSelectedItem(),
                            (int) comboBoxMaxNeighbors.getSelectedItem());
                    long time1 = System.nanoTime();
                    algorithm.run();
                    long time2 = System.nanoTime();
                    timeAlgo = (double) (time2 - time1) / 1000000000;
                    clusters = algorithm.indexPoints;
                    cost = algorithm.cost;
                } else if (kMeansRadioButton.isSelected()) {
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
                    PointSet ps = new PointSet(l, numCluster);
                    long time1 = System.nanoTime();
                    ps.k_means();
                    long time2 = System.nanoTime();
                    timeAlgo = (double) (time2 - time1) / 1000000000;
                    clusters = ps.c_cluster_id.stream().mapToInt(Integer::intValue).toArray();
                    cost = ps.cost;
                } else if (kMedoidsRadioButton.isSelected()) {
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
                    PointSet ps = new PointSet(l, numCluster);
                    long time1 = System.nanoTime();
                    ps.k_medoids();
                    long time2 = System.nanoTime();
                    timeAlgo = (double) (time2 - time1) / 1000000000;
                    clusters = ps.m_cluster_id.stream().mapToInt(Integer::intValue).toArray();
                    cost = ps.cost;
                }
                labelExecutionTime.setText(labelExecutionTime.getText() + format.format(timeAlgo) + "s");
                labelExecutionTime.setForeground(Color.GREEN);
                algorithmStepButton.setText("Finished!");
                algorithmStepButton.setEnabled(false);
                setEnablePanelRec(panelChoose, false);

                table.addColumn(0, classColumn);
                table1.setModel(createTableModel(table));

                DataTableModel dtm = (DataTableModel) table1.getModel();

                int[] copy = Arrays.copyOf(classColumn.getData(), classColumn.getData().length);
                for (int i = 0; i < copy.length; i++) {
                    copy[i]--;
                }
                f1score = Utils.computeMetrics(copy, clusters);
                for (int i = 0; i < table.height(); i++) {
                    dtm.setRowColour(i, colorClusters[clusters[i]]);
                }
                System.out.println(f1score);

                scrollPaneTable.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Clusters",
                        TitledBorder.CENTER, TitledBorder.TOP));
                labelCost.setText(labelCost.getText() + cost);
                labelF1Score.setText(labelF1Score.getText() + format.format(f1score * 100) + "%");
                retryButton.setEnabled(true);
            }

        });
        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retryButton.setEnabled(false);
                table.removeColumn(0);
                table1.setModel(createTableModel(table));
                setEnablePanelRec(panelChoose, true);
                setEnablePanelRec(panelSampleSize, false);
                if (CLARANSRadioButton.isSelected()) {
                    CLARANSRadioButton.doClick();
                }
                scrollPaneTable.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Data",
                        TitledBorder.CENTER, TitledBorder.TOP));
                algorithmStepButton.setText("3: Run the algorithm");
                algorithmStepButton.setEnabled(true);
                labelF1Score.setText("F1-Score : ");
                labelCost.setText("Cost : ");
                labelExecutionTime.setText("Execution time : ");
                labelExecutionTime.setForeground(Color.BLACK);
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Clustering");
        frame.setContentPane(new ClusteringApp().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(5, 2, new Insets(10, 10, 10, 10), -1, -1));
        scrollPaneTable = new JScrollPane();
        mainPanel.add(scrollPaneTable, new GridConstraints(0, 0, 4, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPaneTable.setBorder(BorderFactory.createTitledBorder(null, "Data", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION));
        table1 = new JTable();
        scrollPaneTable.setViewportView(table1);
        panelChoose = new JPanel();
        panelChoose.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        panelChoose.setEnabled(false);
        mainPanel.add(panelChoose, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelChoose.setBorder(BorderFactory.createTitledBorder("2: Algorithm parameters"));
        kMeansRadioButton = new JRadioButton();
        kMeansRadioButton.setEnabled(false);
        kMeansRadioButton.setText("K-Means");
        panelChoose.add(kMeansRadioButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        kMedoidsRadioButton = new JRadioButton();
        kMedoidsRadioButton.setEnabled(false);
        kMedoidsRadioButton.setText("K-Medoids");
        panelChoose.add(kMedoidsRadioButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        CLARANSRadioButton = new JRadioButton();
        CLARANSRadioButton.setEnabled(false);
        CLARANSRadioButton.setText("CLARANS");
        panelChoose.add(CLARANSRadioButton, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelSampleSize = new JPanel();
        panelSampleSize.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelSampleSize.setEnabled(false);
        panelChoose.add(panelSampleSize, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelSampleSize.setBorder(BorderFactory.createTitledBorder("CLARANS parameters"));
        labelSampleSize = new JLabel();
        labelSampleSize.setEnabled(false);
        labelSampleSize.setText("Maximum number of neighbors examined");
        panelSampleSize.add(labelSampleSize, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxMaxNeighbors = new JComboBox();
        comboBoxMaxNeighbors.setEnabled(false);
        panelSampleSize.add(comboBoxMaxNeighbors, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setEnabled(false);
        label1.setText("Number of iterations");
        panelSampleSize.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxNumLocal = new JComboBox();
        comboBoxNumLocal.setEnabled(false);
        panelSampleSize.add(comboBoxNumLocal, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slider1 = new JSlider();
        slider1.setMajorTickSpacing(1);
        slider1.setMaximum(10);
        slider1.setMinimum(1);
        slider1.setPaintLabels(true);
        slider1.setPaintTicks(true);
        slider1.setSnapToTicks(false);
        slider1.setValue(3);
        panelChoose.add(slider1, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Number of clusters");
        panelChoose.add(label2, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        algorithmStepButton = new JButton();
        algorithmStepButton.setEnabled(false);
        algorithmStepButton.setText("3: Run the algorithm");
        mainPanel.add(algorithmStepButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        labelExecutionTime = new JLabel();
        labelExecutionTime.setText("Execution time : ");
        panel1.add(labelExecutionTime, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelF1Score = new JLabel();
        labelF1Score.setText("F1-Score : ");
        panel1.add(labelF1Score, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelCost = new JLabel();
        labelCost.setText("Cost : ");
        panel1.add(labelCost, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel2, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder("1: Data normalization (Z-Score)"));
        normalizeDataButton = new JButton();
        normalizeDataButton.setText("Normalize Data");
        panel2.add(normalizeDataButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        retryButton = new JButton();
        retryButton.setEnabled(false);
        retryButton.setText("Retry?");
        mainPanel.add(retryButton, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonGroupAlgorithm = new ButtonGroup();
        buttonGroupAlgorithm.add(kMeansRadioButton);
        buttonGroupAlgorithm.add(kMedoidsRadioButton);
        buttonGroupAlgorithm.add(CLARANSRadioButton);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

}
