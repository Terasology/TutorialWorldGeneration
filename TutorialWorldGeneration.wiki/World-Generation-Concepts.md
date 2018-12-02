# Multi-Faceted World

The world gen code works from the idea that a World is multi-faceted. The word facet means the following according to google: "one side of something many-sided, especially of a cut gem". So a facet, as it relates to world generation, represents some data set. That data may represent surface height, density, temperature, biome or anything else you can think of. A FacetProvider, which you will implement in the tutorial, is the class you will implement if you want to "provide" facet data to the world. There are several FacetProviders in the core module that you can check out. Here are a few:
* BiomeProvider
* SeaLevelProvider
* PerlinHillsAndMountainsProvider
* PerlinHumidityProvider
* PerlinOceanProvider
* SurfaceToDensityProvider

Data by itself is not useful until it is interpreted. That's where the WorldRasterizer comes into play.

## Rasterizing(interpreting) the facet data
The FacetProvider stuffs it's data into a class called a GeneratingRegion which keeps up with all the facet data and makes it accessible to other FacetProviders but also to WorldRasterizers. The WorldRasterizer interprets the facet data and places blocks into the world based on that data. 

## Borders
TODO

##World Builder and the plugin system
TODO