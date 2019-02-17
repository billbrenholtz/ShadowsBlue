package hornedowl.shadowsblue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Bill Brenholtz
 * 
 * Serialize and deserialize current list of Thimg(s) to disk file to be used
 * at next start up
 */
class MoversSaver {

    /**
     * Serialize current list of Thimg(s) to disk file
     * @param movers - List<Thimg> to serialize
     */
    public void save(List movers) {
        String fs = System.getProperty("file.separator");
        String home = System.getProperty("user.home");
        //Are we MS Windows?
        File winDir = new File(home + fs + "AppData" + fs + "Local");
        String where;
        if (winDir.exists()) {
            //Yes, Windows
            File hoDir = new File(home + fs + "AppData" + fs + "Local" + fs + "HornedOwl" + fs + "ShadowsBlue");
            if (!hoDir.exists()) {
                //its Windows but need to make the directory to hold data
                hoDir.mkdirs();
            }
            where = home + fs + "AppData" + fs + "Local" + fs + "HornedOwl" + fs + "ShadowsBlue" + fs + "movers";
        } else {
            //No, Linux
            File hoDir = new File(home + fs + ".hornedowl" + fs + "ShadowsBlue");
            if (!hoDir.exists()) {
                //its Linux and need to make directory to hold data
                hoDir.mkdirs();
            }
            where = home + fs + ".hornedowl" + fs + "ShadowsBlue" + fs + "movers";
        }
        Path fp = Paths.get(where);
        try {
            OutputStream fos = Files.newOutputStream(fp);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(movers);
        } catch (IOException ignored) {
        }
    }
    
    /**
     * Deserialize disk file to List<Thimg>
     * 
     */
    public List get() throws IOException, ClassNotFoundException {
        String fs = System.getProperty("file.separator");
        String home = System.getProperty("user.home");
        //Are we MS Windows?
        File winDir = new File(home + fs + "AppData" + fs + "Local" + fs + "HornedOwl" + fs + "ShadowsBlue");
        String where;
        if (winDir.exists()) {
            //Yes, Windows
            where = home + fs + "AppData" + fs + "Local" + fs + "HornedOwl" + fs + "ShadowsBlue" + fs + "movers";
        } else {
            //No, Linux
            where = home + fs + ".hornedowl" + fs + "ShadowsBlue" + fs + "movers";
        }
        Path fp = Paths.get(where);
        InputStream fis = Files.newInputStream(fp);
        ObjectInputStream ois = new ObjectInputStream(fis);
        return (List) ois.readObject();
    }
}
