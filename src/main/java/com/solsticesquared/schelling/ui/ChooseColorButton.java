/*
 * Copyright 2016 Will Knez <wbknez.dev@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.solsticesquared.schelling.ui;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Represents a {@link JButton} that allows the user to choose a color from a
 * {@link JColorChooser} when clicked and also displays a small rectangle
 * filled with the currently chosen color as a visualization.
 */
public class ChooseColorButton extends JButton {

    /**
     * Represents an {@link ActionListener} that shows a {@link JColorChooser}
     * dialog when activated.
     */
    private static class ChooseColorListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent e) {
            final ChooseColorButton colorButton =
                    (ChooseColorButton)e.getSource();
            final Container targetWindow = this.getTargetWindow(colorButton);
            final Color newColor =
                    JColorChooser.showDialog(targetWindow,
                                             "Choose a new color.",
                                             colorButton.getCurrentColor());

            if(newColor != null) {
                colorButton.setNewColor(newColor);
            }
        }

        /**
         * Returns the {@link JFrame} that contains the specified component
         * for use placing dialogs.
         *
         * @param component
         *        The component to obtain the containing window from.
         * @return The {@link JFrame} that contains a component.
         * @throws NullPointerException
         *         If {@code component} is {@code null}.
         */
        private Container getTargetWindow(final JComponent component) {
            if(component == null) {
                throw new NullPointerException();
            }

            Container targetWindow = component.getParent();

            while(!(targetWindow instanceof JFrame) && targetWindow != null) {
                targetWindow = targetWindow.getParent();
            }

            return targetWindow;
        }
    }

    /** The currently chosen color. */
    private Color   currentColor;

    /**
     * Constructor.
     *
     * @param startingColor
     *        The color to both display and initially "choose".
     * @throws NullPointerException
     *         If {@code startingColor} are {@code null}.
     */
    public ChooseColorButton(final Color startingColor) {
        if(startingColor == null) {
            throw new NullPointerException();
        }

        this.addActionListener(new ChooseColorListener());
        this.setNewColor(startingColor);
    }

    /**
     * Creates a new {@link ImageIcon} with the specified dimensions and
     * filled with the currently chosen color.
     *
     * @param width
     *        The width of the icon to use.
     * @param height
     *        The height of the icon to use.
     * @return A new {@link ImageIcon} filled with the currently chosen color.
     * @throws IllegalArgumentException
     *         If either {@code width} or {@code height} are less than or
     *         equal to zero.
     */
    private ImageIcon createIcon(final int width, final int height) {
        if(width < 1) {
            throw new IllegalArgumentException("width must be positive!");
        }

        if(height < 1) {
            throw new IllegalArgumentException("height must be positive!");
        }

        // Paint the new icon first.
        final BufferedImage buffer =
                new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        final Graphics2D g = buffer.createGraphics();

        g.setColor(this.currentColor);
        g.fillRect(0, 0, width, height);
        g.setXORMode(Color.darkGray);
        g.drawRect(0, 0, width - 1, height - 1);

        // Clean up.
        g.dispose();
        buffer.flush();

        // Done.
        return new ImageIcon(buffer);
    }

    /**
     * Returns the currently chosen color.
     *
     * @return The currently chosen color.
     */
    public Color getCurrentColor() {
        return this.currentColor;
    }

    /**
     * Sets the currently chosen color to the specified color and creates a new
     * {@link ImageIcon} that is filled with that color as a visualization.
     *
     * @param newColor
     *        The newly selected color to use.
     * @throws NullPointerException
     *         If {@code newColor} is {@code null}.
     */
    public void setNewColor(final Color newColor) {
        if(newColor == null) {
            throw new NullPointerException();
        }

        // Save the color.
        this.currentColor = newColor;

        // Recreate the icon.
        this.setIcon(this.createIcon(16, 16));

        // Refresh.
        this.repaint();
    }
}
