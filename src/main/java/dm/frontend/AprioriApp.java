package dm.frontend;

import dm.backend.apriori.Algorithm;
import dm.backend.apriori.AprioriStruct;
import dm.backend.apriori.IntValueSet;
import dm.backend.table.ColumnType;
import dm.backend.table.Table;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JSlider slider2;
    private JLabel minSupLabel;
    private JButton viewAllFrequentPatternsButton;

    private AprioriStruct aps;
    private Algorithm algo;
    private int removePhase = 0;
    private ArrayList<IntValueSet> temp;

    private TableModel createTableModel(Table table, String[] columnNames){
        Object[][] data = new Object[table.height()][table.width()];
        for (int i = 0; i < table.height(); i++) {
            for (int j = 0; j < table.width(); j++) {
                data[i][j] = table.column(j).get(i);
            }
        }
        return new DataTableModel(data, columnNames);
    }

    private TableModel createTableModel(AprioriStruct aps){

        Object[][] data = new Object[aps.length][2];
        String s;
        for (int i = 0; i < aps.length; i++) {
            IntValueSet set = aps.get(i);
            data[i][0] = i+1;
            s = set.toString();
            s = s.substring(1, s.length()-1);
            data[i][1] = s;
        }
        return new DataTableModel(data, new String[]{"N°", "Items"});
    }

    private TableModel createTableModel(Map<IntValueSet, Integer> count){
        temp = new ArrayList<>();
        Object[][] data = new Object[count.size()][2];
        String s;
        int i = 0;
        for (Map.Entry<IntValueSet, Integer> entry :
                count.entrySet()) {
            temp.add(entry.getKey());
            s = entry.getKey().toString();
            s = s.substring(1, s.length()-1);
            data[i][0] = s;
            data[i][1] = entry.getValue();
            i++;
        }
        return new DataTableModel(data, new String[]{"Itemset", "Support"});
    }

    private TableModel createTableModel(List<IntValueSetInteger> count){
        temp = new ArrayList<>();
        Object[][] data = new Object[count.size()][2];
        String s;
        int i = 0;
        for (IntValueSetInteger ivsi : count) {
            temp.add(ivsi.ivs);
            s = ivsi.ivs.toString();
            s = s.substring(1, s.length()-1);
            data[i][0] = s;
            data[i][1] = ivsi.i;
            i++;
        }
        return new DataTableModel(data, new String[]{"Itemset", "Support"});
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

    public AprioriApp(){
        setEnabledPanel(panelStep1, true);
        setEnabledPanel(panelStep2, false);
        setEnabledPanel(panelStep3, false);
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
                for (int i = 1; i < table.width(); i++) {
                    table.setColumn(i, table.column(i).discretization(slider1.getValue()-1));
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
                nextStepButton.setEnabled(false);
                viewAllFrequentPatternsButton.setEnabled(false);
            }
        });
        slider2.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                String s = (int)Math.round((double)slider2.getValue()/100*aps.length) + " (" + slider2.getValue() + "%)";
                minSupLabel.setText(s);
            }
        });
        firstCountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algo = new Algorithm(aps, (double)slider2.getValue()/100);
                algo.step();
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
                if(removePhase == 0){
                    algo.step();
                    table1.setModel(createTableModel(algo.countCopy));
                    table1.getColumnModel().getColumn(1).setMaxWidth(80);
                    if(algo.finished){
                        nextStepButton.setText("Finished");
                        nextStepButton.setBackground(Color.GREEN);
                        nextStepButton.setEnabled(false);
                        scroll.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "Finished", TitledBorder.CENTER, TitledBorder.TOP));
                        viewAllFrequentPatternsButton.setEnabled(true);
                        return;
                    }
                    removePhase++;
                    scroll.setBorder(new TitledBorder(BorderFactory.createEtchedBorder(), "C" + algo.k, TitledBorder.CENTER, TitledBorder.TOP));
                    if(algo.toRemove.isEmpty()){
                        removePhase++;
                    }
                }
                else if(removePhase == 1){
                    DataTableModel dtm = (DataTableModel) table1.getModel();
                    HashSet<IntValueSet> remove = new HashSet<>(algo.toRemove);
                    for (int i = 0; i < temp.size(); i++) {
                        if(remove.contains(temp.get(i))){
                            dtm.setRowColour(i, Color.RED);
                        }
                    }
                    removePhase++;
                }
                else if(removePhase == 2){
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
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Apriori");
        frame.setContentPane(new AprioriApp().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        frame.setVisible(true);
    }

}
