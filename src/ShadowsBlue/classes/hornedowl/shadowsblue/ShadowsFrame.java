/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hornedowl.shadowsblue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author liberty
 */
class ShadowsFrame extends JFrame implements MouseListener, KeyListener {

    private final PropertyDude myDude;
    private final TransformAnim myAnim;
    private final Rectangle screenRect;

    private ShadowsFrame() {

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        screenRect = gc.getBounds();

        myAnim = new TransformAnim();
        myAnim.setPreferredSize(new Dimension(screenRect.width, myAnim.getBih()));
        myDude = new PropertyDude();

    }

    public void init() {
        setTitle("Shadows Blue");
        setLayout(new BorderLayout());
        add("Center", myAnim);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                //save starting directory, height and first in line
                myDude.saveProperties(myAnim.getPicRootDir(), myAnim.getBih(), myAnim.getFirstInLine());
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
        addMouseListener(this);
        addKeyListener(this);

        pack();

    }

    //display full image when image is clicked on
    @Override
    public void mouseClicked(MouseEvent e) {

        if (myAnim.getMovers().isEmpty()) {
            return;
        }

        int xpos = e.getX();
        int ypos = e.getY();
        Thimg xthimg = null;
        for (int i = 0; i < myAnim.getMovers().size(); i++) {
            //Determine who was clicked on, if any, by x position
            if (xpos >= myAnim.getMovers().get(i).getX() && xpos < (myAnim.getMovers().get(i).getX() + myAnim.getMovers().get(i).getThumbWidth())) {
                xthimg = myAnim.getMovers().get(i);
                break;
            }
        }
        if (xthimg != null) {
            PopupDialoger pd = new PopupDialoger(this, screenRect);
            //noinspection unused
            JDialog jd = pd.createFull(xthimg, xpos, ypos);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    //pause animation
    @Override
    public void mouseEntered(MouseEvent e) {
        myAnim.stop();
    }

    //restart animation
    @Override
    public void mouseExited(MouseEvent e) {
        myAnim.start();
    }

    private TransformAnim getMyAnim() {
        return myAnim;
    }

    public static void main(String argv[]) {
        ShadowsFrame frame = new ShadowsFrame();
        frame.init();
        frame.setVisible(true);
        try {
            frame.getMyAnim().getSerializedImages();
        } catch (IOException | ClassNotFoundException ex) {
            frame.getMyAnim().collectImages();
        }
        frame.getMyAnim().start();
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
}
