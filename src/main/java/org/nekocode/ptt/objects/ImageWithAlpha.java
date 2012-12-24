package org.nekocode.ptt.objects;

import java.awt.Point;

/**
 * An image where some pixels are transparent.
 *
 * @author fanguad
 */
public class ImageWithAlpha extends Image {
    public ImageWithAlpha(String filename, Point center) {
        super(filename, center);
    }
}
