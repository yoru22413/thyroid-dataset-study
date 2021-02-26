package dm.frontend.part1;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ImgInterfaces {


    void img(String s, JDialog frame){
        JDialog z = new JDialog(frame,"", true);
        JLabel bg;
        BufferedImage img = null;
        try {
            System.out.println(s);
            URL url = ImgInterfaces.class.getClassLoader().getResource(s);
            System.out.println(url);
            assert url != null;
            img = ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Image dimg = img.getScaledInstance(600, 400,
                Image.SCALE_SMOOTH);

        ImageIcon imageIcon = new ImageIcon(dimg);

        bg=new JLabel("",imageIcon, SwingConstants.CENTER);
        bg.setOpaque(false);
        bg.setBounds(0, 0, 600, 400);
        z.add(bg);

        z.setSize(600,440);
        z.setLayout(null);
        z.setVisible(true);
    }



    public class ImagePanel extends JPanel{
        private BufferedImage image;
        public ImagePanel(String s) throws IOException {
            image = ImageIO.read(new File(s));
        }
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, null);
        }
    }

    public static class MyCanvas extends Canvas{
        MyCanvas(String s){

        }

        public void paint(Graphics g,String s) {

            Toolkit t=Toolkit.getDefaultToolkit();
            Image i=t.getImage(s);
            g.drawImage(i, 0,0,this);

        }


        public  void main(String s) {
            MyCanvas m=new MyCanvas(s);
            JFrame f=new JFrame();
            f.add(m);
            f.setSize(400,400);
            f.setVisible(true);
        }

    }
}
