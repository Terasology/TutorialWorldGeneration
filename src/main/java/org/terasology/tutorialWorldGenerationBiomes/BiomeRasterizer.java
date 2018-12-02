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

import org.terasology.math.ChunkMath;
import org.terasology.math.geom.Vector3i;
import org.terasology.world.chunks.CoreChunk;
import org.terasology.world.generation.Facet;
import org.terasology.world.generation.Region;
import org.terasology.world.generation.Requires;
import org.terasology.world.generation.WorldRasterizer;
import org.terasology.world.generation.facets.SeaLevelFacet;
import org.terasology.world.generation.facets.SurfaceHeightFacet;

@Requires({@Facet(SeaLevelFacet.class), @Facet(SurfaceHeightFacet.class)})
public class BiomeRasterizer implements WorldRasterizer {
    @Override
    public void initialize() { }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        SurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
        SeaLevelFacet seaLevelFacet = chunkRegion.getFacet(SeaLevelFacet.class);
        for (Vector3i position : chunkRegion.getRegion()) {
            if (position.y > Math.max(seaLevelFacet.getSeaLevel(), surfaceHeightFacet.getWorld(position.x, position.z)) + 10) {
                chunk.setBiome(ChunkMath.calcBlockPos(position), TutorialBiome.SKY);
            } else if (surfaceHeightFacet.getWorld(position.x, position.z) + 1 > seaLevelFacet.getSeaLevel()) {
                chunk.setBiome(ChunkMath.calcBlockPos(position), TutorialBiome.LAND);
            }
            else {
                chunk.setBiome(ChunkMath.calcBlockPos(position), TutorialBiome.WATER);
            }
        }
    }
}
