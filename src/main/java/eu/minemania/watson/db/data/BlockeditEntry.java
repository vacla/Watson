package eu.minemania.watson.db.data;

import eu.minemania.watson.db.BlockEdit;

public class BlockeditEntry
{
    private final BlockEdit edit;

    public BlockeditEntry(BlockEdit edit)
    {
        this.edit = edit;
    }

    public BlockEdit getEdit()
    {
        return this.edit;
    }
}
