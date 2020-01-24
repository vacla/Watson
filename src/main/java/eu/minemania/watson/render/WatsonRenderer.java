package eu.minemania.watson.render;

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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class WatsonRenderer {
	private static final WatsonRenderer INSTANCE = new WatsonRenderer();

	private static final ShaderProgram SHADER_ALPHA = new ShaderProgram("watson", null, "shaders/alpha.frag");

	private MinecraftClient mc;

	static {
		int program = SHADER_ALPHA.getProgram();
		GL20.glUseProgram(program);
		GL20.glUniform1i(GL20.glGetUniformLocation(program, "texture"), 0);
		GL20.glUseProgram(0);
	}

	public static WatsonRenderer getInstance() {
		return INSTANCE;
	}

	public void piecewiseRenderEntities(MinecraftClient mc, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
		if(this.mc == null) {
			this.mc = mc;
		}
		if(Configs.Generic.DISPLAYED.getBooleanValue() && this.mc.getCameraEntity() != null && Configs.Generic.OUTLINE_SHOWN.getBooleanValue()) {
			this.mc.getProfiler().push("watson_entities");
			RenderSystem.pushMatrix();
			RenderSystem.disableLighting();
			RenderSystem.disableCull();
			RenderUtils.setupBlend();
			RenderSystem.disableTexture();
			RenderUtils.color(1f, 1f, 1f, 1f);
			RenderSystem.glMultiTexCoord2f(GL13.GL_TEXTURE1, 240.0F, 240.0F);
			RenderSystem.depthMask(false);
			boolean foggy = GL11.glIsEnabled(GL11.GL_FOG);
			RenderSystem.disableFog();
			RenderSystem.disableDepthTest();
			EditSelection selection = DataManager.getEditSelection();
			BlockEditSet edits = selection.getBlockEditSet();

			Vec3d cameraPos = this.mc.gameRenderer.getCamera().getPos();
		    RenderSystem.translated(-cameraPos.getX(),-cameraPos.getY(),-cameraPos.getZ());
			edits.drawOutlines();
			edits.drawVectors();
			selection.drawSelection();

			if(foggy) {
				RenderSystem.enableFog();
			}
			RenderSystem.enableDepthTest();
			RenderSystem.depthMask(true);
			RenderSystem.enableTexture();
			RenderSystem.disableBlend();
			RenderSystem.enableCull();
			RenderSystem.enableLighting();
			RenderSystem.popMatrix();
			this.mc.getProfiler().pop();
		}
	}
}
