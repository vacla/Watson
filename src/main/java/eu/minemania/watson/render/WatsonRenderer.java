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
import net.minecraft.client.renderer.culling.Frustum;
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
			RenderUtils.setupBlend();
			EditSelection selection = DataManager.getEditSelection();
			BlockEditSet edits = selection.getBlockEditSet();
			Entity entity = mc.getRenderViewEntity();
			if(entity == null) {
				entity = mc.player;
			}
			GlStateManager.pushMatrix();
			GlStateManager.translated(-entity.posX, -entity.posY, -entity.posZ);
			//System.out.println(entity);
			edits.drawOutlines();
			edits.drawVectors();
			selection.drawSelection();
			edits.drawAnnotations();
			edits.getOreDB().drawDepositLabels();
			GlStateManager.popMatrix();
			GlStateManager.disableBlend();
			this.mc.profiler.endSection();
		}
	}
}
