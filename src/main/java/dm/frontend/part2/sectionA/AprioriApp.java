package dm.frontend.part2.sectionA;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import dm.backend.apriori.Algorithm;
import dm.backend.apriori.AprioriStruct;
import dm.backend.apriori.AssociationRule;
import dm.backend.apriori.IntValueSet;
import dm.backend.table.ColumnType;
import dm.backend.table.Table;
import dm.frontend.part2.DataTableCellRenderer;
import dm.frontend.part2.DataTableModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

class IntValueSetInteger implements Comparable<IntValueSetInteger>{
    IntValueSet ivs;
    Integer i;

    public IntValueSetInteger(IntValueSet ivs, Integer i) {
        this.ivs = ivs;
        this.i = i;
    }

    @Override
    public int compareTo(IntValueSetInteger o) {
        return ivs.size() - o.ivs.size();
    }
}

public class AprioriApp {
    private JTable table1;
    public JPanel mainPanel;
    private JScrollPane scroll;
    private JButton buttonDiscretize;
    private JSlider slider1;
    private JButton buildItemsetTableButton;
    private JPanel panelStep1;
    private JPanel panelStep2;
    private JPanel panelStep3;
    private JButton firstCountButton;
    private JButton nextStepButton;
    private JSlider slider2;
    private JLabel minSupLabel;
    private JButton viewAllFrequentPatternsButton;
    private JSlider slider3;
    private JLabel minConfLabel;
    private JButton generateAssociationRulesButton;
    private JButton filterAssociationRulesButton;
    private JPanel panelStep4;
    private JLabel stepTime1Label;
    private JLabel stepTime2Label;
    private JLabel stepTime3Label;
    private JLabel stepTime4Label;
    private JLabel totalTimeLabel;

    private AprioriStruct aps;
    private Algorithm algo;
    private int removePhase = 0;
    private ArrayList<IntValueSet> temp;
    private ArrayList<AssociationRule> tempRule;
    private double totalTime = 0;
    private double stepTime = 0;

    private Color DARK_GREEN = new Color(0, 153, 0);
    private DecimalFormat df = new DecimalFormat("#.####");

    private TableModel createTableModel(Table table, String[] columnNames) {
        Object[][] data = new Object[table.height()][table.width()];
        for (int i = 0; i < table.height(); i++) {
            for (int j = 0; j < table.width(); j++) {
                data[i][j] = table.column(j).get(i);
            }
        }
        return new DataTableModel(data, columnNames);
    }

    private TableModel createTableModel(HashSet<AssociationRule> rules) {
        ArrayList<AssociationRule> ruleArrayList = new ArrayList<>();
        ruleArrayList.addAll(rules);
        DecimalFormat df = new DecimalFormat("#.##");
        Collections.sort(ruleArrayList);
        tempRule = ruleArrayList;
        Object[][] data = new Object[rules.size()][3];
        for (int i = 0; i < ruleArrayList.size(); i++) {
            data[i][0] = ruleArrayList.get(i).leftside.toString();
            data[i][1] = ruleArrayList.get(i).rightside.toString();
            data[i][2] = df.format(ruleArrayList.get(i).confidence * 100);
        }
        return new DataTableModel(data, new String[]{"Rule left side", "Rule right side", "Confidence (%)"});
    }

    private TableModel createTableModel(AprioriStruct aps) {

        Object[][] data = new Object[aps.length][2];
        String s;
        for (int i = 0; i < aps.length; i++) {
            IntValueSet set = aps.get(i);
            data[i][0] = i + 1;
            s = set.toString();
            data[i][1] = s;
        }
        return new DataTableModel(data, new String[]{"N°", "Items"});
    }

    private TableModel createTableModel(Map<IntValueSet, Integer> count) {
        temp = new ArrayList<>();
        Object[][] data = new Object[count.size()][2];
        String s;
        int i = 0;
        for (Map.Entry<IntValueSet, Integer> entry :
                count.entrySet()) {
            temp.add(entry.getKey());
            s = entry.getKey().toString();
            data[i][0] = s;
            data[i][1] = entry.getValue();
            i++;
        }
        return new DataTableModel(data, new String[]{"Itemset", "Support"});
    }

