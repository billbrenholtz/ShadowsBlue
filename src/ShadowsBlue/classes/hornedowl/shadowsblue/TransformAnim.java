/*
 *
 * Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
import java.awt.geom.AffineTransform;
import java.awt.image.ImageObserver;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Animation images translating around a canvas.
 */
@SuppressWarnings("serial")
class TransformAnim extends JPanel implements PropertyChangeListener, MouseListener, KeyListener {

    private List<Thimg> movers;
    private String picRootDir;

    private int firstInLine;
    private final PropertyDude propDude;

    private final long sleepAmount = 75;
    private int biw, bih;
    private volatile boolean running = false;
    private volatile Thread animationThread;
    private ImageResizer resizer;
    private boolean resizing = false;
    private final Rectangle screenRect;
    private JFrame parent;
    private TopXferHandler handler;

    public TransformAnim(JFrame parent, Rectangle rect) {

        //Use settings from last run
        propDude = new PropertyDude();
        propDude.getProperties();
        picRootDir = propDude.getPicRootDir();
        bih = propDude.getInitialHeight();

        //some empty list for now
        movers = new ArrayList<>();

        screenRect = rect;
        this.parent = parent;

        biw = screenRect.width;

        //Drag and drop handler
        handler = new TopXferHandler();
        handler.addPropertyChangeListener(this);

        setTransferHandler(handler);
        addMouseListener(this);
        addKeyListener(this);

    }

    //traverse a directory to get image files names
    public void collectImages() {
        Thimg newThimg;
        DirectoryCrawl dc = new DirectoryCrawl(picRootDir);
        dc.run();
        for (Object o : dc.getFileList()) {
            newThimg = new Thimg((String) o, bih);
            movers.add(newThimg);
        }
        scrambleImages();
        firstInLine = 0;
        if (!movers.isEmpty()) {
            movers.get(firstInLine).setX(getSize().width - movers.get(firstInLine).getThumbWidth());
        }
    }

    public void getSerializedImages() throws IOException, ClassNotFoundException {
        MoversSaver ms = new MoversSaver();
        Thimg newThimg;
        List saved = ms.get();
        for (Object t : saved) {
            newThimg = (Thimg) t;
            //serialization did not save these two things
            newThimg.setThumbImage(newThimg.getFilename(), bih);
            newThimg.setAfTran(new AffineTransform());
            movers.add(newThimg);
        }
        firstInLine = propDude.getFirstInLine();

    }

    /**
     * randomize the image list
     */
    private void scrambleImages() {
        Random generator = new Random(System.currentTimeMillis());
        Thimg thirdThimg;
        int pick;
        for (int i = 0; i < movers.size(); i++) {
            pick = i;
            while (pick == i) {
                pick = generator.nextInt(movers.size() - 1);
            }
            thirdThimg = movers.get(i);
            movers.set(i, movers.get(pick));
            movers.set(pick, thirdThimg);
        }
    }

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

        //paint those images that are within our boudary
        for (Thimg th : movers) {
            if (th.isVisible(biw)) {
                g2.setTransform(th.getAfTran());
                g2.drawImage(th.getThumbImage(), 0, 0, (img, infoflags, x, y, width, height) -> (infoflags & ImageObserver.ALLBITS) == 0);
            }
        }
        g2.dispose();
    }

    //display full image when image is clicked on
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
            PopupDialoger pd = new PopupDialoger(parent, screenRect);
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
        stop();
    }

    //restart animation
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

    public void start() {
        if (!running()) {
            animationThread = new Thread(() -> {

                while (running()) {
                    if (!movers.isEmpty()) {
                        //is the current BufferedImage size not same as window?
                        //(has window been resized?)
                        Dimension d = getSize();
                        if (biw != d.width) {
                            //reinitialize and remember new BufferedImage width
                            biw = d.width;
                        }
                        if (bih != d.height) {
                            //reinitialize and remember new BufferedImage height
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
                running = false;
            });
            animationThread.setPriority(Thread.MIN_PRIORITY);
            animationThread.setName("RunAnimation");
            animationThread.start();
            running = true;
        }
    }

    public synchronized void stop() {
        if (animationThread != null) {
            running = false;
            animationThread.interrupt();
        }
        animationThread = null;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        //New directory dragged and dropped
        if (evt.getPropertyName().equalsIgnoreCase("newDirStr")) {
            picRootDir = (String) evt.getNewValue();  //New starting directory dropped

            stop(); //Hold everything

            //Clear image cache and get new thumbnails
            movers.clear();
            collectImages();

            start();
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

    private boolean running() {
        return running;
    }
} // End TransformAnim
