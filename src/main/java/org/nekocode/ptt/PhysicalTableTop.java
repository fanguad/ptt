package org.nekocode.ptt;

import org.nekocode.ptt.tabletop.TableTop;

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.Dimension;

/**
 * Main class.  Launches the Physical Table Top (ptt) application.
 *
 * @author fanguad
 */
public class PhysicalTableTop {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Physical Table Top");

        // TODO use terracotta to connect.  if there are no controllers connected, then we're the controller
        frame.setContentPane(new TableTop(TableTopType.CONTROLLER));

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(640, 480));
        frame.setSize(new Dimension(640, 480));
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
    }
}
