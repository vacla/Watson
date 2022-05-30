package eu.minemania.watson.render;

import com.mojang.blaze3d.systems.RenderSystem;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.selection.EditSelection;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class WatsonRenderer
{
    private static final WatsonRenderer INSTANCE = new WatsonRenderer();

    private MinecraftClient mc;

    public static WatsonRenderer getInstance()
    {
        return INSTANCE;
    }

    public void piecewiseRenderEntities(MinecraftClient mc)
    {
        if (this.mc == null)
        {
            this.mc = mc;
        }
        if (Configs.Generic.DISPLAYED.getBooleanValue() && this.mc.getCameraEntity() != null && Configs.Outlines.OUTLINE_SHOWN.getBooleanValue())
        {
            this.mc.getProfiler().push("watson_entities");
            float fogStart = RenderSystem.getShaderFogStart();
            BackgroundRenderer.clearFog();
            EditSelection selection = DataManager.getEditSelection();
            BlockEditSet edits = selection.getBlockEditSet();
            MatrixStack matrixStack = RenderSystem.getModelViewStack();
            matrixStack.push();
            RenderSystem.disableCull();

            RenderUtils.setupBlend();

            RenderSystem.disableTexture();
            RenderUtils.color(1f, 1f, 1f, 1f);
            RenderSystem.depthMask(false);

            RenderSystem.disableDepthTest();

            Vec3d cameraPos = this.mc.gameRenderer.getCamera().getPos();

            matrixStack.translate(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ());
            RenderSystem.applyModelViewMatrix();
            edits.drawOutlines(matrixStack);
            edits.drawVectors();
            selection.drawSelection();

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();

            RenderSystem.disableBlend();

            RenderSystem.enableCull();

            matrixStack.pop();
            RenderSystem.applyModelViewMatrix();
            RenderSystem.setShaderFogStart(fogStart);
            this.mc.getProfiler().pop();
        }
    }
}