# Randomness

Random numbers play a major role in procedural terrain generation and many other dynamically created content in the game. There are different random number generators and different types of noise. Random number is also related to [Noise](noise).

## Random Numbers

There are two implementations of the `Random` interface: `FastRandom` and `MersenneRandom`. As you might expect, the first one is rather simplistic, which makes it very fast. In some cases, the quality is not sufficient though and we recommend the implementation that is based on the Mersenne prime number twister. It is very close to *real* noise, but more expensive to compute.

**Rule of thumb:** Use `MersenneRandom` when looking at very small value ranges (e.g. floats between 0 and 0.000001 or boolean values), FastRandom otherwise.