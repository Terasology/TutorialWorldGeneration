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

public enum TutorialBiome implements Biome {
    LAND("Land", (short) 1), WATER("Water", (short) 2), SKY("Sky", (short) 3);

    TutorialBiome(String name, short biomeHash) {
        this.name = name;
        this.biomeHash = biomeHash;
    }

    private final String name;
    private final short biomeHash;

    @Override
    public String getId() {
        return "TutorialWorldGeneration:" + this.toString();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public short biomeHash() {
        return biomeHash;
    }
}
