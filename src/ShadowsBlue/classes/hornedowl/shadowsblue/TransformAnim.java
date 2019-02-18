package hornedowl.shadowsblue;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_RENDER_SPEED;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * JPanel that performs the image animation, resizing, etc..
 */
@SuppressWarnings("serial")
class TransformAnim extends JPanel implements PropertyChangeListener, MouseListener, KeyListener {

    private List<Thimg> movers;                 //the data that will be scrolled
    private String picRootDir;                  //root directory of all present pictures
    private int firstInLine;                    //index in movers of image on far right of window
    private final long sleepAmount = 75;        //millisecs between translations
    private int biw, bih;                       //current window width, height
    private boolean running = false;            //are animations running?
    private AnimationThread animationThread;    //performs the animations
    private ImageResizer resizer;               //thread to resize current images
    private boolean resizing = false;           //are we resizing:?
    private final Rectangle screenRect;         //screen dimension
    private final JFrame parent;
    private TopXferHandler handler;             //performs image directory change via drag and drop
    private final Fetcher fetcher;

    public TransformAnim(JFrame parent, Fetcher f, Rectangle rect) {

        //some empty list for now
        movers = new ArrayList<>();

        screenRect = rect;
        this.parent = parent;
        fetcher = f;

        //use whole width of screen and saved or default height
        biw = screenRect.width;
        bih = fetcher.getSavedHeight();
    }

    /**
     * more setup better done outside constructor
     */
    public void init() {
        //Drag and drop handler
        handler = new TopXferHandler();
        handler.addPropertyChangeListener(this);
        setTransferHandler(handler);
        //stop and starts scrolling and clicking for full image displays
        addMouseListener(this);
        addKeyListener(this);
        
        movers = fetcher.getMovers();
        picRootDir = fetcher.getPicRootDir();
        firstInLine = fetcher.getFirstInLine();
    }

    /**
     * perform the actual painting of images in the correct locations
     * @param g 
     */
    @Override
    public void paint(Graphics g) {

        super.paint(g);

        //Nothing to paint
        if (movers.isEmpty()) {
            return;
        }

        //clear the slate
        Graphics2D g2 = (Graphics2D) g;
        g2.setBackground(getBackground());
        g2.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(KEY_RENDERING, VALUE_RENDER_SPEED);
        g2.clearRect(0, 0, biw, bih);

        //paint those images that are within our boundary
        for (Thimg th : movers) {
            if (th.isVisible(biw)) {
                g2.setTransform(th.getAfTran());
                g2.drawImage(th.getThumbImage(), 0, 0, (img, infoflags, x, y, width, height) -> (infoflags & ImageObserver.ALLBITS) == 0);
            }
        }
        g2.dispose();
    }

