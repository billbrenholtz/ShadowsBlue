/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hornedowl.shadowsblue;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.swing.ImageIcon;

/**
 *
 * @author liberty An Image and its current translation
 */
class Thimg implements ImageObserver, Serializable {

    private transient Image thumbImage;
    private Point thumbPos;
    private int ix, iy;
    private Dimension thumbDimension;
    private transient AffineTransform afTran;
    private String filename;

    public Thimg() {
        filename = "";
        afTran = new AffineTransform();
        thumbPos = new Point(0, 0);
        thumbDimension = new Dimension(0, 0);
        ix = 1;
        iy = 0;
        thumbImage = null;
    }

    public Thimg(String newFile, int hite) {
        filename = newFile;
        afTran = new AffineTransform();
        thumbPos = new Point(0, 0);
        thumbDimension = new Dimension(0, 0);
        ix = 1;
        iy = 0;

        setThumbImage(newFile, hite);
    }

    public final void setThumbImage(String filename, int ht) {
        Path checkFile = Paths.get(filename);
        ImageIcon tmpIcon;
        if (checkFile.getParent() == null) {
            tmpIcon = new ImageIcon(getClass().getResource(filename));
        }
        else {
            tmpIcon = new ImageIcon(filename);
        }
        thumbImage = tmpIcon.getImage().getScaledInstance(-1, ht, Image.SCALE_DEFAULT);
        //noinspection unused
        int w = thumbImage.getWidth((img, infoflags, x, y, width, height) -> {
            if ((infoflags & ImageObserver.HEIGHT) != 0) {
                if ((infoflags & ImageObserver.WIDTH) != 0) {
                    thumbDimension = new Dimension(width, height);
                    return false;
                }
            }
            return true;
        });
    }

    //update the transform position and wrapround if necessary
    public void step() {
        thumbPos.translate(ix, iy);
    }

    public void setTranslation(int width) {
        if (isVisible(width)) {
            afTran.setToIdentity();
            afTran.translate(thumbPos.x, thumbPos.y);
        }
    }

    public boolean isVisible(int width) {
        return (thumbPos.x >= -1 * thumbDimension.width) && (thumbPos.x <= width);
    }

    public void resizeImage(int newHeight) {
        Path checkFile = Paths.get(filename);
        ImageIcon tmpIcon;
        if (checkFile.getParent() == null) {
            tmpIcon = new ImageIcon(getClass().getResource(filename));
        }
        else {
            tmpIcon = new ImageIcon(filename);
        }
        Image newImg = tmpIcon.getImage().getScaledInstance(-1, newHeight, Image.SCALE_DEFAULT);
        //noinspection unused
        double newThumbHeight = newImg.getHeight(this);
    }

    @Override
    public boolean imageUpdate(Image mimg, int infoflags, int ix, int iy, int width, int height) {
        if ((infoflags & ImageObserver.HEIGHT) == 0) {
            return true;
        }
        if ((infoflags & ImageObserver.WIDTH) == 0) {
            return true;
        }
        if ((infoflags & ImageObserver.SOMEBITS) != 0) {
            return true;
        }
        thumbImage = mimg;
        thumbDimension = new Dimension(width, height);
        return false;
    }

    public void clone(Thimg cloned) {
        if (cloned == null) {
            return;
        }
        thumbImage = cloned.getThumbImage();
        thumbDimension = cloned.getThumbDimension();
        thumbPos = cloned.getThumbPos();
        ix = cloned.getIx();
        iy = cloned.getIy();
        filename = cloned.getFilename();
        afTran = cloned.getAfTran();
    }

    public int getThumbWidth() {
        return thumbDimension.width;
    }

    public int getX() {
        return thumbPos.x;
    }

    public void setX(int nx) {
        thumbPos.x = nx;
    }

    public Image getThumbImage() {
        return thumbImage;
    }

    private Point getThumbPos() {
        return thumbPos;
    }

    private int getIx() {
        return ix;
    }

    private int getIy() {
        return iy;
    }

    private Dimension getThumbDimension() {
        return thumbDimension;
    }

    public AffineTransform getAfTran() {
        return afTran;
    }

    public void setAfTran(AffineTransform afTran) {
        this.afTran = afTran;
    }

    public String getFilename() {
        return filename;
    }

}
