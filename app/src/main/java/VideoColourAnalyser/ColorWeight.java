package VideoColourAnalyser;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.awt.image.BufferedImage;



public class ColorWeight extends Color implements Comparable<Color> {
    
    private Color colour;
    private long weight;
    
    public ColorWeight(Color colour) {
        super(colour.getRGB());
        this.weight = 1;
    }

    public ColorWeight(int colour) {
        super(colour);
        this.weight = 1;
    }

    public ColorWeight(int colour, long weight) {
        super(colour);
        this.weight = weight;
    }

    public ColorWeight(Color colour, long weight) {
        super(colour.getRGB());
        this.weight = weight;
    }

    // public void joinColours(ColorWeight cw) {
    //     int newWeight = this.getWeight() + cw.getWeight();
    //     int newRed = (cw.getRed() * cw.getWeight() + this.getRed() * this.getWeight())/newWeight; 
    //     int newBlue = (cw.getBlue() * cw.getWeight() + this.getBlue() * this.getWeight())/newWeight; 
    //     int newGreen = (cw.getGreen() * cw.getWeight() + this.getGreen() * this.getWeight())/newWeight; 
    //     return new ColorWeight(new Color(newRed, newBlue, newGreen), .getWeight() + cw2.getWeight());
    // }

    public static ColorWeight joinColours(ColorWeight cw1, ColorWeight cw2) {
        long newWeight = cw1.getWeight() + cw2.getWeight();
        long newRed = (cw1.getRed() * cw1.getWeight() + cw2.getRed() * cw2.getWeight())/newWeight; 
        long newBlue = (cw1.getBlue() * cw1.getWeight() + cw2.getBlue() * cw2.getWeight())/newWeight; 
        long newGreen = (cw1.getGreen() * cw1.getWeight() + cw2.getGreen() * cw2.getWeight())/newWeight; 
        return new ColorWeight(new Color(newRed, newBlue, newGreen), newWeight);
    }

    public long getWeight() {
        return weight;
    }

    public void increaseWeight(int amnt) {
        weight += amnt;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Color)) {
            return false;
        }
        Color other = (Color)obj;
        return this.getRGB() == other.getRGB();
    }


    public float getHue() {
        return Color.RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null)[0];
    }

    public Color inverseColour() {
        return new Color(255-this.getRed(),255-this.getGreen(),255-this.getBlue());
    }

    public static ColorWeight averageWeights(List<ColorWeight> colors) {
        long red = 0;
        long green = 0;
        long blue = 0;
        long weight = 0;
        
        for (ColorWeight curr : colors) {
            // float[] hsb = ColorWeight.RGBtoHSB(curr.getRed(), curr.getGreen(), curr.getBlue(), null);
            
            red += (curr.getRed() * curr.getWeight());
            green += (curr.getGreen() * curr.getWeight());
            blue += (curr.getBlue() * curr.getWeight());
            weight += curr.getWeight();    
        }
        Color balancedColor = new Color(Math.round(red/weight), Math.round(green/weight), Math.round(blue/weight));

        return new ColorWeight(balancedColor, weight);
    }

    public static Color averageWeights(BufferedImage image) {
        long red = 0, green = 0, blue = 0;
        int weight = image.getWidth() * image.getHeight();
        for (int x = 0; x <image.getWidth(); x++) {
            for (int y = 0; y< image.getHeight(); y++) {
                // float[] hsb = ColorWeight.RGBtoHSB(curr.getRed(), curr.getGreen(), curr.getBlue(), null);
                Color pixel = new Color(image.getRGB(x, y));
                red += pixel.getRed();
                green += pixel.getGreen();
                blue += pixel.getBlue();
            }
        }
        return new Color(Math.round(red/weight),Math.round(green/weight),Math.round(blue/weight));
    }

    public static List<ColorWeight> condenseWeights(List<ColorWeight> colors) {
        long red = 0;
        long green = 0;
        long blue = 0;
        int weight = 0;
        HashMap<ColorWeight,Long> condensed = new HashMap<>();
        for (ColorWeight curr : colors) {
            if (condensed.containsKey(curr)) {
                condensed.put(curr, condensed.get(curr) + curr.getWeight());
            }
            else {
                condensed.put(curr,curr.getWeight());
            }
        }
        List<ColorWeight> ret = new ArrayList<>();
        for (ColorWeight cw : condensed.keySet()) {
            ret.add(new ColorWeight(cw,condensed.get(cw)));
        }

        return ret;
    }

    @Override
    public int compareTo(Color o) {
        float i = Color.RGBtoHSB(this.getRed(), this.getGreen(), this.getBlue(), null)[0];
        float j = Color.RGBtoHSB(o.getRed(), o.getGreen(), o.getBlue(), null)[0];
        if (i<j) {
            return -1;
        }
        if (i>j) {
            return 1;
        }
        return 0;
    }

    
}
