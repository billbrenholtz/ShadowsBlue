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
 * @author Bill Brenholtz
 *
 * Workhorse of Files.walkFileTree
 */
public class Finder extends SimpleFileVisitor<Path> {

    private final PathMatcher matcher;
    private final ArrayList<String> fileList;

    Finder() {
        //Look for GIFs, JPGs, PNGs
        matcher = FileSystems.getDefault().getPathMatcher("glob:*.{jpg,png,gif,JPG,PNG,GIF}");
        fileList = new ArrayList<>();
    }

    /**
     * Add each filename matching pattern to list
     *
     * @param file - the file to check
     * @param attrs
     * @return
     */
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

    /**
     * Use this if this is first run or something else is wrong
     */
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
