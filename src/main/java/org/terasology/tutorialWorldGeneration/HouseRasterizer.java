/*
 * Copyright 2015 MovingBlocks
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

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.chunks.Chunk;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizer;

import java.util.Map.Entry;

public class HouseRasterizer implements WorldRasterizer {
    private Block stone;

    @Override
    public void initialize() {
        stone = CoreRegistry.get(BlockManager.class).getBlock("CoreAssets:Stone");
    }

    @Override
    public void generateChunk(Chunk chunk, Region chunkRegion) {
        HouseFacet houseFacet = chunkRegion.getFacet(HouseFacet.class);

        for (Entry<Vector3ic, House> entry : houseFacet.getWorldEntries().entrySet()) {
            // there should be a house here
            // create a couple 3d regions to help iterate through the cube shape, inside and out
            Vector3i centerHousePosition = new Vector3i(entry.getKey());
            int extent = entry.getValue().getExtent();
            centerHousePosition.add(0, extent, 0);
            BlockRegion walls = new BlockRegion(centerHousePosition).expand(extent, extent, extent);
            BlockRegion inside = new BlockRegion(centerHousePosition).expand(extent - 1, extent - 1, extent - 1);

            // loop through each of the positions in the cube, ignoring the indices
            // reusing one mutable vector is more efficient than creating a new one for each toRelative()
            Vector3i tmp = new Vector3i();
            for (Vector3ic newBlockPosition : walls) {
                if (chunkRegion.getRegion().contains(newBlockPosition)
                        && !inside.contains(newBlockPosition)) {
                    chunk.setBlock(Chunks.toRelative(newBlockPosition, tmp), stone);
                }
            }
        }
    }
}
