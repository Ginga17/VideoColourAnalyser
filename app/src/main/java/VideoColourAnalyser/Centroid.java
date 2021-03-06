package VideoColourAnalyser;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.awt.Color;


public class Centroid {
    
    // private float hue;
    // private float brightness;
    private Color color;
    private List<ColorWeight> cluster;
    private long weight = 1;

    public Centroid (List<ColorWeight> cluster) {
        this.cluster = cluster;
        color = ColorWeight.averageWeights(cluster);
        // hue = Color.RGBtoHSB(color.getRed(), color.getGreen(),color.getBlue(), null)[0];
        // brightness = Color.RGBtoHSB(color.getRed(), color.getGreen(),color.getBlue(), null)[1];
    }
    
    /**
     * Calculates the total distance between every colour in the data set and the mean.
     * @return - the sum distance between all the nodes and the mean
     */
    public double sumDistanceFromMean() {
        return cluster.stream().mapToDouble(c -> distFromColor(c) * c.getWeight()).sum();

    }

    /**
     * 
     * @return - true if the centroid changes, false otherwise
     */
    public boolean recalculateCentroid(List<ColorWeight> newCluster) {
        if (cluster.equals(newCluster)) {
            return false;
        }

        cluster = newCluster;
        // hue = Color.RGBtoHSB(newColor.getRed(), newColor.getGreen(), newColor.getBlue(), null)[0];
        // brightness = Color.RGBtoHSB(color.getRed(), color.getGreen(),color.getBlue(), null)[1];

        color =  ColorWeight.averageWeights(cluster);
        weight = newCluster.stream().mapToLong(c -> c.getWeight()).sum();
        return true;
    }

    private int squared(int val) {
        return val*val;
    }
    
    public int distFromColor(Color c1) {
        return squared(c1.getRed()-color.getRed()) + squared(c1.getGreen()-color.getGreen()) + squared(c1.getBlue()-color.getBlue());

    }

    public List<ColorWeight> getCluster() {
        return cluster;
    }

    public ColorWeight getColorWeight() {
        return ColorWeight.averageWeights(cluster);
    }

    public long getWeight() {
        return weight;
    }

    public static List<Centroid> splitCentroid(List<Centroid> centroids) {
        Centroid target = centroids.stream().max(Comparator.comparingDouble(c -> c.sumDistanceFromMean()/ c.getColorWeight().getWeight())).get();
        centroids.remove(target);
        List<ColorWeight> cluster = target.getCluster();
        cluster.sort(new ByHSB());
        centroids.add(new Centroid(cluster.subList(0, cluster.size()/2)));
        centroids.add(new Centroid(cluster.subList(cluster.size()/2, cluster.size())));
        return centroids;
    }


}
