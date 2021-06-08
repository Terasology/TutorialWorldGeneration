# Additional Facet Production

Another neat way to use facets is to use the data from one or more other facets to create a unique dataset.  In this example we will attempt to create random stone houses on the surface of the world.  For this,  we will create a sparse object 3d facet that will contain the bottom-center locations of each of the houses.  From there we can then create a rasterizer that loops through each of the values and creates a house wherever the facet value should contain a house.

But first,  the facet class itself:

```java
public class HouseFacet extends SparseObjectFacet3D<House> {

    public HouseFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
```

There are multiple other options to choose from, but generally they are grouped on their amount of dimensions.
An amount of dimensions in this case is just 2D or 3D. You have a whole lot of these to choose from!
Check it out here: https://github.com/MovingBlocks/Terasology/tree/develop/engine/src/main/java/org/terasology/engine/world/generation/facets/base
As the name suggests, these are base classes. Interfaces are also in that package.


Easy when immortius has already created the plumbing!  

Before we can actually set up the house facet, there's one other thing we need to add first. If we use the ```ElevationFacet``` to check how high to place each house, it restricts us to not having multiple houses one above another if the world generator produces overhanging terrain. In this case it can't, yet, but other [plugins](advanced/plugins.md) may change this, so it's best to be prepared just in case (and for consistency). The ```SurfacesFacet``` stores information on the detailed shape of the surface of the ground, potentially including multiple or 0 heights per column. In this case, we can just set it to initially contain the same data as the ```ElevationFacet```.

```java
@Produces(SurfacesFacet.class)
@Requires(@Facet(ElevationFacet.class))
public class SurfacesProvider implements FacetProvider {

    @Override
    public void setSeed(long seed) {

    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet elevation = region.getRegionFacet(ElevationFacet.class);
        SurfacesFacet surfacesFacet = new SurfacesFacet(region.getRegion(), region.getBorderForFacet(SurfacesFacet.class));

        for (BaseVector2i pos : elevation.getWorldRegion().contents()) {
            int height = (int) Math.ceil(elevation.getWorld(pos)) - 1;
            if (height >= surfacesFacet.getWorldRegion().minY() && height <= surfacesFacet.getWorldRegion().maxY()) {
                surfacesFacet.setWorld(pos.x(), height, pos.y(), true);
            }
        }
        region.setRegionFacet(SurfacesFacet.class, surfacesFacet);
    }
}
```

Now the ```FacetProvider``` for the houses themselves:

```java
@Produces(HouseFacet.class)
@Requires(@Facet(SurfacesFacet.class))
public class HouseProvider implements FacetProvider {

    private Noise noise;

    @Override
    public void setSeed(long seed) {
        noise = new WhiteNoise(seed);
    }

    @Override
    public void process(GeneratingRegion region) {

        Border3D border = region.getBorderForFacet(HouseFacet.class).extendBy(0, 8, 4);
        HouseFacet facet = new HouseFacet(region.getRegion(), border);
        SurfacesFacet surfacesFacet = region.getRegionFacet(SurfacesFacet.class);

        Region3i worldRegion = surfacesFacet.getWorldRegion();

        for (int wz = worldRegion.minZ(); wz <= worldRegion.maxZ(); wz++) {
            for (int wx = worldRegion.minX(); wx <= worldRegion.maxX(); wx++) {
                for (int surfaceHeight : surfacesFacet.getWorldColumn(wx, wz)) {

                    // TODO: check for overlap
                    if (noise.noise(wx, wz) > 0.99) {
                        facet.setWorld(wx, surfaceHeight, wz, new House());
                    }
                }
            }
        }

        region.setRegionFacet(HouseFacet.class, facet);
    }
}
```

Here we are discarding 99% of the positions and placing a house at the remaining positions at the surface height when in the range of the facet.

Now we will go ahead and define what a house object actually is. Because our houses are simple 8x8x8 hollow cubes, we can easily create a house class and define a `getExtent()` method that returns 4.

```java
public class House {
    public int getExtent() {
        return 4;
    }
}
```

The last remaining piece is to rasterize these "houses" into the world and then plug all them in to the world builder.

```java
public class HouseRasterizer implements WorldRasterizer {
    private Block stone;

    @Override
    public void initialize() {
        stone = CoreRegistry.get(BlockManager.class).getBlock("CoreAssets:Stone");
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        HouseFacet houseFacet = chunkRegion.getFacet(HouseFacet.class);

        for (Entry<BaseVector3i, House> entry : houseFacet.getWorldEntries().entrySet()) {
            // there should be a house here
            // create a couple 3d regions to help iterate through the cube shape, inside and out
            Vector3i centerHousePosition = new Vector3i(entry.getKey());
            int extent = entry.getValue().getExtent();
            centerHousePosition.add(0, extent, 0);
            Region3i walls = Region3i.createFromCenterExtents(centerHousePosition, extent);
            Region3i inside = Region3i.createFromCenterExtents(centerHousePosition, extent - 1);

            // loop through each of the positions in the cube, ignoring the inside
            for (Vector3i newBlockPosition : walls) {
                if (chunkRegion.getRegion().encompasses(newBlockPosition)
                        && !inside.encompasses(newBlockPosition)) {
                    chunk.setBlock(ChunkMath.calcBlockPos(newBlockPosition), stone);
                }
            }
        }
    }
}
```

Now just add the `SurfacesProvider`, `HouseProvider` and `HouseRasterizer` to the world builder.

```java
    @Override
    protected WorldBuilder createWorld(long seed) {
        return new WorldBuilder(seed)
                .addProvider(new SurfaceProvider())
                .addProvider(new SeaLevelProvider(0))
                .addProvider(new MountainsProvider())
                .addProvider(new SurfacesProvider())
                .addProvider(new HouseProvider())
                .addRasterizer(new TutorialWorldRasterizer())
                .addRasterizer(new HouseRasterizer());
    }
```

Bingo.  The world is now a village of boring stone dwelling hermits!

<fig src="/_media/img/additional-facet-production-1.png" alt="additional house facet 1"></fig>
<fig src="/_media/img/additional-facet-production-2.png" alt="additional house facet 2"></fig>

Notice though that there are problems. Some houses are missing walls and roofs. 
This will happen at the chunk boundary where the neighboring chunk does not know that it should be generating the edge of the house in its chunk.
