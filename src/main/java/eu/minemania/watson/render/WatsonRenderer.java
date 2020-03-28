package eu.minemania.watson.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.selection.EditSelection;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.shader.ShaderProgram;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VisibleRegion;
import net.minecraft.util.math.Vec3d;

public class WatsonRenderer
{
    private static final WatsonRenderer INSTANCE = new WatsonRenderer();

    private static final ShaderProgram SHADER_ALPHA = new ShaderProgram("watson", null, "shaders/alpha.frag");

    private MinecraftClient mc;

    static
    {
        int program = SHADER_ALPHA.getProgram();
        GL20.glUseProgram(program);
        GL20.glUniform1i(GL20.glGetUniformLocation(program, "texture"), 0);
        GL20.glUseProgram(0);
    }

    public static WatsonRenderer getInstance()
    {
        return INSTANCE;
    }

    public void piecewiseRenderEntities(VisibleRegion visibleRegion, float partialTicks)
    {
        if(this.mc == null)
        {
            this.mc = MinecraftClient.getInstance();
        }
        if(Configs.Generic.DISPLAYED.getBooleanValue() && this.mc.getCameraEntity() != null && Configs.Generic.OUTLINE_SHOWN.getBooleanValue())
        {
            this.mc.getProfiler().push("watson_entities");
            GlStateManager.pushMatrix();
            GlStateManager.disableLighting();
            GlStateManager.disableCull();
            RenderUtils.setupBlend();
            GlStateManager.disableTexture();
            RenderUtils.color(1f, 1f, 1f, 1f);
            GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, 240.0F, 240.0F);
            GlStateManager.depthMask(false);
            boolean foggy = GL11.glIsEnabled(GL11.GL_FOG);
            GlStateManager.disableFog();
            GlStateManager.disableDepthTest();
            EditSelection selection = DataManager.getEditSelection();
            BlockEditSet edits = selection.getBlockEditSet();

            Vec3d cameraPos = this.mc.gameRenderer.getCamera().getPos();
            GlStateManager.translated(-cameraPos.getX(),-cameraPos.getY(),-cameraPos.getZ());
            edits.drawOutlines();
            edits.drawVectors();
            selection.drawSelection();

            if(foggy)
            {
                GlStateManager.enableFog();
            }
            GlStateManager.enableDepthTest();
            GlStateManager.depthMask(true);
            GlStateManager.enableTexture();
            GlStateManager.disableBlend();
            GlStateManager.enableCull();
            GlStateManager.enableLighting();
            GlStateManager.popMatrix();
            this.mc.getProfiler().pop();
        }
    }
}