package org.nekocode.ptt.tabletop;

import org.apache.log4j.Logger;
import org.nekocode.ptt.TableTopType;
import org.nekocode.ptt.objects.VisibleObject;

import javax.swing.JComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
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
    public static final AffineTransform DEFAULT_TRANSFORM = new AffineTransform();

    /**
     * Main image buffer.
     */
    private BufferedImage buffer1;
    /**
     * Secondary image buffer.
     */
    private BufferedImage buffer2;

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
    private double scale = 1;

    public TableTop(TableTopType type) {
        addComponentListener(new ResizeHandler());
        ScrollZoomAdapter scrollZoomAdapter = new ScrollZoomAdapter();
        addMouseListener(scrollZoomAdapter);
        addMouseWheelListener(scrollZoomAdapter);
        addMouseMotionListener(scrollZoomAdapter);
        this.type = type;
        objects = new ArrayList<>();
        centerTransform = new AffineTransform();
        scaleTransform = new AffineTransform();

        @SuppressWarnings("unused") // the drop target needs to be created, but doesn't need to be attached to anything
        DropTarget dt = new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, new FileDropListener());

    }

    public void redraw() {
        BufferedImage target = buffer2;
        if (target == null) {
//            logger.warn("Cannot repaint - buffer is null");
            return;
        }

        Graphics2D g2 = (Graphics2D) target.getGraphics();
        // save current transform
        AffineTransform previous = g2.getTransform();
        g2.setTransform(DEFAULT_TRANSFORM);
        g2.clearRect(0, 0, target.getWidth(), target.getHeight());

        // set transform for translate and scale
        AffineTransform transform = new AffineTransform();
        transform.concatenate(centerTransform);
        transform.concatenate(scaleTransform);
        g2.setTransform(transform);

        for (VisibleObject object : objects) {
            Dimension d = getSize();
            object.getPainter(type).paint(g2, object, d.width, d.height);
        }

        // reset previous transform
        g2.setTransform(previous);

        buffer2 = buffer1;
        buffer1 = target;

        super.repaint();
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
                dtde.acceptDrag(DnDConstants.ACTION_REFERENCE);
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
                dtde.acceptDrop(DnDConstants.ACTION_REFERENCE);
                try {
                    List<File> files = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);

                    for (File file : files) {
                        logger.debug("Adding new file: " + file.getAbsolutePath());
                        VisibleObject image = new org.nekocode.ptt.objects.Image(file.getAbsolutePath(), dtde.getLocation());
                        objects.add(image);
                    }
                    redraw();

                } catch (UnsupportedFlavorException | IOException e) {
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

    private class ScrollZoomAdapter extends MouseAdapter {
        private Point startPoint;

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1)
            {
                startPoint = e.getPoint();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            setCursor(Cursor.getDefaultCursor());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if ((e.getModifiersEx() & MouseEvent.BUTTON1_DOWN_MASK) == MouseEvent.BUTTON1_DOWN_MASK)
            {
                Point currentPoint = e.getPoint();
                int xDiff = currentPoint.x - startPoint.x;
                int yDiff = currentPoint.y - startPoint.y;
                startPoint = currentPoint;

                centerTransform.translate(xDiff, yDiff);
                redraw();
            }
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            double clicks = e.getPreciseWheelRotation();
            scale *= 1 + (clicks * .1);
            scaleTransform.setToScale(scale, scale);
            redraw();
        }
    }
}
