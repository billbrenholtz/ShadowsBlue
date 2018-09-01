/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hornedowl.shadowsblue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author valance
 */
@SuppressWarnings("unused")
public class DirectoryCrawl {

    private String startDir;
    private final Finder myFinder;

    public DirectoryCrawl(String rootDir) {
        this.startDir = rootDir;
        myFinder = new Finder();
    }

    public void run() {
        if (startDir.isEmpty()) {
            myFinder.useDefault();
            return;
        }
        if (!Paths.get(startDir).toFile().exists()) {
            return;
        }
        try {
            Files.walkFileTree(Paths.get(startDir), myFinder);
        } catch (IOException ignored) {

        }
    }

    public List getFileList() {
        return myFinder.getFileList();
    }

    public Finder getMyFinder() {
        return myFinder;
    }

    public String getStartDir() {
        return startDir;
    }

    public void setStartDir(String startDir) {
        this.startDir = startDir;
    }
}
