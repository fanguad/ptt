package org.nekocode.ptt.objects;

import org.jetbrains.annotations.NotNull;
import org.nekocode.ptt.TableTopType;
import org.nekocode.ptt.painters.EmptyPainter;
import org.nekocode.ptt.tabletop.TableTop;

import javax.swing.Painter;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * An image as a visible object.  The image is assumed to have no transparent pixels.
 *
 * @author fanguad
 */
public class Image extends VisibleObject {

    private final java.awt.Image image;
    private Rectangle2D bounds;

    public Image(String filename, Point center) {
        image = Toolkit.getDefaultToolkit().createImage(filename);

        int height = image.getHeight(null);
        int width = image.getWidth(null);

        int x = center.x - width / 2;
        int y = center.y - height / 2;

        bounds = new Rectangle2D.Double(x, y, x + width, y + height);
    }

    @Override
    @NotNull
    public Painter<VisibleObject> getPainter(TableTopType type) {
        if (seenBy == TableTopType.NONE || (seenBy == TableTopType.CONTROLLER && type == TableTopType.VIEWER)) {
            // this object is hidden OR
            // only the controller is allowed to see this, but this painter is for the viewer
            return EmptyPainter.getInstance();
        }

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    @NotNull
    public Rectangle2D getBounds() {
        return bounds;
    }

    @Override
    public boolean contains(double x, double y) {
        return getBounds().contains(x, y);
    }
}
