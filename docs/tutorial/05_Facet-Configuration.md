# Facet Configuration

You can allow your FacetProvider to be configured by implementing ConfigurableFacetProvider and providing a custom configuration class that implements Component.  The user interface will automatically detect this and provide the user with configuration options based on your custom configuration classes.

Let's allow users to configure the height of the mountains in the MountainsProvider. First let's create an inner class called MountainsConfiguration inside our MountainsProvider class and make it implement Component.

```java
private static class MountainsConfiguration implements Component {
    @Range(min = 200, max = 500f, increment = 20f, precision = 1, description = "Mountain Height")
    private float mountainHeight = 400;
}
```

Notice the @Range annotation. This tells the UI what components to render for the user. There are more annotations available like @TextField, @Checkbox and @OneOf but @Range is generally the one you will use for your world gen configurations. If you want to know more about those components you can check out the classes in org.terasology.rendering.nui.

Next, we need to make MountainsProvider implement ConfigurableFacetProvider instead of FacetProvider. This lets the UI automatically discover the configuration options. Here are the methods in ConfigurableFacetProvider that you will have to now implement:

```java
public interface ConfigurableFacetProvider extends FacetProvider {
    String getConfigurationName();

    Component getConfiguration();

    void setConfiguration(Component configuration);
}
```

The UI will create a new collapsible section of information for your FacetProvider and the name of that section will be the result of getConfigurationName(). So let's name this one "Mountains". The getConfiguration and setConfiguration gives the UI access to your MountainsConfiguration object. Let's create an instance variable at the top of our class and instantiate it. Then we can fill in the other two methods. Below is the resulting class.

```java
@Updates(@Facet(ElevationFacet.class))
public class MountainsProvider implements ConfigurableFacetProvider {

    private Noise mountainNoise;

    //Be sure to initialize this!
    private MountainsConfiguration configuration = new MountainsConfiguration();

    @Override
    public void setSeed(long seed) {
        mountainNoise = new SubSampledNoise(
            new BrownianNoise(new PerlinNoise(seed + 2), 8), new Vector2f(0.001f, 0.001f), 1);
    }

    @Override
    public void process(GeneratingRegion region) {
        ElevationFacet facet = region.getRegionFacet(ElevationFacet.class);
        float mountainHeight = 400;
        // loop through every position on our 2d array
        Rect2i processRegion = facet.getWorldRegion();
        for (BaseVector2i position : processRegion.contents()) {
            // scale our max mountain height to noise (between -1 and 1)
            float additiveMountainHeight = mountainNoise.noise(position.x(), position.y()) * mountainHeight;
            // dont bother subtracting mountain height,  that will allow unaffected regions
            additiveMountainHeight = TeraMath.clamp(additiveMountainHeight, 0, mountainHeight);

            facet.setWorld(position, facet.getWorld(position) + additiveMountainHeight);
        }
    }

    @Override
    public String getConfigurationName()
    {
        return "Mountains";
    }

    @Override
    public Component getConfiguration()
    {
        return configuration;
    }

    @Override
    public void setConfiguration(Component configuration)
    {
        this.configuration = (MountainsConfiguration)configuration;
    }

    private static class MountainsConfiguration implements Component
    {
        @Range(min = 200, max = 500f, increment = 20f, precision = 1, description = "Mountain Height")
        private float mountainHeight = 400;
    }
}
```

The above code has one mistake in it. Can you spot it? We aren't using our configurable mountain height in the process() method. We need to change

```java
float mountainHeight = 400;
```

to

```java
float mountainHeight = configuration.mountainHeight;
```

Run the code and you should see your options in the "Details" screen as in the screenshots below.

<fig src="/_media/img/facet-configuration-1.png" alt="Navigate to advanced game setup screen.">Navigate to the advanced game setup screen by clicking "Details".</fig>

<fig src="/_media/img/facet-configuration-2.png" alt="Slider to adjust mountain height.">A slider to adjust the mountain height</fig>

> [!NOTE]
> You can have multiple `@Ranges` in your configuration class. Each one will show up under your configuration section (Mountains in this case).