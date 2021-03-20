---
title: Facets Across Borders
---

## **Basic topics**

Many people have issues understanding what's going on with the facets across borders.
This is a short tutorial to help you understand it better, pictures included in the following tutorials.
First of all, you need to understand the structure of how the world generation works. The Terasology developers have made a clear structure in this, so everyone is able to add new and great world generators. You have:

* Facets
* FacetProviders
* Rasterizers


**What is a Facet?**
A facet, is a side of a many-sided object. Take for instance a map of the Earth. You have a facet (or side) of the Earth that's all about population. Meanwhile, you also have a facet that's about differences in species across the globe!
A facet is one of these things. What does this have to do with our dear game Terasology you ask? Well, 3D-worlds are as you know not plain 3D fields with only dirt! The worlds contain objects, have properties and so on. One 'facet' of the world might be the biomes. Another one could be where that certain fruit grows.

**What is a FacetProvider?**
A FacetProvider is, as the name itself says, a class that providers the world with the appropriate facet for a certain region. What you typically do in here is scan through the blocks and check for a condition. If that condition is true, you do something, such as placing a facet on that particular coordinate which you scanned through.

**What is a Rasterizer?**
This is where it gets intresting. A rasterizer is what interprets the Facet data. The rasterizer is where you place the blocks in the world.

Simple enough? Well, let's get started with some tips on how to make some more complex shapes, such as pyramids.
Let us start with a problem definition, what do we want?
> We want to have a good looking pyramid to show off to my friends. It must have some sort of path to get to the center. No more than a few pyramids should spawn on a short distance, so it doesn't get too crowded. We also want to make sure that the pyramid isn't floating!





Okay. That seems a whole lot of work. First things first.
We should start out with creating our classes. (Try to find the solution on these issues yourself first before copying it from here! It's the best practice!)

## **Basic setup**

**Our Facet**
```java
public class TempleFacet extends SparseObjectFacet3D<Temple> {

    public TempleFacet(Region3i targetRegion, Border3D border) {
        super(targetRegion, border);
    }
}
```



**Our FacetProvider**
```java
@Requires(@Facet(value = SurfacesFacet.class, border = @FacetBorder(sides = 28, bottom = 28, top = 28)))
@Produces(TempleFacet.class)
public class TempleFacetProvider implements FacetProvider {
    private WhiteNoise noise;

    @Override
    public void process(GeneratingRegion region) {
    }

    @Override public void setSeed(long seed) {
    }
}
```

```java
public class TempleRasterizer implements WorldRasterizer {

    public static int getSize() {
    }

    @Override
    public void initialize() {
    }

    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        
    }
}

```





Now we have set up you can continue.
We only have to change two classes basically, the provider and the rasterizer.

## **Adding the Meat!**
**The TempleFacetProvider**




First of all, you want to make sure you have added a border on the @Requires annotation Facet. What this does is make sure the Facet provides enough space.
```java
@Requires(@Facet(value = SurfacesFacet.class, border = @FacetBorder(sides = 28, bottom = 28, top = 28)))
```
You should add a field with type `WhiteNoise `. We use this to generate randomly.
This gets the border of the TempleFacet and adds 30 by it on the sides/top/bottom.
```java
Border3D border = region.getBorderForFacet(TempleFacet.class).extendBy(30, 30, 30);
```
Create a temple facet with the specified region and borders (You have made a variable above (border))
```java
TempleFacet facet = new TempleFacet(region.getRegion(), border);
```
This takes the SurfacesFacet from the region. It's used to get the location of the land's surfaces, as that's where the temples are
```java    
SurfacesFacet surfacesFacet = region.getRegionFacet(SurfacesFacet.class);
```
We will iterate over the locations where both facets are defined. Which of them is defined over a larger area may depend on other parts of the world generator, so we iterate over the locations in one, then check that they're also in the other.
```java
Region3i worldRegion = surfacesFacet.getWorldRegion();
```
That's about all we need to start looping and doing the nice stuff.  
Loop through the surface points within this region.
```java  
for (int wx = worldRegion.minX(); wx <= worldRegion.maxX(); wx++) {
    for (int wz = worldRegion.minZ(); wz <= worldRegion.maxZ(); wz++) {
        for (int wy : surfacesFacet.getWorldColumn(wx, wz)) {
            if (facet.getWorldRegion().encompasses(wx, wy, wz)) {
                // Content here
            }
        }
    }
}
```
Then, inside of the inner for-loop, put this piece of code:
```java         
if (noise.noise(wx, wy, wz) > 0.9999) {
    templeFacet.setWorld(wx, wy, wz, new Temple());
}
```
Finally, outside of the two for loops write 
```java 
region.setRegionFacet(TempleFacet.class, templeFacet); 
```
Don't forget to add this. It takes the seed and generates a new WhiteNoise object with it.
```java
@Override public void setSeed(long seed) {
    noise = new WhiteNoise(seed);
}
```

**The TempleRasterizer**

This is the best part, really. Make a Block field inside of the Rasterizer, and in the Initialize method, set this field equal to something like:
```java
stone = CoreRegistry.get(BlockManager.class).getBlock("CoreAssets:Stone");
```
We will be using this Block to fill our pyramid.
Call the getFacet(); method on the chunkRegion and assign it to a new TempleFacet. Like so:
```java
chunkRegion.getFacet(TempleFacet.class);
```
Loop through the WorldEntries like so
```java
for (Entry<BaseVector3i, Temple> entry : templeFacet.getWorldEntries().entrySet()) {
```
This is the most vital part. If you go check locally on the chunk itself you will get buildings cut in half!

Perform these really basic operations to get the size and such for the pyramid
```java
Vector3i basePosition = new Vector3i(entry.getKey());
int size = TempleRasterizer.getSize();
int min = 0;
int height = (TempleRasterizer.getSize() + 1) / 2;
```
We then can start looping through our locations! This is the algorithm I used to generate the pyramid.

```java   
for (int i = 0; i <= height; i++) {
    for (int x = min; x <= size; x++) {
        for (int z = min; z <= size; z++) {
            Vector3i chunkBlockPosition = new Vector3i(x, i, z).add(basePosition);
            if (chunk.getRegion().encompasses(chunkBlockPosition) && !region3i1.encompasses(chunkBlockPosition) &&     !region3i2.encompasses(chunkBlockPosition))
                chunk.setBlock(ChunkMath.calcBlockPos(chunkBlockPosition), stone);

        }
    }
    min++;
    size--;
}
```

//TODO: add repo location.