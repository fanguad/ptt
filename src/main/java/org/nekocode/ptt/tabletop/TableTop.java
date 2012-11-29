package org.nekocode.ptt.tabletop;

import org.nekocode.ptt.TableTopType;
import org.nekocode.ptt.objects.VisibleObject;

import javax.swing.JComponent;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Primary display container.  Contains an ordered list of VisibleObjects along with their state.
 *
 * @author fanguad
 */
public class TableTop extends JComponent {
    /**
     * Main image buffer.
     */
    private Image buffer1;
    /**
     * Secondary image buffer.
     */
    private Image buffer2;

    private TableTopType type;
    private final List<VisibleObject> objects;

    public TableTop(TableTopType type) {
        addComponentListener(new ResizeHandler());
        this.type = type;
        objects = new ArrayList<>();
    }

    public void repaint() {
        Image target = buffer2;
        if (target == null) {
            return;
        }

        Graphics2D g2 = (Graphics2D) target.getGraphics();
        for (VisibleObject object : objects) {
            Dimension d = getSize();
            object.getPainter(type).paint(g2, object, d.width, d.height);
        }
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
}
