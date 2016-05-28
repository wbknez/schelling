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

import sim.util.gui.LabelledList;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import java.awt.Insets;

/**
 * A collection of utility methods for working with widgets.
 */
public final class UiUtils {

    /**
     * Returns a new {@link Box} object with the same custom insets that
     * MASON uses.
     *
     * @param flags
     *        The type of {@code Box} to create.
     * @return A new {@link Box} with custom insets.
     */
    public static Box createBox(final int flags) {
        return new Box(flags) {

            /** The insets to use as per MASON. */
            final Insets insets = new Insets(2, 4, 2, 4);

            @Override
            public Insets getInsets() {
                return this.insets;
            }
        };
    }

    /**
     * Returns a new {@link LabelledList} object with the same custom insets
     * that MASON uses.
     *
     * @return A new {@link LabelledList} with custom insets.
     */
    public static LabelledList createLabelledList() {
        return new LabelledList() {

            /** The insets to use as per MASON. */
            final Insets insets = new Insets(2, 4, 2, 4);

            @Override
            public Insets getInsets() {
                return this.insets;
            }
        };
    }

    /**
     * Returns a new {@link JScrollPane} object that wraps the specified
     * component and has "as needed" vertical and horizontal scrolling
     * behavior and also has the same custom insets that MASON uses.
     *
     * @param component
     *        The component to wrap.
     * @return A new {@link JScrollPane} with custom insets.
     * @throws NullPointerException
     *         If {@code component} is {@code null}.
     */
    public static JScrollPane createScrollPanel(final JComponent component) {
        return createScrollPane(component,
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }

    /**
     * Returns a new {@link JScrollPane} object that wraps the specified
     * component and has the specified vertical and horizontal scrolling
     * behavior and also has the same custom insets that MASON uses.
     *
     * @param component
     *        The component to wrap.
     * @param vFlags
     *        The type of vertical scrolling behavior to use.
     * @param hFlags
     *        The type of horizontal scrolling behavior to use.
     * @return A new {@link JScrollPane} with custom insets.
     * @throws NullPointerException
     *         If {@code component} is {@code null}.
     */
    public static JScrollPane createScrollPane(final JComponent component,
                                               final int vFlags,
                                               final int hFlags) {
        if(component == null) {
            throw new NullPointerException();
        }

        return new JScrollPane(component, vFlags, hFlags) {

            /** The insets to use as per MASON. */
            final Insets insets = new Insets(0, 0, 0, 0);

            @Override
            public Insets getInsets() {
                return this.insets;
            }
        };
    }

    /**
     * Constructor (private).
     */
    private UiUtils() {
    }
}
