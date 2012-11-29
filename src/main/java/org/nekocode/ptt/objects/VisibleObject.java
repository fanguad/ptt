package org.nekocode.ptt.objects;

import org.jetbrains.annotations.NotNull;
import org.nekocode.ptt.TableTopType;
import org.nekocode.ptt.tabletop.TableTop;

import javax.swing.Painter;
import java.awt.geom.Rectangle2D;

/**
 * Visible object that can be shared between tabletops.
 *
 * @author fanguad
 */
public abstract class VisibleObject {
    /**
     * Which tabletops can see this object.
     */
    protected TableTopType seenBy;

    /**
     * Retrieves a painter that can draw this object on a tabletop of the requested type.
     *
     * @param type type of tabletop to draw on
     * @return painter for this object
     */
    @NotNull
    public abstract Painter<VisibleObject> getPainter(TableTopType type);

    /**
     * Draws this object to the given tableTop.
     *
     * @param tableTop table to draw object to
     */
//    public abstract void paint(TableTop tableTop);

    /**
     * Returns the bounding box of this object.
     *
     * @return bounding box
     */
    @NotNull
    public abstract Rectangle2D getBounds();

    /**
     * Indicates whether a specific location is contained in this this object.
     *
     * @param x x location
     * @param y y location
     * @return true if object exists at specified location
     */
    public abstract boolean contains(double x, double y);
}
