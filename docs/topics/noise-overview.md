# Overview

Noise generators are similar to random number generators, but provide a deterministic value per coordinate in space. 

The `PerlinNoise` and `SimplexNoise` classes assign random gradient in a regular grid (Perlin uses squares/cubes, Simplex uses triangles/tetrahedrons) and interpolate in between. Simplex is a bit faster than Perlin, in particular for higher dimensions at comparable noise quality. Noise is isotropic (looks the same independent from direction or position).

The `BrownianNoise` class integrates values from other noise implementations. This allows for adjustment of noise frequencies. For example, different layers of Perlin noise can be put on top of each other at different spatial scales and at different amplitudes. This gives the prominent Perlin noise textures.

The `FastNoise` class is a bit different as it works on discrete numbers. This is good enough for per-block noise values. It is about 2x faster than SimplexNoise and 5x faster than PerlinNoise. Noise values repeat after 256, i.e. noise(256) is equal to noise(0).

**Rule of thumb:** Use `SimplexNoise` whenever possible. Noise that is required per block can also be computed using `FastNoise`.

<fig src="_media/img/noise-overview.png" alt="overview of noise implementations">An overview over different noise implementations.</fig>

*Originally added at https://github.com/MovingBlocks/Terasology/wiki/Randomness-and-Noise*

For terrain generation, the brighter the area, the higher the elevation. The brighter, the higher the random numbers at that area.

## How does the noise work?

In procedural generation, noise is important to generate a terrain. In this tutorial, we use `SimplexNoise` to generate a simple terrain with some rolling hills. `SimplexNoise` works similar to `PerlinNoise` but with faster performance. Below is a graph which represents the random values generated using Simplex Noise. We use these random values to further building up world and terrain.

<fig src="_media/img/simplex-noise-graph.png" alt="Perlin Noise Graph">A Perlin noise graph.</fig>

`SimplexNoise` works by generating a random number between -1 and 1 which is related to the previous and next random numbers. As you can see the line is smooth, that means the differences between each random value are not much. This can help to create a smoother and more organic terrain.

For even more realistic terrain, we can overlay multiple noise maps over each other using `BrownianNoise`. We can adjust noise frequencies, scales and amplitudes of each different overlaying noise map. Later on, we will use this technique to create mountains.

You may think that the noise behaves like a wave with a bunch of random numbers. It has frequency, amplitude and wavelength. Here are some nice terms to know:

**Frequency** - the number of cycles per unit length of a noise function outputs.

**Amplitude** - the maximum output values of the noise function. The point at maximum is called "crest".

**Wavelength** - the distance between two consecutive crests or troughs of a wave

**Octave** - number of noise functions added together to form one function. This is used in `BrownianNoise`. Usually, each octave apart has double the frequency.

It's important to note that because `BrownianNoise` with more than one octave adds multiple layers of the underlying noise function together, its value isn't always in between -1 and 1.
