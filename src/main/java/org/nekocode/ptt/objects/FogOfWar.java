package org.nekocode.ptt.objects;

import org.jetbrains.annotations.NotNull;
import org.nekocode.ptt.TableTopType;
import org.nekocode.ptt.painters.EmptyPainter;

import javax.swing.Painter;
import java.awt.geom.Rectangle2D;

/**
 * Represents Fog of War.  A visible fog of war is translucent to the controller, but opaque to the viewer.
 */
public class FogOfWar extends VisibleObject {
    @NotNull
    @Override
    public Painter<VisibleObject> getPainter(TableTopType type) {
        if (this.seenBy == TableTopType.CONTROLLER) {
            // if the viewer can't see it, don't bother painting the object
            return EmptyPainter.getInstance();
        }

        return null;
    }

    @NotNull
    @Override
    public Rectangle2D getBounds() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean contains(double x, double y) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
