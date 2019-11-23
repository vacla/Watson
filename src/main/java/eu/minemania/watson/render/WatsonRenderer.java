package eu.minemania.watson.render;

import org.lwjgl.opengl.GL20;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEditSet;
import eu.minemania.watson.render.playeredit.WorldRendererPlayeredit;
import eu.minemania.watson.selection.EditSelection;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.render.shader.ShaderProgram;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;

public class WatsonRenderer {
	private static final WatsonRenderer INSTANCE = new WatsonRenderer();
	
	private static final ShaderProgram SHADER_ALPHA = new ShaderProgram("watson", null, "shaders/alpha.frag");
	
	private Minecraft mc;
	private WorldRendererPlayeredit worldRenderer;
	
	static {
		int program = SHADER_ALPHA.getProgram();
		GL20.glUseProgram(program);
		GL20.glUniform1i(GL20.glGetUniformLocation(program, "texture"), 0);
		GL20.glUseProgram(0);
	}
	
	public static WatsonRenderer getInstance() {
		return INSTANCE;
	}
	
	public WorldRendererPlayeredit getWorldRenderer() {
		if(this.worldRenderer == null) {
			this.mc = Minecraft.getInstance();
			this.worldRenderer = new WorldRendererPlayeredit(this.mc);
		}
		
		return this.worldRenderer;
	}
	
	public void loadRenderers() {
        this.getWorldRenderer().loadRenderers();
    }
	
	/*public void piecewisePrepareAndUpdate(float partialTicks) {
		this.renderPiecewise = Configs.Generic.DISPLAYED.getBooleanValue() && this.mc.getRenderViewEntity() != null;
		this.renderPiecewiseBlocks = false;
		if(this.renderPiecewise) {
			this.renderPiecewiseBlocks = Configs.Generic.OUTLINE_SHOWN.getBooleanValue();
			//Entity entity = this.mc.getRenderViewEntity();
			//this.createCamera(entity, partialTicks);
		}
	}*/
	
	public void piecewiseRenderEntities(float partialTicks) {
		if(Configs.Generic.DISPLAYED.getBooleanValue() && this.mc.getRenderViewEntity() != null && Configs.Generic.OUTLINE_SHOWN.getBooleanValue()) {
			this.mc.profiler.startSection("watson_entities");
			GlStateManager.depthMask(true);
			GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.pushMatrix();
			RenderUtils.setupBlend();
			RenderUtils.color(1f, 1f, 1f, 1f);
            OpenGlHelper.glMultiTexCoord2f(OpenGlHelper.GL_TEXTURE1, 240, 240);
            GlStateManager.depthMask(false);
            GlStateManager.disableDepthTest();
			EditSelection selection = DataManager.getEditSelection();
			BlockEditSet edits = selection.getBlockEditSet();
			Entity entity = mc.getRenderViewEntity();
			if(entity == null) {
				entity = mc.player;
			}
			
			double dx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
	        double dy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
	        double dz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
	        GlStateManager.translated(-dx, -dy, -dz);
	        edits.drawOutlines();
	        edits.drawVectors();
			selection.drawSelection(dx, dy, dz);
			GlStateManager.enableDepthTest();
			GlStateManager.depthMask(true);
			GlStateManager.popMatrix();
			GlStateManager.disableBlend();
			GlStateManager.enableTexture2D();
            GlStateManager.enableCull();
            GlStateManager.enableLighting();
			this.mc.profiler.endSection();
		}
	}
}
