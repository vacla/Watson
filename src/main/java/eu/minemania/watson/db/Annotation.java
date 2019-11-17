package eu.minemania.watson.db;

import java.util.Arrays;

import eu.minemania.watson.config.Configs;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class Annotation {
	protected String _text;
	protected int _x;
	protected int _y;
	protected int _z;
	
	public Annotation(int x, int y, int z, String text) {
		_text = text;
		_x = x;
		_y = y;
		_z = z;
	}
	
	public String getText() {
		return _text;
	}
	
	public int getX() {
		return _x;
	}
	
	public int getY() {
		return _y;
	}
	
	public int getZ() {
		return _z;
	}
	
	public void draw() {
		drawBillboard(getX(), getY(), getZ(), 0.02, getText());
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
