package eu.minemania.watson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.minemania.watson.render.WatsonRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.client.render.WorldRenderer;

@Mixin(WorldRenderer.class)
public abstract class MixinWorldRenderer
{
    @Inject(method = "renderEntities", at = @At("TAIL"))
    private void onPostRenderEntities(Camera camera, VisibleRegion visibleRegion, float partialTicks, CallbackInfo ci)
    {
        WatsonRenderer.getInstance().piecewiseRenderEntities(visibleRegion, partialTicks);
    }
}