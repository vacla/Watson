package eu.minemania.watson.event;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.render.OverlayRenderer;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.minecraft.client.MinecraftClient;

public class RenderHandler implements IRenderer {
	private static final RenderHandler INSTANCE = new RenderHandler();

	public static RenderHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public void onRenderWorldLast(float partialTicks) {
		MinecraftClient mc = MinecraftClient.getInstance();

		if (Configs.Generic.ENABLED.getBooleanValue() && mc.world != null && mc.player != null) {
			OverlayRenderer.renderOverlays(mc, partialTicks);
		}
	}
}
