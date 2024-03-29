/*
 * Copyright 2017 MovingBlocks
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
package org.terasology.tutorialWorldGenerationZones;

import org.joml.Vector2f;
import org.joml.Vector2ic;
import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.SimplexNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Updates;
import org.terasology.engine.world.generation.facets.ElevationFacet;

@Updates(@Facet(ElevationFacet.class))
public class MountainSurfaceProvider implements FacetProvider {
    public static final int MIN_MOUNTAIN_HEIGHT = 80;
    private static final float MOUNTAIN_HEIGHT_MULTIPLIER = 1f;

    private Noise mountainNoise;

    @Override
    public void setSeed(long seed) {
        mountainNoise = new SubSampledNoise(new SimplexNoise(seed), new Vector2f(0.001f, 0.001f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);
        for (Vector2ic pos : facet.getWorldArea()) {
            float height = facet.getWorld(pos);
            if (height >= MIN_MOUNTAIN_HEIGHT) {
                float noiseValue = mountainNoise.noise(pos.x(), pos.y());
                noiseValue = (noiseValue + 1) / 2; //Into range [0..1]
                facet.setWorld(pos, height + noiseValue * (height - MIN_MOUNTAIN_HEIGHT) * MOUNTAIN_HEIGHT_MULTIPLIER);
            }
        }
    }
}
