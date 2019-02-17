package hornedowl.shadowsblue;

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author billb
 */
public class Fetcher {

    private List<Thimg> movers;
    private String picRootDir;
    private int firstInLine;
    private final PropertyDude propDude;
    private int savedHeight;

    public Fetcher() {
        //Use settings from last run
        propDude = new PropertyDude();
        propDude.getProperties();
        picRootDir = propDude.getPicRootDir();
        savedHeight = propDude.getInitialHeight();
        firstInLine = propDude.getFirstInLine();
        movers = new ArrayList<>();
    }

    //traverse a directory to get image files names
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
        if (!movers.isEmpty()) {
            movers.get(firstInLine).setX(d.getSize().width - movers.get(firstInLine).getThumbWidth());
        }
    }

    public void getSerializedImages() throws IOException, ClassNotFoundException {
        MoversSaver ms = new MoversSaver();
        Thimg newThimg;
        List saved = ms.get();
        for (Object t : saved) {
            newThimg = (Thimg) t;
            //serialization did not save these two things
            newThimg.setThumbImage(newThimg.getFilename(), savedHeight);
            newThimg.setAfTran(new AffineTransform());
            movers.add(newThimg);
        }
        firstInLine = propDude.getFirstInLine();

    }

    /**
     * randomize the image list
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
