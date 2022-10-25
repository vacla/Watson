package eu.minemania.watson.gui;

import eu.minemania.watson.Reference;
import malilib.gui.icon.BaseMultiIcon;
import malilib.util.StringUtils;
import malilib.util.data.Identifier;

public class Icons extends BaseMultiIcon
{
    public static final Identifier TEXTURE = StringUtils.identifier(Reference.MOD_ID, "textures/gui/gui_widgets.png");
    public static final Icons CONFIGURATION = new Icons(0, 0, 14, 14);
    public static final Icons ARROW_DOWN = new Icons(42, 15, 15, 15);
    public static final Icons ARROW_UP = new Icons(42, 0, 15, 15);
    public static final Icons CHECKBOX_SELECTED = new Icons(87, 11, 11, 11);
    public static final Icons CHECKBOX_UNSELECTED = new Icons(87, 0, 11, 11);
    public static final Icons FILE_ICON_SEARCH = new Icons(0, 14, 12, 12);
    public static final Icons INFO_11 = new Icons(12, 14, 11, 11, 0, 0);

    private Icons(int u, int v, int w, int h)
    {
        super(u, v, w, h, TEXTURE);
    }

    private Icons(int u, int v, int w, int h, int hoverOffU, int hoverOffV)
    {
        super(u, v, w, h, hoverOffU, hoverOffV, TEXTURE);
    }
}
