package hornedowl.shadowsblue;

import java.awt.BorderLayout;
import java.nio.file.Paths;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Bill Brenholtz
 *
 * create JDialog with full sized image
 */
class PopupDialoger extends JDialog {

    public PopupDialoger(JFrame fr) {
        super(fr);
    }

    /**
     *
     * @param t - contains the image to be displayed
     * @return
     */
    public void init(Thimg t) {
        final JPanel dp = new JPanel();
        dp.setLayout(new BorderLayout());
        if (Paths.get(t.getFilename()).getParent() == null) {
            //using my stock images that came with app
            dp.add(new JLabel(new ImageIcon(getClass().getResource(t.getFilename()))));
        } else {
            dp.add(new JLabel(new ImageIcon(t.getFilename())));
        }
        setTitle(t.getFilename());

        add("Center", dp);
        pack();
    }
}
