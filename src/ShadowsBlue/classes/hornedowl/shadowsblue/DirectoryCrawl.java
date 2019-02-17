package hornedowl.shadowsblue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Bill Brenholtz
 * 
 * Recursively walk starting directory looking for images to display
 */
@SuppressWarnings("unused")
public class DirectoryCrawl {

    private String startDir;            //root of directories to search
    private final Finder myFinder;

    public DirectoryCrawl(String rootDir) {
        startDir = rootDir;
        myFinder = new Finder();
    }

    public void run() {
        //if no starting root diectory, use stock photos included with this app
        if (startDir.isEmpty()) {
            myFinder.useDefault();
            return;
        }
        //make sure root still exists
        if (!Paths.get(startDir).toFile().exists()) {
            return;
        }
        //get to work
        try {
            Files.walkFileTree(Paths.get(startDir), myFinder);
        } catch (IOException ignored) {

        }
    }

    public List getFileList() {
        return myFinder.getFileList();
    }

    public String getStartDir() {
        return startDir;
    }

    public void setStartDir(String startDir) {
        this.startDir = startDir;
    }
}
