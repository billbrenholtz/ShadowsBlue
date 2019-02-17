package hornedowl.shadowsblue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author  Bill Brenholtz
 * 
 * Read and write the properties file that contains the current starting
 * directory, the index of Thimg that is on the furthest right position of the screen
 * and the current window height
 * 
 */
class PropertyDude {
    
    private final Properties p;
    private String picRootDir;      //root directory of images
    private String strHeight;       //last height of the window
    private String firstInLine;     //index of first Thimg in line
    
    public PropertyDude() {
        p = new Properties(System.getProperties());        
    }
    
    /**
     * get properties from the properties file
     */
    public void getProperties() {
        String fs = System.getProperty("file.separator");
        String home = System.getProperty("user.home");
        //Are we MS Windows?
        File winDir = new File(home + fs + "AppData" + fs + "Local" + fs + "HornedOwl" + fs + "ShadowsBlue");
        String where;
        if (winDir.exists()) {
            //Yes, Windows
            where = home + fs + "AppData" + fs + "Local" + fs + "HornedOwl" + fs + "ShadowsBlue";
        } else {
            //No, Linux
            where = home + fs + ".hornedowl" + fs + "ShadowsBlue";
        }
        try (FileReader settingsFile = new FileReader(new File(where, "config"))) {
            p.load(settingsFile);
            System.setProperties(p);
        } catch (IOException ignored) {
        }
        picRootDir = System.getProperty("shadowsblue.dir.start", "");
        strHeight = System.getProperty("shadowsblue.height", "120");
        firstInLine = System.getProperty("shadowsblue.firstinline", "0");
    }

    /**
     * save the properties
     * @param pRootDir - current root directory
     * @param sHeight - current windows height
     * @param first - index of Thimg on far right side of window
     */
    public void saveProperties(String pRootDir, int sHeight, int first) {
        String fs = System.getProperty("file.separator");
        String home = System.getProperty("user.home");
        //Are we MS Windows?
        File winDir = new File(home + fs + "AppData" + fs + "Local");
        String where;
        if (winDir.exists()) {
            //Yes, Windows
            File hoDir = new File(home + fs + "AppData" + fs + "Local" + fs + "HornedOwl" + fs + "ShadowsBlue");
            if (!hoDir.exists()) {
                //directory does not exist; make it
                hoDir.mkdirs();
            }
            where = home + fs + "AppData" + fs + "Local" + fs + "HornedOwl" + fs + "ShadowsBlue";
        } else {
            //No, Linux
            File hoDir = new File(home + fs + ".hornedowl" + fs + "ShadowsBlue");
            if (!hoDir.exists()) {
                //directory does not exist; make it
                hoDir.mkdirs();
            }
            where = home + fs + ".hornedowl" + fs + "ShadowsBlue";
        }
        try (FileWriter settingsFile = new FileWriter(new File(where, "config"))) {
            p.setProperty("shadowsblue.dir.start", pRootDir);
            strHeight = Integer.toString(sHeight);
            p.setProperty("shadowsblue.height", strHeight);
            firstInLine = Integer.toString(first);
            p.setProperty("shadowsblue.firstinline", firstInLine);
            p.store(settingsFile, "No comment");
        } catch (IOException ignored) {
        }
    }

    public String getPicRootDir() {
        return picRootDir;
    }

    public int getInitialHeight() {
        return Integer.parseInt(strHeight);
    }

    public int getFirstInLine() {
        return Integer.parseInt(firstInLine);
    }
}
