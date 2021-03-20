---
title: Biomes
---

Biomes in Terasology represent region-specific world metadata of sorts. They
can be used in runtime as means of scheduling music to play/mobs to spawn/
selecting color of unicorns/whatever to fit the surroundings, and thus 
improving player experience (you don't want whales to spawn in every pond).
Biomes can also be used as sorts of additional data used when generating
worlds, for instance to help you decide which kind of flowers to spawn where.
Please note, that unlike in minecraft, biomes can be set on per-block basis,
meaning you can have different biomes at different heights within the same 
`xz` coordinate. Also note, that unlike Terraria, biomes are set during world 
generation and not computed on the run from surrounding blocks, meaning you 
have to update block's biome manually if you ever find it necessary to. In 
this tutorial, we will set up our world with three different biomes: `Land`, 
`Water`, and `Sky` (the latest starting at 10 blocks above terrain and 
spanning the whole rest of the world).

Firstly, since biomes are no longer part of the base engine, you need to
change the `module.txt` of your module to depend on the `BiomesAPI` module
(if you are depending on `CoreWorlds`, you are already implicitly depending on
`BiomesAPI`, but it is a good idea to denote this dependency explicitly).

Then, to get started, first create the biomes themselves. Biomes are easiest
created as different values of enum, so let's do just that!
```java
public enum TutorialBiome implements Biome {
    LAND, WATER, SKY;
    
    @Override
    public String getId() {
        return null;
    }
    
    @Override
    public String getName() {
        return null;
    }
}
```
You already got two methods required by the `Biome` interface, what should
those return? By checking their documentation in your IDE you can see that
`getId()` should return the gestalt-style unique ID of your biome, including
the module-name, and `getName()` ought return human-friendly name of the
biome. The simplest and proven way of implementing those methods is thus:
```java
public enum TutorialBiome implements Biome {
    LAND("Land"), WATER("Water"), SKY("Sky");

    TutorialBiome(String name) {
        this.name = name;
    }

    private final String name;

    @Override
    public String getId() {
        return "TutorialWorldGeneration:" + this.toString();
    }

    @Override
    public String getName() {
        return name;
    }
}
```
Looking at the Biome interface, you can also see a third method, `biomeHash`,
with a default implementation. A good practice is however to override this
method too to hardcoded values, so that leaves us with:
```java
public enum TutorialBiome implements Biome {
    LAND("Land", (short) 1), WATER("Water", (short) 2), SKY("Sky", (short) 3);

    TutorialBiome(String name, short biomeHash) {
        this.name = name;
        this.biomeHash = biomeHash;
    }

    private final String name;
    private final short biomeHash;

    @Override
    public String getId() {
        return "TutorialWorldGeneration:" + this.toString();
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public short biomeHash() {
        return biomeHash;
    }
}
```
> Note: The biome hashes have to be explicitly casted to `short` due to how
Java handles numerical literals

Now that you have created your biomes, you also need to register them with the
BiomesAPI module. To do that, create a plain old ComponentSystem, and in its
`preBegin` method, register each of the biomes as following
```java
@RegisterSystem
public class TutorialBiomes extends BaseComponentSystem {
    @In
    BiomeRegistry registry;
    
    @Override
    public void preBegin() {
        for (TutorialBiome biome : TutorialBiome.values()) {
            registry.registerBiome(biome);
        }
    }
}
```
Now that you have the biomes all set up and ready to use, you can finally get
to the last step, populating the world with the biomes. You do this as you
would populate the world with blocks, by creating a new rasterizer. For the
behaviour as used by this tutorial, the rasterizer can look like this:
```java
@Requires({@Facet(SeaLevelFacet.class), @Facet(ElevationFacet.class)})
public class BiomeRasterizer implements WorldRasterizer {
    private BiomeRegistry biomeRegistry;

    @Override
    public void initialize() {
        biomeRegistry = CoreRegistry.get(BiomeRegistry.class);
    }
    
    @Override
    public void generateChunk(CoreChunk chunk, Region chunkRegion) {
        ElevationFacet elevationFacet = chunkRegion.getFacet(ElevationFacet.class);
        SeaLevelFacet seaLevelFacet = chunkRegion.getFacet(SeaLevelFacet.class);
        for (Vector3i position : chunkRegion.getRegion()) {
            if (position.y > Math.max(seaLevelFacet.getSeaLevel(), elevationFacet.getWorld(position.x, position.z)) + 10) {
                biomeRegistry.setBiome(TutorialBiome.SKY, position);
            } else if (elevationFacet.getWorld(position.x, position.z) + 1 > seaLevelFacet.getSeaLevel()) {
                biomeRegistry.setBiome(TutorialBiome.LAND, position);
            }
            else {
                biomeRegistry.setBiome(TutorialBiome.WATER, position);
            }
        }
    }
}
```
You can of course also create a 2d/3d `Facet`, its respective `FacetProvider`
and do lots of more interesting things with designating biomes, but after
going through this tutorial, you should already have high enough grasp of the
principles used when dealing with biomes to figure that out by yourself.
