package org.nekocode.ptt.objects;

import org.apache.log4j.Logger;
import org.nekocode.ptt.TableTopType;
import org.nekocode.ptt.painters.EmptyPainter;
import org.nekocode.ptt.painters.ImagePainter;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.swing.Painter;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * An image as a visible object.  The image is assumed to have no transparent pixels.
 *
 * @author fanguad
 */
public class Image extends VisibleObject {

    private static final Logger logger = Logger.getLogger(Image.class);

    private final BufferedImage image;
    private Rectangle2D bounds;
    private AffineTransform transform;

    public Image(File file, Point center) throws IOException {
//        image = Toolkit.getDefaultToolkit().createImage(filename);
        image = ImageIO.read(file);
        transform = new AffineTransform();

        int height = image.getHeight(null);
        int width = image.getWidth(null);

        int x = center.x - width / 2;
        int y = center.y - height / 2;
        transform.translate(x, y);
        logger.info(String.format("(%d, %d) %dx%d", x, y, width, height));

//        transform.translate(height / 2, width / 2);

//        bounds = new Rectangle2D.Double(x, y, x + width, y + height);
        bounds = new Rectangle2D.Double(0, 0, width, height);
    }

    @Override
    @Nonnull
    public Painter<VisibleObject> getPainter(TableTopType type) {
        if (seenBy == TableTopType.CONTROLLER && type == TableTopType.VIEWER) {
            // only the controller is allowed to see this, but this painter is for the viewer
            return EmptyPainter.getInstance();
        }

        return ImagePainter.getInstance();
    }

    @Override
    @Nonnull
    public Rectangle2D getBounds() {
        return bounds;
    }

    @Override
    public boolean contains(double x, double y) {
        return getBounds().contains(x, y);
    }

    public java.awt.Image getImage() {
        return image;
    }

    public AffineTransform getTransform() {
        return transform;
    }
}
