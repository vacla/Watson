package eu.minemania.watson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.render.OverlayRenderer;
import eu.minemania.watson.render.WatsonRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OutlineVertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer {
	@Inject(method = "render", at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = "ldc=weather"))
	private void render(MatrixStack matrices, float tickDelta, long limitTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo ci) {
		MinecraftClient mc = MinecraftClient.getInstance();

		if (Configs.Generic.ENABLED.getBooleanValue() && mc.world != null && mc.player != null) {
			OutlineVertexConsumerProvider vertexProvider = mc.getBufferBuilders().getOutlineVertexConsumers();
			OverlayRenderer.renderOverlays(mc, tickDelta);
			WatsonRenderer.getInstance().piecewiseRenderEntities(mc, matrices, vertexProvider);
		}
	}
}
