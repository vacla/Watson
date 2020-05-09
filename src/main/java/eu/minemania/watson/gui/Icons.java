package eu.minemania.watson.gui;

import eu.minemania.watson.Reference;
import fi.dy.masa.malilib.gui.interfaces.IGuiIcon;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.util.Identifier;

public enum Icons implements IGuiIcon
{
    FILE_ICON_SEARCH        (0,  14, 12, 12),
    ARROW_UP                (42,   0, 15, 15),
    ARROW_DOWN              (42,  15, 15, 15);

    public static final Identifier TEXTURE = new Identifier(Reference.MOD_ID, "textures/gui/gui_widgets.png");

    private final int u;
    private final int v;
    private final int w;
    private final int h;

    private Icons(int u, int v, int w, int h)
    {
        this.u = u;
        this.v = v;
        this.w = w;
        this.h = h;
    }

    @Override
    public int getWidth()
    {
        return this.w;
    }

    @Override
    public int getHeight()
    {
        return this.h;
    }

    @Override
    public int getU()
    {
        return this.u;
    }

    @Override
    public int getV()
    {
        return this.v;
    }

    @Override
    public void renderAt(int x, int y, float zLevel, boolean enabled, boolean selected)
    {
        RenderUtils.drawTexturedRect(x, y, this.u, this.v, this.w, this.h, zLevel);
    }

    @Override
    public Identifier getTexture()
    {
        return TEXTURE;
    }
}
