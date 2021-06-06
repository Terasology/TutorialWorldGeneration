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

import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3ic;
import org.terasology.core.world.generator.facetProviders.BiomeProvider;
import org.terasology.core.world.generator.facetProviders.DefaultFloraProvider;
import org.terasology.core.world.generator.facetProviders.DefaultTreeProvider;
import org.terasology.core.world.generator.facetProviders.PerlinBaseSurfaceProvider;
import org.terasology.core.world.generator.facetProviders.PerlinHillsAndMountainsProvider;
import org.terasology.core.world.generator.facetProviders.PerlinHumidityProvider;
import org.terasology.core.world.generator.facetProviders.PerlinOceanProvider;
import org.terasology.core.world.generator.facetProviders.PerlinRiverProvider;
import org.terasology.core.world.generator.facetProviders.PerlinSurfaceTemperatureProvider;
import org.terasology.core.world.generator.facetProviders.PlateauProvider;
import org.terasology.core.world.generator.facetProviders.SeaLevelProvider;
import org.terasology.core.world.generator.facetProviders.SurfaceToDensityProvider;
import org.terasology.core.world.generator.rasterizers.FloraRasterizer;
import org.terasology.core.world.generator.rasterizers.TreeRasterizer;
import org.terasology.engine.core.SimpleUri;
import org.terasology.engine.entitySystem.entity.EntityRef;
import org.terasology.engine.logic.spawner.FixedSpawner;
import org.terasology.engine.registry.CoreRegistry;
import org.terasology.engine.registry.In;
import org.terasology.engine.world.block.Block;
import org.terasology.engine.world.block.BlockManager;
import org.terasology.engine.world.chunks.Chunks;
import org.terasology.engine.world.chunks.Chunk;
import org.terasology.engine.world.generation.BaseFacetedWorldGenerator;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.generation.WorldBuilder;
import org.terasology.engine.world.generation.WorldRasterizer;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.engine.world.generator.RegisterWorldGenerator;
import org.terasology.engine.world.generator.plugin.WorldGeneratorPluginLibrary;
import org.terasology.engine.world.zones.ConstantLayerThickness;
import org.terasology.engine.world.zones.LayeredZoneRegionFunction;
import org.terasology.engine.world.zones.SingleBlockRasterizer;
import org.terasology.engine.world.zones.Zone;

import static org.terasology.engine.world.zones.LayeredZoneRegionFunction.LayeredZoneOrdering.ABOVE_GROUND;
import static org.terasology.engine.world.zones.LayeredZoneRegionFunction.LayeredZoneOrdering.GROUND;
import static org.terasology.engine.world.zones.LayeredZoneRegionFunction.LayeredZoneOrdering.SHALLOW_UNDERGROUND;

@RegisterWorldGenerator(id = "zonedperlin", displayName = "ZonedPerlin", description = "Perlin world generator using zones")
public class ZonedPerlinWorldGenerator extends BaseFacetedWorldGenerator {

    private final FixedSpawner spawner = new FixedSpawner(0, 0);

    @In
    private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

    public ZonedPerlinWorldGenerator(SimpleUri uri) {
        super(uri);
    }

    @Override
    public Vector3f getSpawnPosition(EntityRef entity) {
        return spawner.getSpawnPosition(getWorld(), entity);
    }

    @Override
    protected WorldBuilder createWorld() {
        int seaLevel = 32;
        Vector2i spawnPos = new Vector2i(0, 0); // as used by the spawner

        return new WorldBuilder(worldGeneratorPluginLibrary)
                .setSeaLevel(seaLevel)
                .addProvider(new SeaLevelProvider(seaLevel))
                .addProvider(new PerlinHumidityProvider())
                .addProvider(new PerlinSurfaceTemperatureProvider())

                //The surface layer, containing things that sit on top of the ground
                .addZone(new Zone("Surface", new LayeredZoneRegionFunction(new ConstantLayerThickness(10), ABOVE_GROUND))
                        .addProvider(new DefaultFloraProvider())
                        .addProvider(new DefaultTreeProvider())
                        .addRasterizer(new FloraRasterizer())
                        .addRasterizer(new TreeRasterizer())

                        //A zone for the ocean, existing in between the ground height and sea level
                        .addZone(new Zone("Ocean", (x, y, z, region) ->
                                (int) Math.floor(region.getFacet(ElevationFacet.class).getWorld(x, z)) < y && y <= seaLevel)
                                .addRasterizer(new WorldRasterizer() {
                                    private Block water;

                                    @Override
                                    public void initialize() {
                                        water = CoreRegistry.get(BlockManager.class).getBlock("CoreAssets:water");
                                    }

                                    @Override
                                    public void generateChunk(Chunk chunk, Region chunkRegion) {
                                        for (Vector3ic pos : Chunks.CHUNK_REGION) {
                                            chunk.setBlock(pos, water);
                                        }
                                    }
                                })))

                //The layer for the ground
                .addZone(new Zone("Ground", new LayeredZoneRegionFunction(new ConstantLayerThickness(10), GROUND))
                        .addProvider(new PerlinBaseSurfaceProvider())
                        .addProvider(new PerlinRiverProvider())
                        .addProvider(new PerlinOceanProvider())
                        .addProvider(new PerlinHillsAndMountainsProvider())
                        .addProvider(new BiomeProvider())
                        .addProvider(new SurfaceToDensityProvider())
                        .addProvider(new PlateauProvider(spawnPos, seaLevel + 4, 10, 30))

                        //The default zone for areas which aren't part of the other zones
                        .addZone(new Zone("Default", () -> true)
                                .addZone(new Zone("Grass top", new LayeredZoneRegionFunction(new ConstantLayerThickness(1), GROUND))
                                        .addRasterizer(new SingleBlockRasterizer("CoreAssets:grass")))
                                .addZone(new Zone("Dirt", new LayeredZoneRegionFunction(new ConstantLayerThickness(20), SHALLOW_UNDERGROUND))
                                        .addRasterizer(new SingleBlockRasterizer("CoreAssets:dirt"))))

                        //A zone controlling the mountains
                        .addZone(new Zone("Mountains", (x, y, z, region) -> y >= MountainSurfaceProvider.MIN_MOUNTAIN_HEIGHT
                                && (int) Math.floor(region.getFacet(ElevationFacet.class).getWorld(x, z)) == y)
                                .addProvider(new MountainSurfaceProvider())
                                .addZone(new Zone("Mountain top", new LayeredZoneRegionFunction(new ConstantLayerThickness(1), GROUND))
                                        .addRasterizer(new SingleBlockRasterizer("CoreAssets:Snow"))))

                        //A zone controlling the beaches
                        .addZone(new Zone("Beach", (x, y, z, region) ->
                                region.getFacet(ElevationFacet.class).getWorld(x, z) < seaLevel + 3)
                                .addRasterizer(new SingleBlockRasterizer("CoreAssets:Sand"))))

                //The underground layer, which just fills the underground with stone
                .addZone(new Zone("Underground", new LayeredZoneRegionFunction(new ConstantLayerThickness(1000), SHALLOW_UNDERGROUND))
                        .addRasterizer(new SingleBlockRasterizer("CoreAssets:Stone")))

                .addPlugins();
    }
}
