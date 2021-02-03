package dm.frontend.part1;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class WordWrapCellRenderer extends JTextArea implements TableCellRenderer {
    WordWrapCellRenderer() {
        setLineWrap(true);
        setWrapStyleWord(true);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value.toString());
        setSize(table.getColumnModel().getColumn(column).getWidth(), getPreferredSize().height);
        if (table.getRowHeight(row) != getPreferredSize().height) {
            table.setRowHeight(row, getPreferredSize().height);
        }
        return this;
    }
}

public class MainMenu {
    //description des données
    //Affichage
    //Stats Attributs
    // Données Symetriques
    // Boites a moustaches
    // afficher les histogrammes
    // Affichages des diagrammes
    // Existences de correlations
    public static void main(String[] args){

        final JFrame f=new JFrame("Menu Principal");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel l = new JLabel("         AMMAR KHODJA Hichem         -    BOUDRAR Ryad         -        HARITI Halima");
        l.setOpaque(true);
        JPanel p = new JPanel();
        p.add(l);
        p.setVisible(true);
        f.add(p);


        f.pack();


        JButton desc=new JButton("Descriptions des données");
        desc.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String[] columnNames = {"N°", "Description de l’attribut"};
                Object[][] data={
                        {"1", "Classe de l'état de la Thyroïde (1= thyroïde normale, 2= hyperthyroïdie, 3= hypothyroïdie)"},
                        {"2", "Test d'absorption de la résine T3"},
                        {"3", "Thyroxine sérique totale mesurée par la méthode de déplacement isotopique"},
                        {"4", "Triiodothyronine sérique totale mesurée par dosage radio-immunologique"},
                        {"5", "Hormone basale de stimulation de la thyroïde (TSH) mesurée par dosage radio-immunologique"},
                        {"6", "Différence absolue maximale de la valeur TSH après injection de 200 microgrammes d'hormone de libération de la thyrotropine par rapport à la valeur basale"}
                };
                JTable table = new JTable(data, columnNames);
                table.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                table.setSize(400, 300);

                table.getColumnModel().getColumn(0).setPreferredWidth(50);
                table.getColumnModel().getColumn(1).setPreferredWidth(350);
                JFrame f = new JFrame();
                f.add(new JScrollPane(table));
                f.setSize(430, 300);
                f.setVisible(true);
            }
        });
        desc.setBounds(110,100,250,30);
        f.add(desc);

        JButton affich=new JButton("Afficher le fichier");
        affich.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String[] columnNames = {"class", "t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"};
                Object[][] data={
                        {"1", "107", "10.1", "2.2", "0.9", "2.7"},
                        {"1", "113", "9.9", "3.1", "2.0", "5.9"},
                        {"1", "127", "12.9", "2.4", "1.4", "0.6"},
                        {"1", "109", "5.3", "1.6", "1.4", "1.5"},
                        {"1", "105", "7.3", "1.5", "1.5", "-0.1"},
                };
                JTable table = new JTable(data, columnNames);
                //table.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                table.setSize(400, 300);
                table.setRowHeight(40);
                JFrame f = new JFrame();
                f.add(new JScrollPane(table));
                f.setSize(500, 300);
                f.setVisible(true);
            }
        });
        affich.setBounds(110,130,250,30);
        f.add(affich);

        JButton stats=new JButton("Afficher les statistiques des attributs");
        stats.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e1){
                String[] columnNames = {"Name", "Mean", "Median", "Mode"};
                Object[][] data={
                        {"t3_resin", "109.6", "110", "110.81"},
                        {"total_thyroxin", "9.8", "9.2", "7.99"},
                        {"total_triio", "2.05", "1.7", "0.999"}
                };
                JTable table = new JTable(data, columnNames);
                //table.getColumnModel().getColumn(1).setCellRenderer(new WordWrapCellRenderer());
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                table.setSize(400, 300);
                table.setRowHeight(40);
                JFrame f = new JFrame();
                f.add(new JScrollPane(table));
                f.setSize(350, 250);
                f.setVisible(true);
            }
        });
        stats.setBounds(110,160,250,30);
        f.add(stats);

        JButton sym=new JButton("La symetrie des données");
        sym.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e2){
                JOptionPane.showMessageDialog(f,
                        "Voir le rapport",
                        "Les statistiques des attributs",
                        JOptionPane.PLAIN_MESSAGE);
            }
        });
        sym.setBounds(110,190,250,30);
        f.add(sym);

        JButton boxplot=new JButton("Afficher les boites a moustaches");
        boxplot.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e3){
                JFrame b=new JFrame();

                JButton boxplot1=new JButton("Boxplot t3_resin");
                boxplot1.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();

                        a.img("Results/boxplots/t3_resin__boxplot.png");
                    }
                });
                boxplot1.setBounds(50,20,200,30);
                b.add(boxplot1);

                JButton boxplot2=new JButton("Boxplot total_thyroxin");
                boxplot2.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();
                        a.img("Results/boxplots/total_thyroxin__boxplot.png");
                    }
                });
                boxplot2.setBounds(50,50,200,30);
                b.add(boxplot2);

                JButton boxplot3=new JButton("Boxplot total_triio");
                boxplot3.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();
                        a.img("Results/boxplots/total_triio__boxplot.png");
                    }
                });
                boxplot3.setBounds(50,80,200,30);
                b.add(boxplot3);

                JButton boxplot4=new JButton("Boxplot tsh");
                boxplot4.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();
                        a.img("Results/boxplots/tsh__boxplot.png");
                    }
                });
                boxplot4.setBounds(50,110,200,30);
                b.add(boxplot4);

                JButton boxplot5=new JButton("Boxplot max_diff_tsh");
                boxplot5.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();
                        a.img("Results/boxplots/max_diff_tsh__boxplot.png");
                    }
                });
                boxplot5.setBounds(50,140,200,30);
                b.add(boxplot5);



                b.setSize(300,250);
                b.setLayout(null);
                b.setVisible(true);


            }
        });
        boxplot.setBounds(110,220,250,30);
        f.add(boxplot);

        JButton histo=new JButton("Afficher les Histogrammes");
        histo.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                JFrame b=new JFrame();

                JButton hist1=new JButton("Histogram max_diff_tsh");
                hist1.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();
                        a.img("Results/histograms/max_diff_tsh__hist.png");
                    }
                });
                hist1.setBounds(50,20,200,30);
                b.add(hist1);

                JButton hist2=new JButton("Histogram t3_resin");
                hist2.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();
                        a.img("Results/histograms/t3_resin__hist.png");
                    }
                });
                hist2.setBounds(50,50,200,30);
                b.add(hist2);

                JButton hist3=new JButton("Histogram total_thyroxin");
                hist3.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();
                        a.img("Results/histograms/total_thyroxin__hist.png");
                    }
                });
                hist3.setBounds(50,80,200,30);
                b.add(hist3);

                JButton hist4=new JButton("Histogram total_triio");
                hist4.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();
                        a.img("Results/histograms/total_triio__hist.png");
                    }
                });
                hist4.setBounds(50,110,200,30);
                b.add(hist4);

                JButton hist5=new JButton("Histogram tsh");
                hist5.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();
                        a.img("Results/histograms/tsh__hist.png");
                    }
                });
                hist5.setBounds(50,140,200,30);
                b.add(hist5);



                b.setSize(350,250);
                b.setLayout(null);
                b.setVisible(true);




            }
        });
        histo.setBounds(110,250,250,30);
        f.add(histo);

        JButton diag=new JButton("Afficher les Scatterplots");
        diag.setBounds(110,280,250,30);
        diag.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){

                JFrame b=new JFrame();

                JLabel label1 = new JLabel("Variable 1", SwingConstants.CENTER);
                label1.setBounds(50,20,200,30);
                b.add(label1);

                JComboBox combo1 = new JComboBox(new String[]{"t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"});
                b.add(combo1);
                combo1.setBounds(50,60,200,30);

                JLabel label2 = new JLabel("Variable 2", SwingConstants.CENTER);
                label2.setBounds(50,100,200,30);
                b.add(label2);

                JComboBox combo2 = new JComboBox(new String[]{"t3_resin", "total_thyroxin", "total_triio", "tsh", "max_diff_tsh"});
                b.add(combo2);
                combo2.setBounds(50,140,200,30);

                JButton diag1=new JButton("Scatterplot");
                diag1.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e11) {
                        ImgInterfaces a = new ImgInterfaces();
                        a.img("Results/scatterplots/" + combo1.getSelectedItem() + "__" + combo2.getSelectedItem() +"__scatter.png");
                    }
                });
                diag1.setBounds(50,180,200,30);
                b.add(diag1);




                b.setSize(320,260);
                b.setLayout(null);
                b.setVisible(true);
            }
        });
        f.add(diag);












        f.setSize(500,500);
        f.setLayout(null);
        f.setVisible(true);
        f.show(true);

    }
}