    /**
     * display full image in separate window when clicked on
     * @param e 
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        if (movers.isEmpty()) {
            return;
        }

        int xpos = e.getX();
        int ypos = e.getY();
        Thimg xthimg = null;
        for (int i = 0; i < movers.size(); i++) {
            //Determine who was clicked on, if any, by x position
            if (xpos >= movers.get(i).getX() && xpos < (movers.get(i).getX() + movers.get(i).getThumbWidth())) {
                xthimg = movers.get(i);
                break;
            }
        }
        if (xthimg != null) {
            //found it
            PopupDialoger pd = new PopupDialoger(parent, screenRect);
            //create JDialog and diaplay it
            pd.createFull(xthimg, xpos, ypos);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /**
     * pause animation on mouse enter
     * @param e 
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        stop();
    }

    /**
     * restart animation on mouse exit
     * @param e 
     */
    @Override
    public void mouseExited(MouseEvent e) {
        start();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
//        displayInfo(e, "KEY PRESSED: ");
        int keyCode = e.getKeyCode();
        int modifiersEx = e.getModifiersEx();
        //ctnrl-alt-u
        if (keyCode == 85 && modifiersEx == 640) {
            JOptionPane.showMessageDialog(this, "Created by Bill Brenholtz");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * start animation thread
     */
    public void start() {
        animationThread = new AnimationThread();
        animationThread.setPriority(Thread.NORM_PRIORITY);
        animationThread.setName("RunAnimation");
        animationThread.start();
        running = true;
    }

    /**
     * stop animation thread
     */
    public synchronized void stop() {
        if (animationThread != null) {
            running = false;
            animationThread.interrupt();
            animationThread = null;
        }
    }

    /**
     * change scrolling images by dragging and dropping a new directory on the scrolling window
     * @param evt 
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        //New directory dragged and dropped
        if (evt.getPropertyName().equalsIgnoreCase("newDirStr")) {
            picRootDir = (String) evt.getNewValue();

            stop(); //Hold everything

            //Clear image cache and get new thumbnails
            movers.clear();
            fetcher.collectImages(picRootDir, getSize());
            movers= fetcher.getMovers();
            firstInLine = 0;

            start();    //start up again
        }
    }

    /**
     * Thread that does heavy lifting of animation
     */
    private class AnimationThread extends Thread {

        @Override
        public void run() {
            while (running) {
                if (!movers.isEmpty()) {
                    //has window been resized?
                    Dimension d = getSize();
                    if (biw != d.width) {
                        //adjust to and remember new window width
                        biw = d.width;
                    }
                    if (bih != d.height) {
                        //adjust to and remember new window height
                        bih = d.height;
                        //we could detect mulitple resize operations
                        //if so kill any active resize operations until the
                        //window has stopped resizing
                        if (resizer != null && resizer.isAlive()) {
                            resizer.interrupt();
                        }
                        //start resizing images thread
                        resizer = new ImageResizer(movers, bih);
                        resizing = true;
                        resizer.start();
                    }
                    //if resizing thread has stopped switch the current ArrayList
                    //with the ArrayList containing the resized copies
                    if (resizing) {
                        if (resizer != null && !resizer.isAlive()) {
                            resizing = false;
                            movers = resizer.getMoversCopy();
                        }
                    }
                    //move the first image in line forward
                    movers.get(firstInLine).step();
                    //is the first in line now off screen?
                    if (movers.get(firstInLine).getX() > biw) {
                        //yes, the next Thimg in the ArrayList is now the leader
                        firstInLine++;
                        //handle Arraylist wraparound
                        if (firstInLine > movers.size() - 1) {
                            firstInLine = 0;
                        }
                        //move the new leader forward
                        movers.get(firstInLine).step();
                    }
                    //set translation for firstInLine
                    movers.get(firstInLine).setTranslation(biw);
                    //set x position and translation for everyone behind leader
                    //to end of ArrayList
                    for (int i = firstInLine; i < movers.size(); i++) {
                        if (i != firstInLine) {
                            movers.get(i).setX(movers.get(i - 1).getX() - movers.get(i).getThumbWidth());
                            movers.get(i).setTranslation(biw);
                        }
                    }
                    if (firstInLine != 0) {
                        //wrapped around so now process everyone from
                        //ArrayList(0) up to the firstInLine
                        for (int i = 0; i < firstInLine; i++) {
                            if (i == 0) {
                                //ArrayList(0) will always be behind last
                                //member of the ArrayList
                                movers.get(0).setX(movers.get(movers.size() - 1).getX() - movers.get(0).getThumbWidth());
                            } else {
                                //Everyone else is behind the index in front
                                //of them
                                movers.get(i).setX(movers.get(i - 1).getX() - movers.get(i).getThumbWidth());
                            }
                            movers.get(i).setTranslation(biw);
                        }
                    }
                    repaint();
                }
                try {
                    Thread.sleep(sleepAmount);
                } catch (InterruptedException ignored) {
                    running = false;
                }
            }
        }
    }

    public List<Thimg> getMovers() {
        return movers;
    }

    public String getPicRootDir() {
        return picRootDir;
    }

    public int getFirstInLine() {
        return firstInLine;
    }

    public int getBih() {
        return bih;
    }
}
