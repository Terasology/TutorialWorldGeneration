// Copyright 2021 The Terasology Foundation
// SPDX-License-Identifier: Apache-2.0

package org.terasology.tutorialWorldGenerationMesas;

import org.joml.Vector2f;
import org.joml.Vector2ic;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.SimplexNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.FacetProviderPlugin;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Requires;
import org.terasology.engine.world.generation.UpdatePriority;
import org.terasology.engine.world.generation.Updates;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.engine.world.generation.facets.SeaLevelFacet;
import org.terasology.engine.world.generator.plugin.RegisterPlugin;

@RegisterPlugin
@Requires(@Facet(SeaLevelFacet.class))
@Updates(value = @Facet(ElevationFacet.class), priority = UpdatePriority.PRIORITY_LOW)
public class MesaProvider implements FacetProviderPlugin {
    private Noise mesaNoise;

    @Override
    public void setSeed(long seed) {
        mesaNoise = new SubSampledNoise(new BrownianNoise(new SimplexNoise(seed + 5), 2), new Vector2f(0.001f, 0.001f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);
        SeaLevelFacet seaLevelFacet = region.getRegionFacet(SeaLevelFacet.class);

        int seaLevel = seaLevelFacet.getSeaLevel();
        float mesaHeight = 50;

        // loop through every position on our 2d array
        BlockAreac processRegion = facet.getWorldArea();
        for (Vector2ic position : processRegion) {
            float mesaness = mesaNoise.noise(position.x(), position.y());

            // to generate a mesa, just raise the entire area by mesaHeight
            // only generate mesas above sea level
            if (mesaness > 0.6 && facet.getWorld(position) > seaLevel + 5) {
                facet.setWorld(position, facet.getWorld(position) + mesaHeight);
            }
        }
    }
}
