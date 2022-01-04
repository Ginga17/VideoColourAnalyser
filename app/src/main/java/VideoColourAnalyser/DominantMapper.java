package VideoColourAnalyser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;

public class DominantMapper {
    

    private List<ColorWeight> colors;
    private String filmname;
    public DominantMapper(List<ColorWeight> colors, String filmname) {
        this.colors = colors;
        this.colors.sort(new ByHSB());
        this.filmname = filmname;
    }

    public DominantMapper(VideoScanner vs) {
        this.colors = vs.getColorWeights();
        this.colors.sort(new ByHSB());
        this.filmname = vs.getFilmname();
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

    private HashMap<Centroid, List<ColorWeight>> initialiseCentroidsX(int k) {
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        System.out.println("Initialising");
        meansToClusters.put(new Centroid(Arrays.asList(colors.get(0))), new ArrayList<>());
        meansToClusters.put(new Centroid(Arrays.asList(colors.get(colors.size() - 1))), new ArrayList<>());

        return meansToClusters;
    }

    // private HashMap<Centroid, List<ColorWeight>> initialiseCentroids2(int k) {
    //     HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
    //     System.out.println("Initialising");
    //     float huediff = (colors.get(colors.size()-1).getHue() - colors.get(0).getHue())/k;
    //     for (int i = 0; i<k; i += 1) {

    //         for (int p = i*colors.size()/k; p<(i+1)*colors.size()/k; p += 1) {
    //             currCol.add(colors.get(p));
    //         }
    //         meansToClusters.put(new Centroid(currCol), currCol);
    //     }
    //     return meansToClusters;
    // }

    
    /**
     * Initialises the means and clusters to be used in the kMeansCluster algorithm.
     * Delegates by sorting the colours by hue, and then evenly partitioning it in blocks into clusters.
     * @param colorsByWeight - A list of all the colours in the image to be parsed
     * @param k - the number of clusters to be formed
     * @return - The initialised centroids (means) and their associated clusters
     */
    private HashMap<Centroid, List<ColorWeight>> partitionAveMeans(int k) {
        // System.out.println("sorting");
        // colorsByWeight.sort(new ByHSB());
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

    /**
     * Initialises the means and clusters to be used in the kMeansCluster algorithm.
     * Delegates by sorting the colours by hue, and then evenly partitioning it in blocks into clusters.
     * @param colorsByWeight - A list of all the colours in the image to be parsed
     * @param k - the number of clusters to be formed
     * @return - The initialised centroids (means) and their associated clusters
     */
    private HashMap<Centroid, List<ColorWeight>> randAverageMeans(int k) {
        
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        HashMap<Integer, List<ColorWeight>> clusters = new HashMap<>();
        for (int a = 0; a<k; a++) {
            clusters.put(a, new ArrayList<>());
        }
        Random rand = new Random();
        for (ColorWeight cw : colors) {
            clusters.get(rand.nextInt(k)).add(cw);
        }
        for (List<ColorWeight> colors : clusters.values()) {
            meansToClusters.put(new Centroid(colors), colors);
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

    private List<Centroid> kMeansCluster(List<Centroid> centroids) {
        HashMap<Centroid, List<ColorWeight>> meansToClusters = new HashMap<>();
        centroids = Centroid.splitCentroid(centroids);
        for (Centroid centroid : centroids) {
            meansToClusters.put(centroid, new ArrayList<>());
        }
        
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
        displayColors(centroids);
        System.out.println("k = " + k + ": " + centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum());

    }

    private static void displayColors(List<Centroid> means) {
        DominantRectangle rect = new DominantRectangle(means);
        JFrame window = new JFrame();
        window.setLayout(new BorderLayout());
        window.add(rect);
        window.pack();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setVisible(true);
    }

    private List<Centroid> domColours;
    public void save() throws IOException {
        new DominantRectangle(domColours).save(filmname);
    }

    public void findDominantColors() {
        List<Centroid> centroids = kMeansCluster(4);
        List<Centroid> lastCentroids = null;
        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        List<ColorWeight> lastMeans = means;
        double dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum(); 
        double lastDistance = 2*dist;
        int k = 5;
        while(lastDistance * 0.85 > dist) {
        // while(lastDistance/ dist > 1.15) {
            lastDistance = dist;
            lastCentroids = centroids;
            centroids = kMeansCluster(k);
            dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum();
            k++;
        }
        System.out.println("MMMMMMMMMMMOOOOOOOOOOOOOOOOO" + k);
        domColours = lastCentroids;
        displayColors(lastCentroids);
    }

    public void findDominantColors2() {
        List<Centroid> centroids = kMeansCluster(2);
        List<Centroid> lastCentroids = null;

        List<ColorWeight> means = centroids.stream().map(o-> o.getColorWeight()).collect(Collectors.toList());
        List<ColorWeight> lastMeans = means;
        double dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum(); 
        double lastDistance = dist*2;
        // System.out.println("k = 4: " + centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum());        
        while(dist/lastDistance < 0.95) {
            System.out.println("PPPPPPPP" + centroids.size());
            lastDistance = dist;
            lastCentroids = centroids;
            centroids = kMeansCluster(centroids);
            dist = centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum();
            // System.out.println("k = " + k + ": " + centroids.stream().mapToDouble(Centroid::sumDistanceFromMean).sum());
            // System.out.println("ratio = " + dist/lastDistance);

        }
        System.out.println("k = " + (centroids.size() - 1) + ": " + lastDistance);
        System.out.println("ratio = " + dist/lastDistance);
        domColours = lastCentroids;
        displayColors(lastCentroids);
    }



}
