package eu.minemania.watson.render;

import java.util.Arrays;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class OverlayRenderer {
	private static long loginTime;
	private static boolean canRender;

	// https://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors
	public static final int[] KELLY_COLORS = {
			0xFFB300,    // Vivid Yellow
			0x803E75,    // Strong Purple
			0xFF6800,    // Vivid Orange
			0xA6BDD7,    // Very Light Blue
			0xC10020,    // Vivid Red
			0xCEA262,    // Grayish Yellow
			0x817066,    // Medium Gray
			// The following don't work well for people with defective color vision
			0x007D34,    // Vivid Green
			0xF6768E,    // Strong Purplish Pink
			0x00538A,    // Strong Blue
			0xFF7A5C,    // Strong Yellowish Pink
			0x53377A,    // Strong Violet
			0xFF8E00,    // Vivid Orange Yellow
			0xB32851,    // Strong Purplish Red
			0xF4C800,    // Vivid Greenish Yellow
			0x7F180D,    // Strong Reddish Brown
			0x93AA00,    // Vivid Yellowish Green
			0x593315,    // Deep Yellowish Brown
			0xF13A13,    // Vivid Reddish Orange
			0x232C16     // Dark Olive Green
	};

	public static void resetRenderTimeout() {
		canRender = false;
		loginTime = System.currentTimeMillis();
	}

	public static void renderOverlays(Minecraft mc, float partialTicks) {
		Entity entity = mc.getRenderViewEntity();

		if (canRender == false) {
			// Don't render before the player has been placed in the actual proper position,
			// otherwise some of the renderers mess up.
			// The magic 8.5, 65, 8.5 comes from the WorldClient constructor
			if (System.currentTimeMillis() - loginTime >= 5000 || entity.posX != 8.5 || entity.posY != 65 || entity.posZ != 8.5) {
				canRender = true;
			} else {
				return;
			}
		}
		double dx = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
		double dy = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
		double dz = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;

		DataManager.getEditSelection().getBlockEditSet().getOreDB().drawDepositLabels(dx, dy, dz);
		DataManager.getEditSelection().getBlockEditSet().drawAnnotations(dx, dy, dz);
	}

	public static void drawBillboard(double x, double y, double z, double scale, String text) {
		final float scaled = MathHelper.clamp((float) scale, 0.01f, 1f);
		Minecraft mc = Minecraft.getInstance();
		Entity entity = mc.getRenderViewEntity();
		if(entity != null) {
			RenderUtils.drawTextPlate(Arrays.asList(text), x, y, z, entity.rotationYaw, entity.rotationPitch, scaled, Configs.Generic.BILLBOARD_FOREGROUND.getIntegerValue(), Configs.Generic.BILLBOARD_BACKGROUND.getIntegerValue(), true);
		}
	}
}
