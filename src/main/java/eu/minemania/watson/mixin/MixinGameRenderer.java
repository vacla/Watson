package eu.minemania.watson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.minemania.watson.render.WatsonRenderer;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
	@Inject(method = "renderCenter(FJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/WorldRenderer;renderEntities(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/VisibleRegion;F)V"))
	private void renderEntities(float partialTicks, long finishTimeNano, CallbackInfo ci) {
		WatsonRenderer.getInstance().piecewiseRenderEntities(partialTicks);
	}
}
