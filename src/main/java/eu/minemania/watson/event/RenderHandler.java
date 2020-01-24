package eu.minemania.watson.event;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.render.OverlayRenderer;
import eu.minemania.watson.render.WatsonRenderer;
import fi.dy.masa.malilib.interfaces.IRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

public class RenderHandler implements IRenderer {
	private static final RenderHandler INSTANCE = new RenderHandler();

	public static RenderHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public void onRenderWorldLast(float partialTicks, MatrixStack matrixStack) {
		/*MinecraftClient mc = MinecraftClient.getInstance();

		if (Configs.Generic.ENABLED.getBooleanValue() && mc.world != null && mc.player != null) {
			OutlineVertexConsumerProvider vertexProvider = mc.getBufferBuilders().getOutlineVertexConsumers();
			OverlayRenderer.renderOverlays(mc, partialTicks);
			WatsonRenderer.getInstance().piecewiseRenderEntities(mc, matrixStack, (VertexConsumerProvider)vertexProvider);
		}*/
	}
}
