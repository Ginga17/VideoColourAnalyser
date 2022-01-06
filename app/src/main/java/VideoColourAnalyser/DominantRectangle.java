package VideoColourAnalyser;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.geom.AffineTransform;
import java.awt.Dimension;



public class DominantRectangle extends JPanel {

    // private List<ColorWeight> colors;
    // protected HashMap<ColorWeight, Float> colorsToSize = new HashMap<>();
    private Image img;
    private Image scaled;

    // public DominantRectangle(List<ColorWeight> colors) {
    //     long totalWeight = colors.stream().mapToLong(e -> e.getWeight()).sum();
    //     int y = 0;
    //     BufferedImage bufferedImage = new BufferedImage(1500, 1000, BufferedImage.TYPE_INT_RGB);
    //     Graphics2D g2d = bufferedImage.createGraphics();
    //     for (ColorWeight cw : colors) {
            
    //         g2d.setColor(cw);
    //         g2d.fillRect(0, y, 1500, Math.round(1000 * ((float)cw.getWeight()/totalWeight)));
    //         y += Math.round(1000 * ((float)cw.getWeight()/totalWeight));
    //     }
        
    //     img = bufferedImage;
    // }

    public DominantRectangle(List<Centroid> colors) {
        long totalWeight = colors.stream().mapToLong(e -> e.getWeight()).sum();
        int y = 0;
        BufferedImage bufferedImage = new BufferedImage(1500, 1000, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        colors.sort(Comparator.comparingLong(o -> ((Centroid) o).getWeight()).reversed());
        
        for (Centroid cw : colors) {
            
            g2d.setColor(cw.getColorWeight());
            g2d.fillRect(0, y, 1500, Math.round(1000 * ((float)cw.getWeight()/totalWeight)));
            y += Math.round(1000 * ((float)cw.getWeight()/totalWeight));
        }
        
        img = bufferedImage;
    }

    // protected void paintComponent(Graphics g) {
    //     int ypos = 0;
    //     List<ColorWeight> colors = new ArrayList<>(colorsToSize.keySet());
    //     colors.sort(Comparator.comparing(ColorWeight:: getWeight).reversed());
    //     g.setFont(new Font("TimesRoman", Font.PLAIN, 30)); 
    //     for (ColorWeight cw: colors) {
    //         // g.drawRect(10, 10, 600, 510);
    //         g.setColor(cw);
    //         g.fillRect(0, ypos, getSize().width, getSize().height);
    //         g.setColor(Color.WHITE);

    //         // Write text
    //         // if (colorsToSize.get(cw) > 0.04) {
    //         //     String text = "RGB (" + cw.getRed() + ", " + cw.getGreen() + ", " + cw.getBlue() + ")      " + Math.round(colorsToSize.get(cw) * 10000) / 100.0 + "%";
    //         //     int textWidth = (int)Math.round(g.getFontMetrics().getStringBounds(text, g).getWidth());
    //         //     int textHeight = (int)Math.round(g.getFontMetrics().getStringBounds(text, g).getHeight());
    //         //     g.drawString(text, pic.getWidth(null)*3/2 - textWidth/2, ypos + Math.round(colorsToSize.get(cw) * pic.getHeight(null)/2 + textHeight * 1/2));
    //         // }
    //         ypos += Math.round(colorsToSize.get(cw) * 800);
    //     }
    // }
    
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

    public void save(String fileName, float sensitivity) throws IOException {
        
        File outputfile = new File("VideoData/" + fileName + "/" + sensitivity + "Dominants.png");
        BufferedImage buffered = new BufferedImage(6000, 4000, BufferedImage.TYPE_INT_RGB);
        buffered.getGraphics().drawImage(img.getScaledInstance(6000, 4000, Image.SCALE_SMOOTH), 0, 0 , null);
        ImageIO.write(buffered, "png", outputfile);
    }

}
