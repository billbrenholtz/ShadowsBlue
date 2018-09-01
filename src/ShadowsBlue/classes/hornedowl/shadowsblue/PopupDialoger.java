/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hornedowl.shadowsblue;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.nio.file.Paths;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author liberty
 */
class PopupDialoger {

    private final JFrame parentFrame;
    private final Rectangle screenRect;

    public PopupDialoger(JFrame fr, Rectangle scrRect) {
        parentFrame = fr;
        screenRect = scrRect;
    }

    public JDialog createFull(Thimg t, int xpos, int ypos) {
        final JPanel dp = new JPanel();
        dp.setLayout(new BorderLayout());
        if (Paths.get(t.getFilename()).getParent() == null) {
            dp.add(new JLabel(new ImageIcon(getClass().getResource(t.getFilename()))));
        } else {
            dp.add(new JLabel(new ImageIcon(t.getFilename())));
        }
        JDialog imageFrame = new JDialog(parentFrame);
        imageFrame.setTitle(t.getFilename());

        imageFrame.add("Center", dp);
        imageFrame.pack();

        Point p = parentFrame.getLocationOnScreen();
        xpos = xpos + p.x;
        ypos = ypos + p.y;

        Dimension d = imageFrame.getSize();
        //Check for postioning off right side of screen
        if ((xpos + d.width) > screenRect.width) {
            xpos = xpos - d.width;
        }
        //And bottom of screen
        if ((ypos + d.height) > screenRect.height) {
            ypos = ypos - d.height;
        }
        imageFrame.setLocation(xpos, ypos);
        imageFrame.setVisible(true);
        return imageFrame;
    }
}
