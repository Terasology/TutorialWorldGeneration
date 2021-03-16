/*
 * Copyright 2016 MovingBlocks
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
package org.terasology.tutorialWorldGenerationLakes;

import org.joml.Vector2f;
import org.joml.Vector2ic;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.PerlinNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProviderPlugin;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Updates;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.engine.world.generator.plugin.RegisterPlugin;

import static org.joml.Math.clamp;

@RegisterPlugin
@Updates(@Facet(ElevationFacet.class))
public class LakesProvider implements FacetProviderPlugin {

    private Noise lakeNoise;

    @Override
    public void setSeed(long seed) {
        lakeNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 3), 4), new Vector2f(0.001f, 0.001f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);
        float lakeDepth = 40;
        // loop through every position on our 2d array
        BlockAreac processRegion = facet.getWorldArea();
        for (Vector2ic position : processRegion) {
            float additiveLakeDepth = lakeNoise.noise(position.x(), position.y()) * lakeDepth;
            // dont bother adding lake height,  that will allow unaffected regions
            additiveLakeDepth = clamp(additiveLakeDepth, -lakeDepth, 0);

            facet.setWorld(position, facet.getWorld(position) + additiveLakeDepth);
        }
    }
}
