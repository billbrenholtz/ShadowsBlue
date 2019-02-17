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
 * @author liberty
 */
class ShadowsFrame extends JFrame {

    private final TransformAnim myAnim;
    private final Rectangle screenRect;
    private Fetcher fetcher;

    private ShadowsFrame(Fetcher f) {

        fetcher = f;
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        screenRect = gc.getBounds();

        myAnim = new TransformAnim(this, fetcher, screenRect);
        myAnim.setPreferredSize(new Dimension(screenRect.width, myAnim.getBih()));

    }

    public void init() {
        setTitle("Shadows Blue");
        setLayout(new BorderLayout());
        add("Center", myAnim);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                //save starting directory, height and first in line
                fetcher.saveProperties(myAnim.getPicRootDir(), myAnim.getBih(), myAnim.getFirstInLine());
                //serialize the list of Thimg's
                MoversSaver ms = new MoversSaver();
                ms.save(myAnim.getMovers());
                System.exit(0);
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                myAnim.start();
            }

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
            f.getSerializedImages();
        } catch (IOException | ClassNotFoundException ex) {
            f.collectImages(f.getPicRootDir(), frame.getMyAnim().getSize());
        }
        frame.getMyAnim().init();
        frame.getMyAnim().start();
    }
}
