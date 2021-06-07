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

import org.joml.Vector2ic;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProvider;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Produces;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.engine.world.generation.facets.SurfacesFacet;

/**
 * Copies the data from the ElevationFacet into the SurfacesFacet
 */
@Produces(SurfacesFacet.class)
@Requires(@Facet(ElevationFacet.class))
public class SurfacesProvider implements FacetProvider {

    @Override
    public void setSeed(long seed) {

    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet elevation = region.getRegionFacet(ElevationFacet.class);
        SurfacesFacet surfacesFacet = new SurfacesFacet(region.getRegion(),
                region.getBorderForFacet(SurfacesFacet.class));

        for (Vector2ic pos : elevation.getWorldArea()) {
            int height = (int) Math.ceil(elevation.getWorld(pos)) - 1;
            if (height >= surfacesFacet.getWorldRegion().minY() && height <= surfacesFacet.getWorldRegion().maxY()) {
                surfacesFacet.setWorld(pos.x(), height, pos.y(), true);
            }
        }
        region.setRegionFacet(SurfacesFacet.class, surfacesFacet);
    }
}
