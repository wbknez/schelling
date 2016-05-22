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

package com.solsticesquared.schelling.update;

import com.solsticesquared.schelling.SchellingExplorer;
import com.solsticesquared.schelling.move.MovementMethod;

/**
 * Represents a mechanism that updates the locations of an entire group of
 * agents using arbitrary logic.
 *
 * <p>
 *     This is designed to prevent the {@link MovementMethod} algorithms from
 *     needing to handle single vs. batch update styles (as defined by the
 *     user).  Admittedly, the nomenclature gets a little hazy (as this has
 *     no relation to {@link com.solsticesquared.schelling.task.UpdateTask})
 *     but that is its intended purpose.
 * </p>
 */
public interface AgentUpdater {

    /**
     * Updates a list of agents using the specified arbitrary logic.
     *
     * @param moveMethod
     *        The method of movement (solid, liquid, or swap) to use.
     * @param model
     *        The model to use.
     * @throws NullPointerException
     *         If either {@code moveMethod} or {@code model} are {@code null}.
     */
    void update(final MovementMethod moveMethod, final SchellingExplorer model);
}
