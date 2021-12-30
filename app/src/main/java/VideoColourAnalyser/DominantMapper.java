package VideoColourAnalyser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.BorderLayout;
import javax.swing.JFrame;

public class DominantMapper {
    
    private static HashMap<Centroid, List<ColorWeight>> delegateMeans(List<ColorWeight> colorsByWeight, int k) {
        // System.out.println("sorting");
        // colorsByWeight.sort(new ByHSB());
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        System.out.println("Initialising");
        // for (i = 0; i<colorsByWeight.size(); i += (colorsByWeight.size()/numOfDom)) {
        // for (int i = 0; i<k; i += 1) {
        //     meansToClusters.put(new Centroid( Arrays.asList(colorsByWeight.get(i * colorsByWeight.size()/ k))), new ArrayList<>());
        // }
        for (int i = 0; i<k; i += 1) {
            List<ColorWeight> currCol = new ArrayList<>();
            for (int p = i*colorsByWeight.size()/k; p<(i+1)*colorsByWeight.size()/k; p += 1) {
                currCol.add(colorsByWeight.get(p));
            }
            meansToClusters.put(new Centroid(currCol), currCol);
        }
        return meansToClusters;
    }

    private static List<Centroid> kMeansCluster(List<ColorWeight> colorsByWeight, int k) {
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        // HashMap<Centroid, List<ColorWeight>> rearrangedClusters = new HashMap<>();
        meansToClusters = delegateMeans(colorsByWeight, k);
        // meansToClusters = randMeansToClusters(colorsByWeight, k);
        // meansToClusters = forgyMeansToClusters(colorsByWeight, k);
        
        // Here, we need initial means to be set
        while (true)
        {
            // Assign clusters
            for (ColorWeight curr: colorsByWeight) {
                float dist = 0;
                Centroid closest = null;
                for(Centroid key : meansToClusters.keySet()) {
                    if(closest == null || key.distFromColor(curr) < dist) {
                        dist = key.distFromColor(curr);
                        closest = key;
                    }
                }
                meansToClusters.get(closest).add(curr);
            }
 
            boolean noChange = true;
            for (Centroid c : meansToClusters.keySet()) {
                if (c.recalculateCentroid(meansToClusters.get(c))) {
                    noChange = false;
                }
            }
            if (noChange) {
                break;
            }
        }
        return new ArrayList<>(meansToClusters.keySet());
    }

    public static void graphColour(List<ColorWeight> colors,int k) {
        
        colors.sort(new ByHSB());
        List<Centroid> centroids = kMeansCluster(colors,k);
        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        displayColors(means);

    }

    private static void displayColors(List<ColorWeight> means) {
        DominantRectangle rect = new DominantRectangle(means);
        JFrame window = new JFrame();
        window.setLayout(new BorderLayout());
        window.add(rect);
        window.pack();
        // window.setSize(800, 800);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setVisible(true);
    }

    public void findDominantColors(List<ColorWeight> colorsByWeight) {
        
        List<Centroid> centroids = kMeansCluster(colorsByWeight,5);

        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        List<ColorWeight> lastMeans = means;
        double dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum(); 
        double lastDistance = 2*dist;
        // for (int k=2; k<12; k++) {
        int k = 6;
        while(lastDistance * 0.85 > dist) {
        // while(lastDistance/ dist > 1.15) {
            lastDistance = dist;
            centroids = kMeansCluster(colorsByWeight,k);
            lastMeans = means;
            means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
            dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum();
            k++;
        }
        // dominantColours = lastMeans;
        displayColors(lastMeans);
    }
}
