package eu.minemania.watson.event;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.render.OverlayRenderer;
import eu.minemania.watson.render.WatsonRenderer;
import fi.dy.masa.malilib.event.PostWorldRenderer;
import net.minecraft.client.MinecraftClient;

public class RenderHandler implements PostWorldRenderer
{
    private static final RenderHandler INSTANCE = new RenderHandler();

    public static RenderHandler getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void onPostWorldRender(MinecraftClient mc, float partialTicks)
    {
        if (Configs.Generic.ENABLED.getBooleanValue() && mc.world != null && mc.player != null)
        {
            OverlayRenderer.renderOverlays(mc);
            WatsonRenderer.getInstance().piecewiseRenderEntities(mc);
        }
    }
}