    private TableModel createTableModel(List<IntValueSetInteger> count) {
        temp = new ArrayList<>();
        Object[][] data = new Object[count.size()][2];
        String s;
        int i = 0;
        for (IntValueSetInteger ivsi : count) {
            temp.add(ivsi.ivs);
            s = ivsi.ivs.toString();
            data[i][0] = s;
            data[i][1] = ivsi.i;
            i++;
        }
        return new DataTableModel(data, new String[]{"Itemset", "Support"});
    }

    private void nextStep(JPanel p1, JPanel p2) {
        setEnabledPanel(p1, false);
        setEnabledPanel(p2, true);
    }

    private void setEnabledPanel(JPanel p, boolean e) {
        p.setEnabled(e);
        for (Component c :
                p.getComponents()) {
            c.setEnabled(e);
        }
    }

    public AprioriApp() {
        setEnabledPanel(panelStep1, true);
        setEnabledPanel(panelStep2, false);
        setEnabledPanel(panelStep3, false);
        setEnabledPanel(panelStep4, false);
        String[] columnNames = new String[]{"class", "t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"};
        Table table = Table.fromCsv("Thyroid_Dataset.txt",
                new ColumnType[]{ColumnType.INTEGER, ColumnType.INTEGER, ColumnType.DOUBLE, ColumnType.DOUBLE,
                        ColumnType.DOUBLE, ColumnType.DOUBLE},
                columnNames);
        table1.setModel(createTableModel(table, columnNames));
        table1.setDefaultRenderer(Object.class, new DataTableCellRenderer());

        buttonDiscretize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepTime = 0;
                long t1 = System.nanoTime();
                for (int i = 1; i < table.width(); i++) {
                    table.setColumn(i, table.column(i).discretization(slider1.getValue()));
                }
                long t2 = System.nanoTime();
                stepTime += (double) (t2 - t1) / 1000000000;
                table1.setModel(createTableModel(table, columnNames));
                nextStep(panelStep1, panelStep2);
                stepTime1Label.setText(stepTime1Label.getText() + df.format(stepTime) + "s");
                stepTime1Label.setForeground(DARK_GREEN);
                totalTime += stepTime;
            }
        });
        buildItemsetTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long t1 = System.nanoTime();
                aps = new AprioriStruct(table);
                long t2 = System.nanoTime();
                stepTime = (double) (t2 - t1) / 1000000000;
                table1.setModel(createTableModel(aps));
                table1.getColumnModel().getColumn(0).setMaxWidth(50);
                nextStep(panelStep2, panelStep3);
                nextStepButton.setEnabled(false);
                viewAllFrequentPatternsButton.setEnabled(false);
                totalTime += stepTime;
                stepTime2Label.setText(stepTime2Label.getText() + df.format(stepTime) + "s");
                stepTime2Label.setForeground(DARK_GREEN);
            }
        });
        slider2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                String s = (int) Math.round((double) slider2.getValue() / 100 * aps.length) + " (" + slider2.getValue() + "%)";
                minSupLabel.setText(s);
            }
        });
        firstCountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stepTime = 0;
                long t1 = System.nanoTime();
                algo = new Algorithm(aps, (double) slider2.getValue() / 100);
                algo.step();
                long t2 = System.nanoTime();
                stepTime += (double) (t2 - t1) / 1000000000;
                table1.setModel(createTableModel(algo.countCopy));
                table1.getColumnModel().getColumn(1).setMaxWidth(80);
                firstCountButton.setEnabled(false);
                nextStepButton.setEnabled(true);
                slider2.setEnabled(false);
                scroll.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "C" + algo.k, TitledBorder.CENTER, TitledBorder.TOP));
                removePhase++;
            }
        });
        nextStepButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (removePhase == 0) {
                    long t1 = System.nanoTime();
                    algo.step();
                    long t2 = System.nanoTime();
                    stepTime += (double) (t2 - t1) / 1000000000;
                    table1.setModel(createTableModel(algo.countCopy));
                    table1.getColumnModel().getColumn(1).setMaxWidth(80);
                    if (algo.finished) {
                        nextStepButton.setText("Finished");
                        nextStepButton.setBackground(Color.GREEN);
                        nextStepButton.setEnabled(false);
                        scroll.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Finished", TitledBorder.CENTER, TitledBorder.TOP));
                        viewAllFrequentPatternsButton.setEnabled(true);
                        return;
                    }
                    removePhase++;
                    scroll.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "C" + algo.k, TitledBorder.CENTER, TitledBorder.TOP));
                    if (algo.toRemove.isEmpty()) {
                        removePhase++;
                    }
                } else if (removePhase == 1) {
                    DataTableModel dtm = (DataTableModel) table1.getModel();
                    HashSet<IntValueSet> remove = new HashSet<>(algo.toRemove);
                    for (int i = 0; i < temp.size(); i++) {
                        if (remove.contains(temp.get(i))) {
                            dtm.setRowColour(i, Color.RED);
                        }
                    }
                    removePhase++;
                } else if (removePhase == 2) {
                    table1.setModel(createTableModel(algo.count));
                    table1.getColumnModel().getColumn(1).setMaxWidth(80);
                    scroll.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "L" + algo.k, TitledBorder.CENTER, TitledBorder.TOP));
                    removePhase = 0;
                }
            }
        });
        viewAllFrequentPatternsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<IntValueSetInteger> ivsi = new ArrayList<>();
                for (Map.Entry<IntValueSet, Integer> entry :
                        algo.frequentPatterns.entrySet()) {
                    ivsi.add(new IntValueSetInteger(entry.getKey(), entry.getValue()));
                }
                Collections.sort(ivsi);
                table1.setModel(createTableModel(ivsi));
                table1.getColumnModel().getColumn(1).setMaxWidth(80);
                viewAllFrequentPatternsButton.setEnabled(false);
                nextStep(panelStep3, panelStep4);
                stepTime3Label.setText(stepTime3Label.getText() + df.format(stepTime) + "s");
                stepTime3Label.setForeground(DARK_GREEN);
                totalTime += stepTime;
            }
        });
        slider3.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                String s = slider3.getValue() + "%";
                minConfLabel.setText(s);
            }
        });
        generateAssociationRulesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algo.minConf = (double) slider3.getValue() / 100;
                long t1 = System.nanoTime();
                algo.associationRules();
                long t2 = System.nanoTime();
                stepTime = (double) (t2 - t1) / 1000000000;

                table1.setModel(createTableModel(algo.allAssociationRules));
                table1.getColumnModel().getColumn(2).setPreferredWidth(100);
                table1.getColumnModel().getColumn(2).setMaxWidth(100);
                table1.getColumnModel().getColumn(2).setMinWidth(100);
                scroll.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Association rules (Not filtered)", TitledBorder.CENTER, TitledBorder.TOP));
                generateAssociationRulesButton.setEnabled(false);
                slider3.setEnabled(false);
            }
        });
        filterAssociationRulesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (removePhase == 0) {
                    DataTableModel dtm = (DataTableModel) table1.getModel();
                    for (int i = 0; i < tempRule.size(); i++) {
                        if (algo.toRemoveAR.contains(tempRule.get(i))) {
                            dtm.setRowColour(i, Color.RED);
                        }
                    }
                    removePhase++;
                } else if (removePhase == 1) {
                    table1.setModel(createTableModel(algo.associationRulesSatisf));
                    table1.getColumnModel().getColumn(2).setPreferredWidth(100);
                    table1.getColumnModel().getColumn(2).setMaxWidth(100);
                    table1.getColumnModel().getColumn(2).setMinWidth(100);
                    scroll.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Association rules (Filtered)", TitledBorder.CENTER, TitledBorder.TOP));
                    filterAssociationRulesButton.setEnabled(false);
                    filterAssociationRulesButton.setText("Finished");
                    filterAssociationRulesButton.setBackground(Color.GREEN);
                    stepTime4Label.setText(stepTime4Label.getText() + df.format(stepTime) + "s");
                    stepTime4Label.setForeground(DARK_GREEN);
                    removePhase = 0;
                    totalTime += stepTime;
                    totalTimeLabel.setText(totalTimeLabel.getText() + df.format(totalTime) + "s");
                    totalTimeLabel.setForeground(DARK_GREEN);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Apriori");
        frame.setContentPane(new AprioriApp().mainPanel);
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
        mainPanel.setLayout(new GridLayoutManager(8, 4, new Insets(5, 5, 5, 5), -1, -1));
        scroll = new JScrollPane();
        mainPanel.add(scroll, new GridConstraints(0, 0, 7, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(400, -1), new Dimension(200, 400), null, 0, false));
        scroll.setBorder(BorderFactory.createTitledBorder("Data"));
        table1 = new JTable();
        scroll.setViewportView(table1);
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(6, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, 1, new Dimension(100, -1), new Dimension(100, -1), new Dimension(100, -1), 0, false));
        panelStep1 = new JPanel();
        panelStep1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panelStep1, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(-1, 150), 0, false));
        panelStep1.setBorder(BorderFactory.createTitledBorder("1: Bin Descetization (except class)"));
        buttonDiscretize = new JButton();
        buttonDiscretize.setText("Discretize");
        panelStep1.add(buttonDiscretize, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slider1 = new JSlider();
        slider1.setExtent(0);
        slider1.setInverted(false);
        slider1.setMajorTickSpacing(1);
        slider1.setMaximum(10);
        slider1.setMinimum(2);
        slider1.setMinorTickSpacing(1);
        slider1.setPaintLabels(true);
        slider1.setPaintTicks(true);
        slider1.setSnapToTicks(true);
        slider1.setValue(5);
        panelStep1.add(slider1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Number of bins per column");
        panelStep1.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelStep2 = new JPanel();
        panelStep2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panelStep2.setEnabled(true);
        mainPanel.add(panelStep2, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, new Dimension(-1, 80), 0, false));
        panelStep2.setBorder(BorderFactory.createTitledBorder("2: Prepare for Apriori"));
        buildItemsetTableButton = new JButton();
        buildItemsetTableButton.setText("Build itemset table");
        panelStep2.add(buildItemsetTableButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelStep3 = new JPanel();
        panelStep3.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panelStep3, new GridConstraints(3, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelStep3.setBorder(BorderFactory.createTitledBorder("3: Apriori Algorithm"));
        firstCountButton = new JButton();
        firstCountButton.setText("First Count");
        panelStep3.add(firstCountButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nextStepButton = new JButton();
        nextStepButton.setText("Next Step");
        panelStep3.add(nextStepButton, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slider2 = new JSlider();
        slider2.setMajorTickSpacing(1);
        slider2.setMinorTickSpacing(1);
        slider2.setPaintLabels(false);
        slider2.setPaintTicks(false);
        slider2.setPaintTrack(true);
        slider2.setSnapToTicks(true);
        panelStep3.add(slider2, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Minimum support:");
        panelStep3.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        minSupLabel = new JLabel();
        minSupLabel.setText("108 (50%)");
        panelStep3.add(minSupLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        viewAllFrequentPatternsButton = new JButton();
        viewAllFrequentPatternsButton.setText("Show all frequent patterns");
        panelStep3.add(viewAllFrequentPatternsButton, new GridConstraints(4, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        panelStep4 = new JPanel();
        panelStep4.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panelStep4, new GridConstraints(4, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panelStep4.setBorder(BorderFactory.createTitledBorder("4 Association Rules"));
        final JLabel label3 = new JLabel();
        label3.setText("Minimum confidence");
        panelStep4.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        minConfLabel = new JLabel();
        minConfLabel.setText("50%");
        panelStep4.add(minConfLabel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        slider3 = new JSlider();
        slider3.setMajorTickSpacing(1);
        slider3.setMinorTickSpacing(1);
        slider3.setPaintLabels(false);
        slider3.setPaintTicks(false);
        slider3.setPaintTrack(true);
        slider3.setSnapToTicks(true);
        panelStep4.add(slider3, new GridConstraints(1, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        generateAssociationRulesButton = new JButton();
        generateAssociationRulesButton.setText("Generate association rules");
        panelStep4.add(generateAssociationRulesButton, new GridConstraints(2, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        filterAssociationRulesButton = new JButton();
        filterAssociationRulesButton.setText("Filter association rules");
        panelStep4.add(filterAssociationRulesButton, new GridConstraints(3, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(7, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        stepTime1Label = new JLabel();
        stepTime1Label.setText("Execution time (Step 1): ");
        panel1.add(stepTime1Label, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stepTime2Label = new JLabel();
        stepTime2Label.setText("Execution time (Step 2): ");
        panel1.add(stepTime2Label, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel2, new GridConstraints(7, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        stepTime3Label = new JLabel();
        stepTime3Label.setText("Execution time (Step 3): ");
        panel2.add(stepTime3Label, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stepTime4Label = new JLabel();
        stepTime4Label.setText("Execution time (Step 4): ");
        panel2.add(stepTime4Label, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(7, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel3, new GridConstraints(7, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        totalTimeLabel = new JLabel();
        totalTimeLabel.setText("Total execution time: ");
        panel3.add(totalTimeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
