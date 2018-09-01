/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hornedowl.shadowsblue;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author liberty
 */
class PropertyDude {
    
    private final Properties p;
    private String picRootDir;
    private String strHeight;
    private String firstInLine;
    
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
     * save the starting directory and random selection in the properties file
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
                //noinspection unused
                final boolean b = hoDir.mkdirs();
            }
            where = home + fs + "AppData" + fs + "Local" + fs + "HornedOwl" + fs + "ShadowsBlue";
        } else {
            //No, Linux
            File hoDir = new File(home + fs + ".hornedowl" + fs + "ShadowsBlue");
            if (!hoDir.exists()) {
                //noinspection unused
                final boolean b = hoDir.mkdirs();
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
