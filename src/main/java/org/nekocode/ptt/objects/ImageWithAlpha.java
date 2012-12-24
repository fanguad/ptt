package org.nekocode.ptt.objects;

import java.awt.Point;
import java.io.File;
import java.io.IOException;

/**
 * An image where some pixels are transparent.
 *
 * @author fanguad
 */
public class ImageWithAlpha extends Image {
    public ImageWithAlpha(File file, Point center) throws IOException {
        super(file, center);
    }
}
