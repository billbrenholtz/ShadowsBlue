/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hornedowl.shadowsblue;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author liberty
 */
class ImageResizer extends Thread {

    private final List<Thimg> moversCopy;
    private final int newHeight;

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
        //resize me and everyone behind me
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
