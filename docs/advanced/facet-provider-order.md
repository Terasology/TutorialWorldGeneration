# Ordering Facet Providers

In [Plugins](advanced/plugins.md) we created a `LakesProvider`, which updates elevation in a similar way to the `MountainsProvider` from [Facet Modification](tutorial/04_Facet-Modification.md).
In this case, the order in which these two providers run doesn't matter, because they're just adding and subtracting the height; but in many cases it's important that one provider update a facet before another one.

Let's create a new facet provider plugin which updates the elevation to create mesas, mountains with flat tops and steep sides:

```java
@RegisterPlugin
@Requires(@Facet(SeaLevelFacet.class))
@Updates(@Facet(ElevationFacet.class))
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
```

This seems to work:

<fig src="_media/img/Mesas1.png" alt="Correct mesa">Note how the mesa starts above the beach.</fig>

But on closer inspection, there are problems: mesas are spawning in the water!

<fig src="_media/img/Mesas2.png" alt="Incorrect mesa">Here the mesa starts in the water.</fig>

How does this happen? Well, imagine this scenario:
1. The `SurfaceProvider` generates a surface that's above water.
2. The `MesaProvider` sees that it's above water and generates a mesa.
3. The `LakesProvider` pushes the area down, so that the base of the mesa is now in the water.

If we want to avoid this, we need to make sure that the `MesaProvider` always runs after the `LakesProvider`.
The best way to do that is to add a priority to the `@Updates` annotation. The default is `PRIORITY_NORMAL`,
so `PRIORITY_LOW` will cause the `MesaProvider` to run after other providers:

```java
@RegisterPlugin
@Requires(@Facet(SeaLevelFacet.class))
@Updates(value = @Facet(ElevationFacet.class), priority = UpdatePriority.PRIORITY_LOW)
public class MesaProvider implements FacetProviderPlugin {
    /* ... */
}
```