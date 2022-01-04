package VideoColourAnalyser;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Image;


public class VideoDissect extends JPanel {

    private List<Color> colors;
    private Image img;
    private Image scaled;

    public VideoDissect(List<Color> colors) {
        this.colors = colors;
        int x = 0;
        BufferedImage bufferedImage = new BufferedImage(colors.size(), 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        for (Color c : colors) {

            g2d.setColor(c);
            g2d.fillRect(x, 0, 1, 1000);
            x++;
        }
        img = bufferedImage;
    }
    
    public void save(String fileName) throws IOException {
      File outputfile = new File(fileName);
      BufferedImage buffered = new BufferedImage(6000, 4000, BufferedImage.TYPE_INT_RGB);
      buffered.getGraphics().drawImage(img.getScaledInstance(6000, 4000, Image.SCALE_SMOOTH), 0, 0 , null);
      ImageIO.write(buffered, "png", outputfile);
    }

    @Override
    public void invalidate() {
      super.invalidate();
      int width = getWidth();
      int height = getHeight();
  
      if (width > 0 && height > 0) {
        scaled = img.getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
      }
    }
  
    @Override
    public Dimension getPreferredSize() {
      return img == null ? new Dimension(200, 200) : new Dimension(
          img.getWidth(this), img.getHeight(this));
    }
  
    @Override
    public void paintComponent(Graphics g) {
      super.paintComponent(g);
      g.drawImage(scaled, 0, 0, null);
    }

}
