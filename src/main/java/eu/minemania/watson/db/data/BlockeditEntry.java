package eu.minemania.watson.db.data;

import eu.minemania.watson.db.BlockEdit;
import fi.dy.masa.malilib.gui.button.ButtonBase;

public class BlockeditEntry
{
    private final BlockEdit edit;
    private ButtonBase button;

    public BlockeditEntry(BlockEdit edit)
    {
        this.edit = edit;
    }

    public BlockEdit getEdit()
    {
        return this.edit;
    }

    public ButtonBase getButton()
    {
        return this.button;
    }

    public void setButton(ButtonBase button)
    {
        this.button = button;
    }
}
