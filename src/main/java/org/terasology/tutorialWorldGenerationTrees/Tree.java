/*
 * Copyright 2018 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.tutorialWorldGenerationTrees;

/**
 * Class for a tree, containing its variables.
 */
public class Tree {
    private static final int TRUNK_HEIGHT = 4;
    private static final int CROWN_HEIGHT = 2;
    private static final int TOP_CROWN_HEIGHT = 1;
    private static final int CROWN_RADIUS = 2;
    private static final int TOP_CROWN_WIDTH = 3;

    /**
     * The height of a tree.
     *
     * @return the tree's height
     */
    public int getHeight() {
        return (TRUNK_HEIGHT + CROWN_HEIGHT + TOP_CROWN_HEIGHT);
    }

    /**
     * The width of a tree.
     *
     * @return the tree's width
     */
    public int getWidth() {
        return (CROWN_RADIUS * 2) + 1;
    }

    /**
     * The height of a tree's trunk.
     *
     * @return the tree's trunk height
     */
    public int getTrunkHeight() {
        return TRUNK_HEIGHT;
    }

    /**
     * The height of a tree's lower crown - part of a tree containing leaves.
     *
     * @return the tree's lower crown height, excluding the upper crown
     */
    public int getCrownHeight() {
        return CROWN_HEIGHT;
    }

    /**
     * The height of a tree's upper crown - typically smaller than lower crown.
     *
     * @return the tree's upper crown height, excluding lower crown
     */
    public int getTopCrownHeight() {
        return TOP_CROWN_HEIGHT;
    }

    /**
     * The radius of a crown, excluding the trunk at the center.
     *
     * @return the radius of the crown - from center
     */
    public int getCrownRadius() {
        return CROWN_RADIUS;
    }

    /**
     * The width of a tree's upper crown
     *
     * @return the width's of the upper crown
     */
    public int getTopCrownWidth() {
        return TOP_CROWN_WIDTH;
    }
}
