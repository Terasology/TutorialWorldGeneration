/*
 * Copyright 2014 MovingBlocks
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
package org.terasology.tutorialWorldGeneration;

import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.WhiteNoise;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.generation.Border3D;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetBorder;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.SurfacesFacet;

@Produces(HouseFacet.class)
@Requires(@Facet(value = SurfacesFacet.class, border = @FacetBorder(sides = 4, bottom = 8)))
public class HouseProvider implements FacetProvider {

    private Noise noise;

    @Override
    public void setSeed(long seed) {
        noise = new WhiteNoise(seed);
    }

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(HouseFacet.class).extendBy(0, 8, 4);
        HouseFacet facet = new HouseFacet(region.getRegion(), border);
        SurfacesFacet surfacesFacet = region.getRegionFacet(SurfacesFacet.class);

        BlockRegion worldRegion = surfacesFacet.getWorldRegion();

        for (int wz = worldRegion.minZ(); wz <= worldRegion.maxZ(); wz++) {
            for (int wx = worldRegion.minX(); wx <= worldRegion.maxX(); wx++) {
                for (int surfaceHeight : surfacesFacet.getWorldColumn(wx, wz)) {

                    // TODO: check for overlap
                    if (noise.noise(wx, wz) > 0.99) {
                        facet.setWorld(wx, surfaceHeight, wz, new House());
                    }
                }
            }
        }

        region.setRegionFacet(HouseFacet.class, facet);
    }
}
