And now for some fun with metadata.  As mentioned before, we need to provide surface height information so that our rasterizer can read this data and put blocks & players in the right place.  Fortunately, the engine provides us with a facet definition for this, `SurfaceHeightFacet` - a 2d representation of the surface of the world.  Each (X; Y) position holds the value of the height of the surface.

Let's start with a skeleton class:
```java
@Produces(SurfaceHeightFacet.class)
public class SurfaceProvider implements FacetProvider {
    @Override
    public void setSeed(long seed) {
    }

    @Override
    public void process(GeneratingRegion region) {
    }
}
```

The key part of this skeleton is the annotation - without it, the world builder will not know how to organize facet providers together.

Now let's reimplement our existing rasterizer data - but lets make it a little more interesting and define our surface as being located y=10, because danger happens at y=10! :P

```java
@Override
public void process(GeneratingRegion region) {
    // Create our surface height facet (we will get into borders later)
    Border3D border = region.getBorderForFacet(SurfaceHeightFacet.class);
    SurfaceHeightFacet facet = new SurfaceHeightFacet(region.getRegion(), border);

    // Loop through every position in our 2d array
    Rect2i processRegion = facet.getWorldRegion();
    for (BaseVector2i position: processRegion.contents()) {
        facet.setWorld(position, 10f);
    }

    // Pass our newly created and populated facet to the region
    region.setRegionFacet(SurfaceHeightFacet.class, facet);
}
```

Some key points to note is that `facet.getWorldRegion()` refers to world coordinates which happen to coincide with `facet.setWorld()`.  There are also methods that deal with the local coordinate system, but it is easier to stick with world positions.

We then add this to our world builder:

```java
@In
private WorldGeneratorPluginLibrary worldGeneratorPluginLibrary;

@Override
protected WorldBuilder createWorld() {
    return new WorldBuilder(worldGeneratorPluginLibrary)
           .addProvider(new SurfaceProvider())
           .addProvider(new SeaLevelProvider(0))
           .addRasterizer(new TutorialWorldRasterizer());
}
```

(Oh, right - don't forget to put in the ```SeaLevelProvider``` so that the game doesn't spawn the player 100 meters underwater! You'll need to add Core as a dependency in the new module's `module.txt` file. Make sure to recompile or do a `gradlew idea` followed by an Intellij restart.)

Now, when we run the rasterizer, we can access this facet data that we have provided.  Lets use it:

```java
@Override
public void generateChunk(CoreChunk chunk, Region chunkRegion) {
    SurfaceHeightFacet surfaceHeightFacet = chunkRegion.getFacet(SurfaceHeightFacet.class);
    for (Vector3i position : chunkRegion.getRegion()) {
        float surfaceHeight = surfaceHeightFacet.getWorld(position.x, position.z);
        if (position.y < surfaceHeight) {
            chunk.setBlock(ChunkMath.calcBlockPos(position), dirt);
        }
    }
}
```

Be sure to get your x and z correct.  The surface height facet uses 2D (x; y) coordinates, while `Vector3i` uses y as height.

Now run the generator again and witness lots of exciting dirt at y=10!

![Facet Production](https://raw.githubusercontent.com/Terasology/TutorialWorldGeneration/master/images/Facet%20Production.png)

[Next: Noise Sampling](Noise-Sampling)