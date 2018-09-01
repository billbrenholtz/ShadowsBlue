/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hornedowl.shadowsblue;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import static java.nio.file.FileVisitResult.CONTINUE;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author valance
 */
public class Finder extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;
    private final ArrayList<String> fileList;

    Finder() {
        matcher = FileSystems.getDefault().getPathMatcher("glob:*.{jpg,png,gif,JPG,PNG,GIF}");
        fileList = new ArrayList<>();
    }

    // Invoke the pattern matching method on each file.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            fileList.add(file.toString());
        }
        return CONTINUE;
    }

    public List getFileList() {
        return fileList;
    }
    
    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        //noinspection ThrowablePrintedToSystemOut
        System.err.println(exc);
        return CONTINUE;
    }

    public void useDefault() {
        fileList.add("photo_01.jpg");
        fileList.add("photo_02.jpg");
        fileList.add("photo_03.jpg");
        fileList.add("photo_04.jpg");
        fileList.add("photo_05.jpg");
        fileList.add("photo_06.jpg");
        fileList.add("photo_07.jpg");
        fileList.add("photo_08.jpg");
        fileList.add("photo_09.jpg");
        fileList.add("photo_10.jpg");
        fileList.add("photo_11.jpg");
        fileList.add("photo_12.jpg");
    }
}
