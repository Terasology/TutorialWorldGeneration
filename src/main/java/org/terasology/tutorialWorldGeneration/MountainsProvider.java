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

import org.joml.Math;
import org.joml.Vector2f;
import org.joml.Vector2ic;
import org.terasology.engine.utilities.procedural.BrownianNoise;
import org.terasology.engine.utilities.procedural.Noise;
import org.terasology.engine.utilities.procedural.SimplexNoise;
import org.terasology.engine.utilities.procedural.SubSampledNoise;
import org.terasology.engine.world.block.BlockAreac;
import org.terasology.engine.world.generation.ConfigurableFacetProvider;
import org.terasology.engine.world.generation.Facet;
import org.terasology.engine.world.generation.GeneratingRegion;
import org.terasology.engine.world.generation.Updates;
import org.terasology.engine.world.generation.facets.ElevationFacet;
import org.terasology.gestalt.entitysystem.component.Component;
import org.terasology.nui.properties.Range;

@Updates(@Facet(ElevationFacet.class))
public class MountainsProvider implements ConfigurableFacetProvider {

    private Noise mountainNoise;

    //Be sure to initialize this!
    private MountainsConfiguration configuration = new MountainsConfiguration();

    @Override
    public void setSeed(long seed) {
        final float zoomRatio = 0.01f;
        float mountainNoiseZoom = configuration.mountainNoiseZoomRatio * zoomRatio;
        // Default zoom is 0.001f. Max zoom is 0.01f
        mountainNoise = new SubSampledNoise(new BrownianNoise(new SimplexNoise(seed + 2), 8),
                new Vector2f(mountainNoiseZoom, mountainNoiseZoom), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);
        float mountainHeight = configuration.mountainHeight;
        // loop through every position on our 2d array
        BlockAreac processArea = facet.getWorldArea();
        for (Vector2ic position : processArea) {
            // scale our max mountain height to noise (between -1 and 1)
            float additiveMountainHeight = mountainNoise.noise(position.x(), position.y()) * mountainHeight;
            // don't bother subtracting mountain height,  that will allow unaffected regions
            additiveMountainHeight = Math.clamp(additiveMountainHeight, 0, mountainHeight);

            facet.setWorld(position, facet.getWorld(position) + additiveMountainHeight);
        }
    }

    @Override
    public String getConfigurationName() {
        return "Mountains";
    }

    @Override
    public Component getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration) {
        this.configuration = (MountainsConfiguration) configuration;
    }

    private static class MountainsConfiguration implements Component<MountainsConfiguration> {
        @Range(min = 200, max = 500f, increment = 20f, precision = 1, description = "Mountain Height")
        private float mountainHeight = 400;

        @Range(min = 0.1f, max = 1f, increment = 0.1f, precision = 1, description = "Mountain Noise Zoom (Ratio)")
        private float mountainNoiseZoomRatio = 0.1f;

        @Override
        public void copyFrom(MountainsConfiguration other) {
            this.mountainHeight = other.mountainHeight;
            this.mountainNoiseZoomRatio = other.mountainNoiseZoomRatio;
        }
    }
}
