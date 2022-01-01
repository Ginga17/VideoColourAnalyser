package VideoColourAnalyser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;

public class DominantMapper {
    

    private List<ColorWeight> colors;
    public DominantMapper(List<ColorWeight> colors) {
        this.colors = colors;
        this.colors.sort(new ByHSB());
    }

    public DominantMapper(VideoScanner vs) {
        this.colors = vs.getColorWeights();
        this.colors.sort(new ByHSB());
    }

    private HashMap<Centroid, List<ColorWeight>> initialiseCentroids(int k) {
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        System.out.println("Initialising");
        for (int i = 0; i<k; i += 1) {
            List<ColorWeight> currCol = new ArrayList<>();
            for (int p = i*colors.size()/k; p<(i+1)*colors.size()/k; p += 1) {
                currCol.add(colors.get(p));
            }
            meansToClusters.put(new Centroid(currCol), currCol);
        }
        return meansToClusters;
    }

    private List<Centroid> kMeansCluster(int k) {
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        meansToClusters = initialiseCentroids(k);
        while (true)
        {
            // Assign clusters
            for (ColorWeight curr: colors) {
                meansToClusters.get(findClosestCentroid(meansToClusters.keySet(), curr)).add(curr);
            }
 
            boolean noChange = true;
            for (Centroid c : meansToClusters.keySet()) {
                // recalculateCentroid returns true if a change occurs
                if (c.recalculateCentroid(meansToClusters.get(c))) {
                    noChange = false;
                }
                meansToClusters.put(c,new ArrayList<>());
            }
            if (noChange) {
                // kMeansCluster algorithm terminates when no change is made to the centroids over an iteration
                break;
            }
        }
        return new ArrayList<>(meansToClusters.keySet());
    }

    private Centroid findClosestCentroid(Set<Centroid> centroids, Color color) {
        return centroids.stream().min(Comparator.comparingInt(c -> c.distFromColor(color))).get();        
    }

    public void graphColour(int k) {
        List<Centroid> centroids = kMeansCluster(k);
        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        displayColors(means);
        System.out.println("k = " + k + ": " + centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum());

    }

    private static void displayColors(List<ColorWeight> means) {
        DominantRectangle rect = new DominantRectangle(means);
        JFrame window = new JFrame();
        window.setLayout(new BorderLayout());
        window.add(rect);
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setVisible(true);
    }

    public void findDominantColors() {
        List<Centroid> centroids = kMeansCluster(4);

        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        List<ColorWeight> lastMeans = means;
        double dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum(); 
        double lastDistance = 2*dist;
        int k = 5;
        while(lastDistance * 0.85 > dist) {
        // while(lastDistance/ dist > 1.15) {
            lastDistance = dist;
            centroids = kMeansCluster(k);
            lastMeans = means;
            means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
            dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum();
            k++;
        }
        System.out.println("MMMMMMMMMMMOOOOOOOOOOOOOOOOO" + k);
        displayColors(lastMeans);
    }
}
