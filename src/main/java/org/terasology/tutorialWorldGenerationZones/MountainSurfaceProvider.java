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

import org.terasology.math.geom.BaseVector2i;
import org.terasology.math.geom.Vector2f;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.SimplexNoise;
import org.terasology.utilities.procedural.SubSampledNoise;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProvider;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Updates;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

@Updates(@Facet(SurfaceHeightFacet.class))
public class MountainSurfaceProvider implements FacetProvider {

    private Noise mountainNoise;
    public final static int MIN_MOUNTAIN_HEIGHT = 80;
    private final static float MOUNTAIN_HEIGHT_MULTIPLIER = 1f;

    @Override
    public void setSeed(long seed) {
        mountainNoise = new SubSampledNoise(new SimplexNoise(seed), new Vector2f(0.001f, 0.001f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        SurfaceHeightFacet facet = region.getRegionFacet(SurfaceHeightFacet.class);
        for (BaseVector2i pos : facet.getWorldRegion().contents()) {
            float height = facet.getWorld(pos);
            if (height >= MIN_MOUNTAIN_HEIGHT) {
                float noiseValue = mountainNoise.noise(pos.x(), pos.y());
                noiseValue = (noiseValue + 1) / 2; //Into range [0..1]
                facet.setWorld(pos, height + noiseValue * (height - MIN_MOUNTAIN_HEIGHT) * MOUNTAIN_HEIGHT_MULTIPLIER);
            }
        }
    }
}
