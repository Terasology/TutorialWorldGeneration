To avoid making the same mistakes as I did and to save you your precious time I collected here my most annoying errors and their fixes during my learning process. I hope you can find it useful!

**Watch out for concurrency!**  
Never declare your region-specific variables outside the process/generateChunk block in your Providers/Rasterizers. 
As you may have heard, world generation is a multi-threaded process which means you have to take care of making new variables for every thread so that they won't interfere by trying to read/write to the same variable as the values will become messed up. This doesn't matter of course for constant values.

**The thing about borders.**  
I found borders a bit hard to grasp at start even with the tutorial so here are some further notes:
When you request a border area, the respective facet data gets extended by the requested value. As you will have two values for the same position for the border area, one from the actual facet in that region and one from the extended border area from an adjacent region, you'll have to make sure that they are the same! That also implies that it is always the safest to iterate the position over the facet region. 
