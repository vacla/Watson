package eu.minemania.watson.gui;

import eu.minemania.watson.Reference;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.util.Identifier;

public enum ButtonIcons implements IGuiIcon {
	CONFIGURATION (0, 0, 14, 14);

	public static final Identifier TEXTURE = new Identifier(Reference.MOD_ID, "textures/gui/gui_widgets.png");

	private final int u;
	private final int v;
	private final int w;
	private final int h;

	private ButtonIcons(int u, int v, int w, int h) {
		this.u = u;
		this.v = v;
		this.w = w;
		this.h = h;
	}

	@Override
	public int getWidth() {
		return this.w;
	}

	@Override
	public int getHeight() {
		return this.h;
	}

	@Override
	public int getU() {
		return this.u;
	}

	@Override
	public int getV() {
		return this.v;
	}

	@Override
	public void renderAt(int x, int y, float zLevel, boolean enabled, boolean selected) {
		int u = this.u;

		if(enabled) {
			u += this.w;
		}

		if(selected) {
			u += this.w;
		}

		RenderUtils.drawTexturedRect(x, y, u, this.v, this.w, this.h, zLevel);
	}

	@Override
	public Identifier getTexture() {
		return TEXTURE;
	}
}
