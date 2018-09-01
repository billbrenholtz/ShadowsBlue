/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @author liberty
 */
class MoversSaver {

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
                //noinspection unused
                final boolean b = hoDir.mkdirs();
            }
            where = home + fs + "AppData" + fs + "Local" + fs + "HornedOwl" + fs + "ShadowsBlue" + fs + "movers";
        } else {
            //No, Linux
            File hoDir = new File(home + fs + ".hornedowl" + fs + "ShadowsBlue");
            if (!hoDir.exists()) {
                //noinspection unused
                final boolean b = hoDir.mkdirs();
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
