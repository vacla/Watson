package eu.minemania.watson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.minemania.watson.render.WatsonRenderer;
import net.minecraft.client.renderer.GameRenderer;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
	/*@Inject(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;updateChunks(J)V", shift = Shift.AFTER))
    private void setupAndUpdate(float partialTicks, long finishTimeNano, CallbackInfo ci) {
        WatsonRenderer.getInstance().piecewisePrepareAndUpdate(partialTicks);
    }*/
	
	@Inject(method = "updateCameraAndRender(FJ)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/WorldRenderer;renderEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V"))
	private void renderEntities(float partialTicks, long finishTimeNano, CallbackInfo ci) {
		WatsonRenderer.getInstance().piecewiseRenderEntities(partialTicks);
	}
}
