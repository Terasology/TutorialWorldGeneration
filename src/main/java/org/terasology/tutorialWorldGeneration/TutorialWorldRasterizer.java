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

import org.terasology.math.ChunkMath;
import org.terasology.math.JomlUtil;
import org.terasology.math.geom.Vector3i;
import org.terasology.registry.CoreRegistry;
import org.terasology.world.block.Block;
import org.terasology.world.block.BlockManager;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.ElevationFacet;
import org.terasology.world.generation.facets.SurfacesFacet;

public class TutorialWorldRasterizer implements WorldRasterizer {

    private Block dirt;
    private Block grass;

    @Override
    public void initialize() {
        dirt = CoreRegistry.get(BlockManager.class).getBlock("CoreAssets:Dirt");
        grass = CoreRegistry.get(BlockManager.class).getBlock("CoreAssets:Grass");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        ElevationFacet elevationFacet = chunkRegion.getFacet(ElevationFacet.class);
        SurfacesFacet surfacesFacet = chunkRegion.getFacet(SurfacesFacet.class);
        for (Vector3i position : chunkRegion.getRegion()) {
            float surfaceHeight = elevationFacet.getWorld(position.x, position.z);
            if (surfacesFacet.get(JomlUtil.from(position))) {
                chunk.setBlock(ChunkMath.calcRelativeBlockPos(position), grass);
            } else if (position.y < surfaceHeight) {
                chunk.setBlock(ChunkMath.calcRelativeBlockPos(position), dirt);
            }
        }
    }
}
