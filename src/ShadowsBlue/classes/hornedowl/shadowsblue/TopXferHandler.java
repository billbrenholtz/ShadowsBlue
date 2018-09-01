/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hornedowl.shadowsblue;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.TransferHandler;

/**
 *
 * @author liberty
 */
class TopXferHandler extends TransferHandler {

    private final PropertyChangeSupport pcs;
    private String newDirStr;

    public TopXferHandler() {
        pcs = new PropertyChangeSupport(this);
        newDirStr = "";
    }

    public boolean canImport(TransferHandler.TransferSupport support) {
        return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
    }

    @SuppressWarnings("unchecked")
    public boolean importData(TransferHandler.TransferSupport support) {
        if (!canImport(support)) {
            return false;
        }

        Transferable t = support.getTransferable();

        try {
            List<File> newDir = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
            setNewDirStr(newDir.get(0).getAbsolutePath());
        } catch (UnsupportedFlavorException | IOException e) {
            return false;
        }

        return true;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    private void setNewDirStr(String newDirStr) {
        String old = this.newDirStr;
        this.newDirStr = newDirStr;
        pcs.firePropertyChange("newDirStr", old, this.newDirStr);
    }

}
