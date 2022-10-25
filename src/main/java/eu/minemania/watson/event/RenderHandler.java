package eu.minemania.watson.event;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.render.OverlayRenderer;
import eu.minemania.watson.render.WatsonRenderer;
import malilib.event.PostWorldRenderer;
import malilib.util.game.wrap.GameUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

public class RenderHandler implements PostWorldRenderer
{
    private static final RenderHandler INSTANCE = new RenderHandler();

    public static RenderHandler getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void onPostWorldRender(MatrixStack matrices, Matrix4f projMatrix, float tickDelta)
    {
        MinecraftClient mc = GameUtils.getClient();
        if (Configs.Generic.ENABLED.getBooleanValue() && mc.world != null && mc.player != null)
        {
            OverlayRenderer.renderOverlays(mc);
            WatsonRenderer.getInstance().piecewiseRenderEntities(mc);
        }
    }
}
