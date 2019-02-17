package hornedowl.shadowsblue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bill Brenholtz
 * 
 * Thread to make a copy of the images at the new height
 */
class ImageResizer extends Thread {

    private final List<Thimg> moversCopy;
    private final int newHeight;

    /**
     * 
     * @param movers - the things to copy
     * @param nh - the new height
     */
    public ImageResizer(List<Thimg> movers, int nh) {
        moversCopy = new ArrayList<>();
        Thimg copyThimg;
        for (Thimg t : movers) {
            copyThimg = new Thimg();
            copyThimg.clone(t);
            moversCopy.add(copyThimg);
        }
        newHeight = nh;
        setPriority(Thread.MAX_PRIORITY);
        setName("resizingImages");
    }

    @Override
    public void run() {
        //do resizing
        for (Thimg t : moversCopy) {
            if (Thread.interrupted()) {
                return;
            }
            t.resizeImage(newHeight);
        }
    }

    public List<Thimg> getMoversCopy() {
        return moversCopy;
    }
}
