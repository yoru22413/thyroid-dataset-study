package dm.frontend.part2.sectionB;

import dm.backend.table.Column;
import dm.backend.table.ColumnType;
import dm.backend.table.Table;
import dm.frontend.part2.DataTableCellRenderer;
import dm.frontend.part2.DataTableModel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClusteringApp {
    private JTable table1;
    private JPanel mainPanel;
    private JPanel panelChoose;
    private JRadioButton kMeansRadioButton;
    private JRadioButton kMedoidsRadioButton;
    private JRadioButton CLARANSRadioButton;
    private JSlider sliderNumClusters;
    private JButton algorithmStepButton;
    private JSlider slider1;
    private JButton calculateMetricsButton;
    private JLabel precision;
    private JPanel panelSampleSize;
    private JLabel labelSampleSize;
    private ButtonGroup buttonGroupAlgorithm;

    private Table table;
    private Color defaultColor = new Color(238, 238, 238);

    private TableModel createTableModel(Table table){
        ArrayList<String> columnNames = new ArrayList<>();
        Object[][] data = new Object[table.height()][table.width()];
        for (int i = 0; i < table.width(); i++) {
            Column c = table.column(i);
            columnNames.add(c.getName());
            for (int j = 0; j < table.height(); j++) {
                data[j][i] = c.get(i);
            }
        }
        return new DataTableModel(data, columnNames.toArray());
    }

    private void algorithmStepButtonCheck(){
        algorithmStepButton.setEnabled((!CLARANSRadioButton.isSelected() || slider1.getValue() > 0) &&
                buttonGroupAlgorithm.getSelection() != null);
    }

    public ClusteringApp(){
        String[] columnNames = new String[]{"class", "t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"};
        table = Table.fromCsv("Thyroid_Dataset.txt",
                new ColumnType[]{ColumnType.INTEGER, ColumnType.INTEGER, ColumnType.DOUBLE, ColumnType.DOUBLE,
                        ColumnType.DOUBLE, ColumnType.DOUBLE},
                columnNames);
        table.removeColumn(0);
        table1.setModel(createTableModel(table));
        table1.setDefaultRenderer(Object.class, new DataTableCellRenderer());

        slider1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int value = slider1.getValue();
                String s = "Sample size : " + value + "%";
                labelSampleSize.setText(s);
                if (value == 0){
                    labelSampleSize.setForeground(Color.RED);
                    algorithmStepButtonCheck();
                }
                else{
                    labelSampleSize.setForeground(Color.BLACK);
                    algorithmStepButtonCheck();
                }
            }
        });

        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                algorithmStepButtonCheck();
                for (Component c : panelSampleSize.getComponents()){
                    c.setEnabled(CLARANSRadioButton.isSelected());
                }
            }
        };
        kMeansRadioButton.addActionListener(al);
        kMedoidsRadioButton.addActionListener(al);
        CLARANSRadioButton.addActionListener(al);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Apriori");
        frame.setContentPane(new ClusteringApp().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,600);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}
