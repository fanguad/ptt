package org.nekocode.ptt.tabletop;

import org.apache.log4j.Logger;
import org.nekocode.ptt.TableTopType;
import org.nekocode.ptt.objects.VisibleObject;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Primary display container.  Contains an ordered list of VisibleObjects along with their state.
 *
 * @author fanguad
 */
public class TableTop extends JComponent {
    private static final Logger logger = Logger.getLogger(TableTop.class);

    /**
     * Main image buffer.
     */
    private Image buffer1;
    /**
     * Secondary image buffer.
     */
    private Image buffer2;

    private TableTopType type;

    /**
     * List of objects being displayed.  They are ordered (earlier elements are lower layers/hidden by later elements)
     * and should be synchronized between controller and viewer.
     */
    private final List<VisibleObject> objects;
    /**
     * Affine transform that determines where the center of the screen looks.
     * this should be synced between controller and viewer
     */
    private AffineTransform centerTransform;
    /**
     * Affine transform that determines the display scale.
     * this should be independent between controller and viewer
     */
    private AffineTransform scaleTransform;

    public TableTop(TableTopType type) {
        addComponentListener(new ResizeHandler());
        this.type = type;
        objects = new ArrayList<>();
        centerTransform = new AffineTransform();
        scaleTransform = new AffineTransform();

        @SuppressWarnings("unused") // the drop target needs to be created, but doesn't need to be attached to anything
        DropTarget dt = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new FileDropListener());

    }

    public void repaint() {
        Image target = buffer2;
        if (target == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) target.getGraphics();
        // save current transform
        AffineTransform previous = g2.getTransform();

        // set transform for translate and scale
        AffineTransform transform = new AffineTransform();
        transform.concatenate(scaleTransform);
        transform.concatenate(centerTransform); // translate happens first, so is appended last
        g2.setTransform(transform);

        for (VisibleObject object : objects) {
            Dimension d = getSize();
            object.getPainter(type).paint(g2, object, d.width, d.height);
        }

        // reset previous transform
        g2.setTransform(previous);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Image image = buffer1;
        if (image != null) {
            g.drawImage(image, 0, 0, null);
        }
    }

    public Graphics2D getGraphics() {
        return (Graphics2D) buffer2.getGraphics();
    }

    private class ResizeHandler extends ComponentAdapter {
        @Override
        public void componentResized(ComponentEvent e) {
            Dimension d = getSize();
            buffer1 = new BufferedImage(d.width, d.height, BufferedImage.TYPE_3BYTE_BGR);
            buffer2 = new BufferedImage(d.width, d.height, BufferedImage.TYPE_3BYTE_BGR);
        }

        @Override
        public void componentShown(ComponentEvent e) {
            Dimension d = getSize();
            buffer1 = new BufferedImage(d.width, d.height, BufferedImage.TYPE_3BYTE_BGR);
            buffer2 = new BufferedImage(d.width, d.height, BufferedImage.TYPE_3BYTE_BGR);
        }

        @Override
        public void componentHidden(ComponentEvent e) {
            buffer1 = null;
            buffer2 = null;
        }
    }



    /**
     * Handles dragging and dropping into this window.
     */
    private class FileDropListener implements DropTargetListener {

        /**
         * Approves lists of files.
         *
         * @param dtde drag event
         */
        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
            } else {
                dtde.rejectDrag();
            }
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                try {
                    List<File> files = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    // TODO do stuff here
                } catch (UnsupportedFlavorException e) {
                    logger.error("error processing file drop", e);
                } catch (IOException e) {
                    logger.error("error processing file drop", e);
                }
            } else {
                dtde.rejectDrop();
            }
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
            logger.debug("dropActionChanged: " + dtde.toString());
        }
    }
}
