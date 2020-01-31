package eu.minemania.watson.event;

import fi.dy.masa.malilib.interfaces.IRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class RenderHandler implements IRenderer {
	private static final RenderHandler INSTANCE = new RenderHandler();

	public static RenderHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public void onRenderWorldLast(float partialTicks, MatrixStack matrixStack) {
	}
}
