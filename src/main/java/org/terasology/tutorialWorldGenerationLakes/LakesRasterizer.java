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

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.chunks.Chunk;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.WorldRasterizerPlugin;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generator.plugin.RegisterPlugin;

@RegisterPlugin
@Requires({@Facet(SeaLevelFacet.class), @Facet(ElevationFacet.class)})
public class LakesRasterizer implements WorldRasterizerPlugin {
    private Block water;

    @Override
    public void initialize() {
        water = CoreRegistry.get(BlockManager.class).getBlock("CoreAssets:Water");
    }

    @Override
    public void generateChunk(Chunk chunk, Region chunkRegion) {
        ElevationFacet elevationFacet = chunkRegion.getFacet(ElevationFacet.class);
        SeaLevelFacet seaLevelFacet = chunkRegion.getFacet(SeaLevelFacet.class);
        int seaLevel = seaLevelFacet.getSeaLevel();

        Vector3i tmp = new Vector3i();
        for (Vector3ic position : chunkRegion.getRegion()) {
            float surfaceHeight = elevationFacet.getWorld(position.x(), position.z());
            // check to see if the surface is under the sea level and if we are dealing with something above the surface
            if (position.y() < seaLevel && position.y() > surfaceHeight) {
                chunk.setBlock(Chunks.toRelative(position, tmp), water);
            }
        }
    }
}
