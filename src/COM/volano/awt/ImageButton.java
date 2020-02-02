/*
 * ImageButton.java - an image button with active and inactive images.
 * Copyright (C) 1996-1998 John Neffenger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package COM.volano.awt;
import  java.applet.*;
import  java.awt.*;
import  java.awt.image.*;
import  java.net.*;

/**
 * This class is a canvas for emulating a button which displays an image.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class ImageButton extends Canvas {
    // This color gives you a 3D rectangle with the lit sides lighter than
    // Netscape gray (192, 192, 192) but darker than pure white so that they can
    // still be seen on a pure white background.  Making the inner 3D rectangle a
    // bit lighter acts as a highlight to make the button appear to have smoothly
    // rounded edges.
    private static final Color OUTER_RECT_COLOR = new Color(160, 160, 160);
    private static final Color INNER_RECT_COLOR = Color.lightGray;
    private static final Color BGCOLOR          = Color.lightGray;

    private static final int NEW_LINE = '\n';
    private static final int RETURN   = '\r';
    private static final int SPACE    = ' ';

    private AppletContext context;
    private boolean       border;
    private String        status;
    private int           width;
    private int           height;

    private int           width3D1;
    private int           height3D1;
    private int           width3D2;
    private int           height3D2;
    private boolean       clearButton;
    private boolean       raised = true;
    private Image         normalImage;
    private Image         activeImage;
    private Image         image;
    private Frame         frame;

    /**
     * Creates a new image button.  The button is made active by placing the mouse
     * cursor over it.  An action event is generated when the button is clicked.
     *
     * @param context  the applet context.
     * @param border   overlay a 3-dimensional border to make the button raised
     *                 and indented.
     * @param normal   the URL of the button image when inactive.
     * @param active   the URL of the image image when active.
     * @param status   the message to display in the browser status when the
     *                 button is active.
     * @param width    the width of the button image.
     * @param height   the height of the button image.
     */

    public ImageButton(AppletContext context, URL normal, URL active,
                       boolean border, String status, int width, int height) {
        this.context = context;
        this.border  = border;
        this.status  = status;
        this.width   = width;
        this.height  = height;
        width3D1     = width  - 1;
        height3D1    = height - 1;
        width3D2     = width  - 3;
        height3D2    = height - 3;
        normalImage  = context.getImage(normal);
        activeImage  = context.getImage(active);
        setBackground(BGCOLOR);
    }

    /**
     * Called when the native peer of this component has been created, making the
     * parent frame information available and allowing us to start downloading the
     * images.
     */

    public void addNotify() {
        super.addNotify();
        Container parent = getParent();
        while ((! (parent instanceof Frame)) && (parent != null)) {
            parent = parent.getParent();
        }
        if (parent instanceof Frame) {
            frame = (Frame) parent;
        }

        prepareImage(normalImage, width, height, this);
        prepareImage(activeImage, width, height, this);
        image = normalImage;
    }

    /**
     * Gets the minimum size requested for this component.
     *
     * @return the minimum size that should be used to display this component.
     */

    public Dimension getMinimumSize() {
        return new Dimension(width, height);
    }

    /**
     * Gets the preferred size requested for this component.
     *
     * @return the preferred size that should be used to display this component.
     */

    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    /**
     * This method changes the button to display its active image, changes the
     * cursor to the hand cursor, and shows the status message in the browser
     * status field.
     */

    private void enter() {
        clearButton = true;
        image       = activeImage;
        if (frame != null) {
            frame.setCursor(Frame.HAND_CURSOR);
            context.showStatus(status);
        }
        repaint();
    }

    /**
     * This method modifies the border of the button to make it look depressed.
     */

    private void down() {
        raised = false;
        repaint();
    }

    /**
     * This method modifies the border of the button to make it look raised
     * and generates an action event for the button.
     */

    private void up() {
        raised = true;
        repaint();
        deliverEvent(new Event(this, Event.ACTION_EVENT, ""));
    }

    /**
     * This method changes the button to display its normal image, changes the
     * cursor to the default cursor, and clears the browser status field.
     */

    private void exit() {
        clearButton = true;
        image       = normalImage;
        if (frame != null) {
            frame.setCursor(Frame.DEFAULT_CURSOR);
            context.showStatus("");
        }
        repaint();
    }

    /**
     * Called when the mouse cursor has entered this component.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location where the mouse entered.
     * @param y      the vertical pixel location where the mouse entered.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean mouseEnter(Event event, int x, int y) {
        enter();
        return true;
    }

    /**
     * Called when the mouse button has been pressed.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location where the mouse button was
     *               pressed.
     * @param y      the vertical pixel location where the mouse button was
     *               pressed.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean mouseDown(Event event, int x, int y) {
        down();
        return true;
    }

    /**
     * Called when a key is pressed.
     *
     * @param event  the event definition.
     * @param key    the integer key code.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean keyDown(Event event, int key) {
        if (key == NEW_LINE || key == RETURN || key == SPACE) {
            down();
            return true;
        }
        return false;
    }

    /**
     * Called when the mouse button has been released.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location where the mouse button was
     *               released.
     * @param y      the vertical pixel location where the mouse button was
     *               released.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean mouseUp(Event event, int x, int y) {
        up();
        return true;
    }

    /**
     * Called when a key is released.
     *
     * @param event  the event definition.
     * @param key    the integer key code.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean keyUp(Event event, int key) {
        if (key == NEW_LINE || key == RETURN || key == SPACE) {
            up();
            return true;
        }
        return false;
    }

    /**
     * Called when the mouse cursor has left this component.
     *
     * @param event  the event definition.
     * @param x      the horizontal pixel location where the mouse left.
     * @param y      the vertical pixel location where the mouse left.
     * @return <code>true</code> if this method has handled the event; otherwise
     *         <code>false</code>.
     */

    public boolean mouseExit(Event event, int x, int y) {
        exit();
        return true;
    }

    /**
     * Called when the component's display is updated.  This method clears the
     * canvas if necessary and invokes the <code>paint</code> method.
     *
     * @param g  the graphics context for painting on the canvas.
     */

    public void update(Graphics g) {
        if (clearButton) {
            clearButton = false;
            g.setColor(BGCOLOR);
            g.fillRect(0, 0, width, height);
        }
        paint(g);
    }

    /**
     * Called when the component's display is to be painted.  This method paints
     * the button image and its raised or depressed 3-dimensional border.
     *
     * @param g  the graphics context for painting on the canvas.
     */

    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, width, height, this);
        if (border) {
            g.setColor(OUTER_RECT_COLOR);
            g.draw3DRect(0, 0, width3D1, height3D1, raised);
            g.setColor(INNER_RECT_COLOR);
            g.draw3DRect(1, 1, width3D2, height3D2, raised);
        }
    }
}
