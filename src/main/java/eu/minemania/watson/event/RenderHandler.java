package eu.minemania.watson.event;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.render.OverlayRenderer;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.minecraft.client.Minecraft;

public class RenderHandler implements IRenderer {
	private static final RenderHandler INSTANCE = new RenderHandler();

	public static RenderHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public void onRenderWorldLast(float partialTicks) {
		Minecraft mc = Minecraft.getInstance();

		if (Configs.Generic.ENABLED.getBooleanValue() && mc.world != null && mc.player != null) {
			OverlayRenderer.renderOverlays(mc, partialTicks);
		}
	}
}
