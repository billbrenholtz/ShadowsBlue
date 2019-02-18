package hornedowl.shadowsblue;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Bill Brenholtz
 * 
 * Retrieve saved data or collect new images from a starting directory
 * 
 */
public class Fetcher {

    private List<Thimg> movers;             //data to be displayed
    private String picRootDir;              //root directory of images
    private int firstInLine;                //index of first Thimg in movers to be displayed
    private final PropertyDude propDude;    //manage the properties file
    private int savedHeight;                //height that images need to be - width is scaled

    public Fetcher() {
        //Use settings from last run if available
        //If not available, defaults are returned
        propDude = new PropertyDude();
        propDude.getProperties();
        picRootDir = propDude.getPicRootDir();
        savedHeight = propDude.getInitialHeight();
        firstInLine = propDude.getFirstInLine();
        
        movers = new ArrayList<>();
    }

    /**
     * Recursively collect images starting at
     * @param newRoot - starting directory
     * @param d - needed to create thumbnails
     */
    public void collectImages(String newRoot, Dimension d) {
        
        picRootDir = newRoot;
        savedHeight = d.height;
        
        Thimg newThimg;
        DirectoryCrawl dc = new DirectoryCrawl(picRootDir);
        dc.run();
        movers.clear();
        for (Object o : dc.getFileList()) {
            newThimg = new Thimg((String) o, savedHeight);
            movers.add(newThimg);
        }
        scrambleImages();
        firstInLine = 0;
        //if something is returned, set X position for the first image to farthest side of window
        if (!movers.isEmpty()) {
            movers.get(firstInLine).setX(d.getSize().width - movers.get(firstInLine).getThumbWidth());
        }
    }

    /**
     * Try to retrieve the serialized image objects from last run
     * @throws IOException
     * @throws ClassNotFoundException 
     */
    public void getSerializedImages() throws IOException, ClassNotFoundException {
        MoversSaver ms = new MoversSaver();
        Thimg newThimg;
        List saved = ms.get();
        for (Object t : saved) {
            newThimg = (Thimg) t;
            //serialization did not save these two things
            newThimg.setThumbImage(savedHeight);
            newThimg.setAfTran(new AffineTransform());
            movers.add(newThimg);
        }
        firstInLine = propDude.getFirstInLine();
    }

    /**
     * Randomize the order so that we don't get the same boring order all the time
     */
    private void scrambleImages() {
        Random generator = new Random(System.currentTimeMillis());
        Thimg thirdThimg;
        int pick;
        for (int i = 0; i < movers.size(); i++) {
            pick = i;
            while (pick == i) {
                pick = generator.nextInt(movers.size() - 1);
            }
            thirdThimg = movers.get(i);
            movers.set(i, movers.get(pick));
            movers.set(pick, thirdThimg);
        }
    }

    /**
     * Save the properties that are needed the next run
     * @param rootDir - root directories of the images
     * @param hite - current height of windows and thumbnails
     * @param first - index of Thimg on farthest right of window
     */
    public void saveProperties(String rootDir, int hite, int first) {
        propDude.saveProperties(rootDir, hite, first);
    }
    
    public List<Thimg> getMovers() {
        return movers;
    }

    public String getPicRootDir() {
        return picRootDir;
    }

    public int getFirstInLine() {
        return firstInLine;
    }

    public int getSavedHeight() {
        return savedHeight;
    }
}
