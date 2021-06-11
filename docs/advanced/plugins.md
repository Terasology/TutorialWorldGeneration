# Plugins

Now that we have completed our world full of stone houses.  How can we let other modules use this world and add some more fancy features?  Easy, with plugins!

The general principal here is that a module can register a rasterizer or facet producer and have it inserted into the world generator at runtime. This is as simple as adding ```.addPlugins()``` to our world builder initialization like this:

```java
    @Override
    protected WorldBuilder createWorld() {
        return new WorldBuilder(worldGeneratorPluginLibrary)
                .setSeaLevel(0)
                .addProvider(new SurfaceProvider())
                .addProvider(new SeaLevelProvider(0))
                .addProvider(new MountainsProvider())
                .addProvider(new HouseProvider())
                .addRasterizer(new TutorialWorldRasterizer())
                .addRasterizer(new HouseRasterizer())
                .addPlugins();
    }
```

Now, all external modules will have their rasterizers and producers added in alongside the ones already specified.

To create a plugin-able rasterizer you need 2 things: 

1. Decorate the class with the annotation ```@RegisterPlugin```
2. Inherit from the special ```WorldRasterizerPlugin```

Here is a sample rasterizer that will make all space that is below the sea level and above the surface into water...

```java
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
            if (position.y() < seaLevel && position.y() >= surfaceHeight) {
                chunk.setBlock(Chunks.toRelative(position, tmp), water);
            }
        }
    }
}
```

Looks much like our other rasterizers.

However, in order to make our lakes more prominent we need to alter the surface to dip down an extra amount to hold our water.  We do this by supplying a facet provider plugin.  Pluggable facet providers also need 2 things:

1. Decorate the class with the annotation ```@RegisterPlugin```
2. Inherit from the special ```FacetProviderPlugin```

Here is a modified mountain provider that instead creates dips in the surface...

```java
@RegisterPlugin
@Updates(@Facet(ElevationFacet.class))
public class LakesProvider implements FacetProviderPlugin {

    private Noise lakeNoise;

    @Override
    public void setSeed(long seed) {
        lakeNoise = new SubSampledNoise(new BrownianNoise(new PerlinNoise(seed + 3), 4), new Vector2f(0.001f, 0.001f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);
        float lakeDepth = 40;
        // loop through every position on our 2d array
        BlockAreac processRegion = facet.getWorldRegion();
        for (Vector2ic position : processRegion) {
            float additiveLakeDepth = lakeNoise.noise(position.x(), position.y()) * lakeDepth;
            // dont bother adding lake height,  that will allow unaffected regions
            additiveLakeDepth = Math.clamp(additiveLakeDepth, -lakeDepth, 0);

            facet.setWorld(position, facet.getWorld(position) + additiveLakeDepth);
        }
    }
}
```

> [!NOTE]
> Make sure to use a different seed value than other noise or you could cancel each other out.

<fig src="_media/img/PluginLakes.jpg" alt="the lakes plugin">The Lakes plugin in action.</fig>
