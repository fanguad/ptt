package org.nekocode.ptt.painters;

import org.nekocode.ptt.objects.VisibleObject;

import javax.swing.Painter;
import java.awt.Graphics2D;

/**
 * A painter that does nothing.
 *
 * @author dclark
 */
public class EmptyPainter implements Painter<VisibleObject> {
    private static EmptyPainter INSTANCE = new EmptyPainter();

    public static Painter<VisibleObject> getInstance() {
        return INSTANCE;
    }

    @Override
    public void paint(Graphics2D g, VisibleObject object, int width, int height) {
        // do nothing
    }
}
