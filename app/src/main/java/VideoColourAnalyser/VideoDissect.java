package VideoColourAnalyser;
import java.awt.Graphics;
import java.util.List;
import java.awt.Color;

import javax.swing.JPanel;

public class VideoDissect extends JPanel {

    private List<Color> colors;
    public VideoDissect(List<Color> colors) {
        this.colors = colors;
    }

    public int getWidth() {
        return colors.size();
    }

    protected void paintComponent(Graphics g) {
        int x = 0;
        for (Color c : colors) {

            g.setColor(c);
            g.fillRect(x, 0, 1, 500);
            x++;
        }
    }
}
