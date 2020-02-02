/*
 * Logo.java - a canvas for displaying a framed logo.
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
import  java.awt.*;
import  java.net.*;

/**
 * This class is a canvas for displaying an image framed with a 3-dimensional
 * border.
 *
 * @author  John Neffenger
 * @version 25 Jan 1998
 */

public class Logo extends Canvas {
    private final static int   BORDER      = 4;
    private final static int   INNER_INSET = (2 * BORDER) - 1;
    private final static int   PAINT_INSET = (2 * BORDER);
    private final static Color BGBORDER    = Color.lightGray;

    private URL   url;
    private int   imageWidth;
    private int   imageHeight;
    private Color background;
    private Image logo;

    /**
     * Creates a new logo canvas.
     */

    public Logo(URL url, int width, int height, Color background) {
        setLogo(url, width, height, background);
    }

    /**
     * Creates a new logo canvas.
     *
     * @param url         the Web address of the logo image.
     * @param width       the width of the logo image.
     * @param height      the height of the logo image.
     * @param background  the background color displayed when the image is smaller
     *                    than its frame.
     */

    public Logo() {}

    /**
     * Initializes the canvas with a new logo image.
     *
     * @param url         the Web address of the logo image.
     * @param width       the width of the logo image.
     * @param height      the height of the logo image.
     * @param background  the background color displayed when the image is smaller
     *                    than its frame.
     */

    public void setLogo(URL url, int width, int height, Color background) {
        this.url         = url;
        this.imageWidth  = width;
        this.imageHeight = height;
        this.background  = background == null ? getBackground() : background;
        this.logo        = Toolkit.getDefaultToolkit().getImage(url);
        repaint();
    }

    /**
     * Gets the minimum size requested for this component.
     *
     * @return the minimum size that should be used to display this component.
     */

    public Dimension getMinimumSize() {
        return new Dimension(imageWidth + PAINT_INSET, imageHeight + PAINT_INSET);
    }

    /**
     * Gets the preferred size requested for this component.
     *
     * @return the preferred size that should be used to display this component.
     */

    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    /**
     * Called when the component need updated.  Calls paint directly in order to
     * avoid the flicker caused by the default update method clearing the
     * background.
     *
     * @param g  the graphics context for painting on the canvas.
     */

    public void update(Graphics g) {
        paint(g);
    }

    /**
     * Called to paint the framed logo.  Paints the 3-dimensional frame, the
     * background, and the logo image on this canvas.
     *
     * @param g  the graphics context for painting on the canvas.
     */

    public void paint(Graphics g) {
        Dimension size = getSize();
        int width  = size.width;
        int height = size.height;
        g.setColor(BGBORDER);
        g.fill3DRect(0, 0, width, height, true);
        g.draw3DRect(BORDER - 1, BORDER - 1, width - INNER_INSET, height - INNER_INSET, false);
        g.setColor(background);
        g.fillRect(BORDER, BORDER, width - PAINT_INSET, height - PAINT_INSET);
        g.clipRect(BORDER, BORDER, width - PAINT_INSET, height - PAINT_INSET);
        g.drawImage(logo, BORDER, BORDER, this);
    }
}
