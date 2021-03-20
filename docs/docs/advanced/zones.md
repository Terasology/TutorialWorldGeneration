---
title: Zones
---

:::caution

Zones are a feature for v2, so the `v2.0.0` branch of the engine must be used to access these features!

:::

### What are zones

Zones allow the world generator to be split up into separate areas for the purposes of generation and previews.

A zone is specified by a `ZoneRegionFunction`, which returns true for every block in the zone, and false for every block that isn't in the zone. This can be created directly from the `ZoneRegionFunction` (often as a lambda), or a subclass, such as `LayeredZoneRegionFunction`, can be used (layered zones will be described later).

Zones get added to the WorldGenerator with the addZone method.

Example zone creation (assuming a pre-existing `WorldGenerator` called `worldGenerator`):

This creates a zone with the name `Zone1`, and only contains blocks with a y-coordinate of 10 or higher.

```java
worldGenerator.addZone(new Zone("Zone1", (x, y, z, region) -> y >= 10));
```

This zone will only contain blocks that are above the surface of the world.

```java
worldGenerator.addZone(new Zone("Zone2", (x, y, z, region) ->
    y > TeraMath.floorToInt(region.getFacet(ElevationFacet.class).getWorld(x, z))));
```

### Adding rasterizers and providers

The zones given above contain blocks, but they do not do anything. To make the zones have an effect on the world, providers and rasterizers must be added (in the same way as for normal world generators).

Examples (assuming a pre-existing `Zone` called `zone`)

This zone will create trees, but the trees will only be present in areas contained by the zone.

```java
worldGenerator.addZone(zone.addProvider(new DefaultTreeProvider())
    .addRasterizer(new TreeRasterizer()))
```

This zone will set all blocks underneath the surface to stone.

```java
worldGenerator.addZone(new Zone("UndergroundStone", (x, y, z, region) ->
    y < TeraMath.floorToInt(region.getFacet(ElevationFacet.class).getWorld(x, z)))
    .addRasterizer(new SingleBlockRasterizer("CoreAssets:Stone")));
```

### Preview layers

Zones can also have preview layers added to them, and the top level zones (those added directly to the `WorldBuidler`) will show up on the world preview screen as a drop-down list to choose which to preview.

The preview layers are the same `FacetLayer`s that the world preview uses, and they are added to a zone with the `addPreviewLayer` method.

```java
zone.addPreviewLayer(new SurfaceHeightFacetLayer())
```

### Nested Zones

Zones can be nested, by adding zones to existing zones (with the `addZone` method). The child zones will only affect areas also affected by their parent zone, so this can be used to further sub-divide zones.

```java
zone.addZone(new Zone("Nested zone", (x, y, z, region) -> x > z)
    .addRasterizer(new SingleBlockRasterizer("CoreAssets:Sand")))
```

### Layered Zones

Instead of manually creating a `ZoneRegionFunction`, the `LayeredZoneRegionFunction` can be used for specific types of zones. These zones are layered on top of each other and don't overlap. They can be used, for example, to separate the underground, surface, and sky into separate regions for generation.

The `LayeredZoneRegionFunction` needs some properties to determine its shape and position. The first property is a `LayerThickness`, which has a function from the `x, y` position of the layer to its width at that point.

If a layer just needs to be a constant thickness, the `ConstantLayerThickness` can be used; the `MinMaxLayerThickness` can be used to have the thickness be in a predefined range; or a custom thickness function can be made from either `LayerThickness` or `SeededNoiseLayerThickness`.

The layer also needs an ordering value, to determine where it should be in the world. This is just an integer, but there are some predefined constants in `LayeredZoneRegionFunction.LayeredZoneOrdering` which can be used.

```java
worldGenerator.addZone(new Zone("Ground layer", new LayeredZoneRegionFunction(new ConstantLayerThickness(5), LayeredZoneRegionFunction.LayeredZoneOrdering.GROUND)))
```

### Zone Plugins

Zones can be used as world plugins, just like facet providers and rasterizers. The class needs to extend `ZonePlugin`, and have the `@RegisterPlugin` annotation.

```java
@RegisterPlugin
public class ExampleZonePlugin extends ZonePlugin {

    public ExampleZonePlugin() {
        super("Plugin zone", new LayeredZoneRegionFunction(new ConstantLayerThickness(1), DEEP_UNDERGROUND));

        // Inferno
        addProvider(new SurfaceProvider());
        addRasterizer(new SingleBlockRasterizer("CoreAssets:Glass"));
    }
}
```