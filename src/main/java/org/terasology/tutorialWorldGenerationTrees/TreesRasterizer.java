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

import org.joml.Vector3i;
import org.joml.Vector3ic;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.block.BlockRegion;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.chunks.Chunk;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldRasterizerPlugin;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;

import java.util.Map;

/**
 * Class for building up trees.
 */
public class TreesRasterizer implements WorldRasterizerPlugin {
    private Block trunk;
    private Block leaf;

    @Override
    public void initialize() {
        BlockManager blockManager = CoreRegistry.get(BlockManager.class);

        trunk = blockManager.getBlock("CoreAssets:OakTrunk");
        leaf = blockManager.getBlock("CoreAssets:GreenLeaf");
    }

    @Override
    public void generateChunk(Chunk chunk, Region chunkRegion) {
        TreesFacet facet = chunkRegion.getFacet(TreesFacet.class);

        SeaLevelFacet seaLevelFacet = chunkRegion.getFacet(SeaLevelFacet.class);
        int seaLevel = seaLevelFacet.getSeaLevel();

        for (Map.Entry<Vector3ic, Tree> entry : facet.getWorldEntries().entrySet()) {
            Vector3i treePosition = new Vector3i(entry.getKey()).add(0, 1, 0);

            // checks if tree is underwater, if it is then skip - don't build
            if (treePosition.y < seaLevel) {
                continue;
            }

            int height = entry.getValue().getHeight();
            int width = entry.getValue().getWidth();
            int radius = entry.getValue().getCrownRadius();
            int trunkHeight = entry.getValue().getTrunkHeight();
            int crownHeight = entry.getValue().getCrownHeight();
            int topCrownHeight = entry.getValue().getTopCrownHeight();
            int topCrownWidth = entry.getValue().getTopCrownWidth();

            // the position at the far top left corner - used as origin to create regions
            Vector3i treeMinimumPos = new Vector3i(treePosition).sub(radius, 0, radius);

            // creates regions for different parts of a tree
            Vector3i treeMaximumPos = new Vector3i(treeMinimumPos).add(width, height, width);
            BlockRegion treeRegion = new BlockRegion(treeMinimumPos, treeMaximumPos);

            BlockRegion treeTrunk = new BlockRegion(treePosition, new Vector3i(treePosition).add(1, trunkHeight, 1));
            Vector3i treeCrownMin = new Vector3i(treeMinimumPos).add(0, (trunkHeight - 1), 0);
            BlockRegion treeCrown = new BlockRegion(treeCrownMin, new Vector3i(treeCrownMin).add(width, crownHeight,
                    width));
            Vector3i treeTopMin = new Vector3i(treeMinimumPos).add((width - topCrownWidth) / 2,
                    trunkHeight + crownHeight - 1,
                    (width - topCrownWidth) / 2);
            BlockRegion treeTop = new BlockRegion(treeTopMin, new Vector3i(treeTopMin).add(topCrownWidth,
                    topCrownHeight, topCrownWidth));

            // loop through each of the positions in the created regions and placing blocks
            Vector3i tmp = new Vector3i();
            for (Vector3ic newBlockPosition : treeRegion) {
                if (chunkRegion.getRegion().contains(newBlockPosition)) {
                    if (treeTrunk.contains(newBlockPosition)) {
                        chunk.setBlock(Chunks.toRelative(newBlockPosition, tmp), trunk);
                    } else if (!treeTrunk.contains(newBlockPosition)) {
                        if (treeCrown.contains(newBlockPosition) || treeTop.contains(newBlockPosition)) {
                            chunk.setBlock(Chunks.toRelative(newBlockPosition, tmp), leaf);
                        }
                    }
                }
            }
        }
    }
}
