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
package org.terasology.tutorialWorldGenerationBiomes;

import org.terasology.biomesAPI.Biome;
import org.terasology.gestalt.naming.Name;

public enum TutorialBiome implements Biome {
    LAND("Land"),
    WATER("Water"),
    SKY("Sky");

    private final Name id;
    private final String displayName;

    TutorialBiome(String displayName) {
        this.id = new Name("TutorialWorldGeneration:" + name());
        this.displayName = displayName;
    }

    @Override
    public Name getId() {
        return id;
    }

    @Override
    public String getDisplayName() {
        return this.displayName;
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}
