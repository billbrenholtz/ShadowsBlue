package hornedowl.shadowsblue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JFrame;

/**
 *
 * @author Bill Brenholtz
 * 
 * JFrame to hold the JPanel that scrolls the images
 */
class ShadowsFrame extends JFrame {

    private final TransformAnim myAnim;     //JPanel with scrolling images
    private final Rectangle screenRect;     //dimension of screen
    private final Fetcher fetcher;          //has the data

    private ShadowsFrame(Fetcher f) {

        fetcher = f;

        //how much real estate do we have?
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        screenRect = gc.getBounds();

        //create the JPanel and set size to full screen width and a remembered or
        //default height
        myAnim = new TransformAnim(this, fetcher, screenRect);
        myAnim.setPreferredSize(new Dimension(screenRect.width, myAnim.getBih()));

    }

    /**
     * more setup better done outside constructor
     */
    public void init() {
        setTitle("Shadows Blue");
        setLayout(new BorderLayout());
        add("Center", myAnim);
        addWindowListener(new WindowAdapter() {
            //save state for next startup
            @Override
            public void windowClosing(WindowEvent e) {
                //save starting directory, height and first in line
                fetcher.saveProperties(myAnim.getPicRootDir(), myAnim.getBih(), myAnim.getFirstInLine());
                //serialize the list of Thimg's
                MoversSaver ms = new MoversSaver();
                ms.save(myAnim.getMovers());
                System.exit(0);
            }
            //start scrolling when deiconified
            @Override
            public void windowDeiconified(WindowEvent e) {
                myAnim.start();
            }
            //stop scrolling when iconified
            @Override
            public void windowIconified(WindowEvent e) {
                myAnim.stop();
            }
        });
        pack();
    }

    private TransformAnim getMyAnim() {
        return myAnim;
    }

    public static void main(String argv[]) {
        Fetcher f = new Fetcher();
        ShadowsFrame frame = new ShadowsFrame(f);
        frame.init();
        frame.setVisible(true);
        try {
            //try to restore images from previous run
            f.getSerializedImages();
        } catch (IOException | ClassNotFoundException ex) {
            //if unable try to collect again if possible
            f.collectImages(f.getPicRootDir(), frame.getMyAnim().getSize());
        }
        frame.getMyAnim().init();
        frame.getMyAnim().start();
    }
}
