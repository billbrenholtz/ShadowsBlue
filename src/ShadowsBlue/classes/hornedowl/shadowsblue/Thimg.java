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
 * @author Bill Brenholtz
 * 
 * Container class that holds thumbnail image, original file name,
 * AffineTransform and position data
 */
class Thimg implements ImageObserver, Serializable {

    private transient Image thumbImage;         //thumbnail image
    private Point thumbPos;                     //current image position
    private int ix, iy;
    private Dimension thumbDimension;           //size of thumbnail
    private transient AffineTransform afTran;   //needed to move image
    private String filename;                    //file name of original image

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

        setThumbImage(hite);
    }

    /**
     * create the scaled thumbnail of original image
     * @param ht - height to make thumbnail; width is scaled accordingly
     */
    public final void setThumbImage(int ht) {
        Path checkFile = Paths.get(filename);
        ImageIcon tmpIcon;
        if (checkFile.getParent() == null) {
            //no parent, so this is stock image included with app
            tmpIcon = new ImageIcon(getClass().getResource(filename));
        }
        else {
            tmpIcon = new ImageIcon(filename);
        }
        //resize image
        thumbImage = tmpIcon.getImage().getScaledInstance(-1, ht, Image.SCALE_DEFAULT);
        //wait for image to finish resizing and then set new dimension
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

    /**
     * translate image to new position
     */
    public void step() {
        thumbPos.translate(ix, iy);
    }

    /**
     * update the AffineTransform
     * don't bother with anything that isn't visible
     * @param width - area to determine visibility
     */
    public void setTranslation(int width) {
        if (isVisible(width)) {
            afTran.setToIdentity();
            afTran.translate(thumbPos.x, thumbPos.y);
        }
    }

    /**
     * determine visibility of me
     * @param width - available area
     * @return 
     */
    public boolean isVisible(int width) {
        return (thumbPos.x >= -1 * thumbDimension.width) && (thumbPos.x <= width);
    }

    /**
     * create new thumbnail with 
     * @param newHeight - height of thumbnail; width is scaled
     */
    public void resizeImage(int newHeight) {
        Path checkFile = Paths.get(filename);
        ImageIcon tmpIcon;
        if (checkFile.getParent() == null) {
            //no parent, so this is stock image included with app
            tmpIcon = new ImageIcon(getClass().getResource(filename));
        }
        else {
            tmpIcon = new ImageIcon(filename);
        }
        //create new scaled thumbnail
        Image newImg = tmpIcon.getImage().getScaledInstance(-1, newHeight, Image.SCALE_DEFAULT);
        //do this to start imageUpdate()
        double newThumbHeight = newImg.getHeight(this);
    }

    /**
     * wait for image to finish scaling before setting new thumbnail and new dimension
     * @param mimg
     * @param infoflags
     * @param ix
     * @param iy
     * @param width
     * @param height
     * @return 
     */
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

    /**
     * clone me from
     * @param cloned - Thimg to be cloned
     */
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
