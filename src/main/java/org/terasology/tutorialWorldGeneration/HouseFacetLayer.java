package org.terasology.tutorialWorldGeneration;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

import org.joml.Vector2i;
import org.terasology.engine.world.block.BlockArea;
import org.terasology.engine.world.viewer.layers.AbstractFacetLayer;
import org.joml.Vector3ic;
import org.terasology.engine.world.generation.Region;
import org.terasology.engine.world.viewer.layers.Renders;

@Renders(value = HouseFacet.class, order = 5000)
public class HouseFacetLayer extends AbstractFacetLayer {

    private Color fillColor = new Color(224, 128, 128, 128);
    private Color frameColor = new Color(224, 128, 128, 224);

    @Override
    public void render(BufferedImage img, Region region) {
        HouseFacet houseFacet = region.getFacet(HouseFacet.class);

        Graphics2D g = img.createGraphics();

        int dx = region.getRegion().minX();
        int dy = region.getRegion().minZ();
        g.translate(-dx, -dy);

        for (Entry<Vector3ic, House> entry : houseFacet.getWorldEntries().entrySet()) {
            int extent = entry.getValue().getExtent();

            Vector3ic center = entry.getKey();
            g.setColor(fillColor);
            g.fillRect(center.x() - extent, center.z() - extent, 2 * extent, 2 * extent);
            g.setColor(frameColor);
            g.drawRect(center.x() - extent, center.z() - extent, 2 * extent, 2 * extent);
        }

        g.dispose();
    }

    @Override
    public String getWorldText(Region region, int wx, int wy) {
        HouseFacet houseFacet = region.getFacet(HouseFacet.class);

        for (Entry<Vector3ic, House> entry : houseFacet.getWorldEntries().entrySet()) {
            int extent = entry.getValue().getExtent();

            Vector3ic center = entry.getKey();
            Vector2i min = new Vector2i(center.x() - extent, center.z() - extent);
            Vector2i max = new Vector2i(center.x() + extent, center.z() + extent);
            if (new BlockArea(min, max).contains(wx, wy)) {
                return "House";
            }
        }
        return null;
    }

}
