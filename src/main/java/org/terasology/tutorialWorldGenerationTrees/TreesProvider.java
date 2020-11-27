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

import org.terasology.math.Region3i;
import org.terasology.utilities.procedural.Noise;
import org.terasology.utilities.procedural.WhiteNoise;
import org.terasology.world.generation.Border3D;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.FacetProviderPlugin;
import org.terasology.world.generation.GeneratingRegion;
import org.terasology.world.generation.Produces;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.facets.SurfacesFacet;

/**
 * Class for placing trees, using WhiteNoise.
 */
@Produces(TreesFacet.class)
@Requires(@Facet(SurfacesFacet.class))
public class TreesProvider implements FacetProviderPlugin {
    private Noise treesNoise;

    @Override
    public void setSeed(long seed) {
        treesNoise = new WhiteNoise(seed);
    }

    @Override
    public void process(GeneratingRegion region) {
        Border3D border = region.getBorderForFacet(TreesFacet.class).extendBy(0, 7, 1);
        TreesFacet facet = new TreesFacet(region.getRegion(), border);

        SurfacesFacet surfacesFacet = region.getRegionFacet(SurfacesFacet.class);
        Region3i worldRegion = surfacesFacet.getWorldRegion();

        for (int wz = worldRegion.minZ(); wz <= worldRegion.maxZ(); wz++) {
            for (int wx = worldRegion.minX(); wx <= worldRegion.maxX(); wx++) {
                for (int surfaceHeight : surfacesFacet.getWorldColumn(wx, wz)) {

                    // check if point is within this region
                    if (facet.getWorldRegion().encompasses(wx, surfaceHeight, wz)) {

                        if (treesNoise.noise(wx, surfaceHeight, wz) > 0.99) {
                            facet.setWorld(wx, surfaceHeight, wz, new Tree());
                        }
                    }
                }
            }
        }

        region.setRegionFacet(TreesFacet.class, facet);
    }
}
