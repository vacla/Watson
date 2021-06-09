package eu.minemania.watson.render;

import com.mojang.blaze3d.platform.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.mojang.blaze3d.systems.RenderSystem;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.selection.EditSelection;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.shader.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

public class WatsonRenderer
{
    private static final WatsonRenderer INSTANCE = new WatsonRenderer();

    private static final ShaderProgram SHADER_ALPHA = new ShaderProgram("watson", null, "shaders/alpha.frag");
    public static final ShaderProgram SHADER_LINESTIPPLE = new ShaderProgram("watson", null, "shaders/linestipple.frag");

    private MinecraftClient mc;

    static
    {
        int program = SHADER_ALPHA.getProgram();
        int oldProgram = GlStateManager._getInteger(GL20.GL_CURRENT_PROGRAM);
        GL20.glUseProgram(program);
        GL20.glUniform1i(GL20.glGetUniformLocation(program, "texture"), 0);
        GL20.glUseProgram(oldProgram);
    }

    public static WatsonRenderer getInstance()
    {
        return INSTANCE;
    }

    public void piecewiseRenderEntities(MinecraftClient mc, MatrixStack matrices, VertexConsumerProvider vertexConsumers)
    {
        if (this.mc == null)
        {
            this.mc = mc;
        }
        if (Configs.Generic.DISPLAYED.getBooleanValue() && this.mc.getCameraEntity() != null && Configs.Outlines.OUTLINE_SHOWN.getBooleanValue())
        {
            this.mc.getProfiler().push("watson_entities");
            EditSelection selection = DataManager.getEditSelection();
            BlockEditSet edits = selection.getBlockEditSet();
            matrices.push();
            RenderUtils.disableDiffuseLighting();
            RenderSystem.disableCull();

            RenderUtils.setupBlend();

            RenderSystem.disableTexture();
            RenderUtils.color(1f, 1f, 1f, 1f);
            //RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, 240.0F, 240.0F);
            RenderSystem.depthMask(false);
            //boolean foggy = GL11.glIsEnabled(GL11.GL_FOG);
            //RenderSystem.disableFog();
            RenderSystem.disableDepthTest();


            Vec3d cameraPos = this.mc.gameRenderer.getCamera().getPos();
            //matrices.peek().getModel().multiply(RenderSystem.getProjectionMatrix());
            //matrices.translate(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ());
            //RenderSystem.applyModelViewMatrix();
            edits.drawOutlines();
            //edits.drawVectors();
            //selection.drawSelection(matrices);

            /*if (foggy)
            {
                RenderSystem.enableFog();
            }*/
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableTexture();

            RenderSystem.disableBlend();

            RenderSystem.enableCull();

            RenderUtils.enableDiffuseLightingForLevel(matrices);

            matrices.pop();
            ///RenderSystem.applyModelViewMatrix();
            this.mc.getProfiler().pop();
        }
    }
}