package org.nekocode.ptt.painters;

import org.nekocode.ptt.objects.Image;
import org.nekocode.ptt.objects.VisibleObject;

import javax.swing.Painter;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

/**
 * This painter knows how to paint an image.  This painter handles images with transparent pixels.
 *
 * @author fanguad
 */
public class ImagePainter implements Painter<VisibleObject> {
    private static ImagePainter INSTANCE = new ImagePainter();

    public static Painter<VisibleObject> getInstance() {
        return INSTANCE;
    }

    @Override
    public void paint(Graphics2D g, VisibleObject object, int width, int height) {
        if (!(object instanceof Image)) {
            return;
        }

        Image image = (Image) object;

        g.drawImage(image.getImage(), image.getTransform(), null);
    }
}
