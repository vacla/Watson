package eu.minemania.watson.selection;

import java.util.List;

import eu.minemania.watson.db.BlockEdit;
import fi.dy.masa.malilib.util.ItemType;
import net.minecraft.item.ItemStack;

public class PlayereditEntry
{
    private final ItemType item;
    private final int countTotal;
    private final List<BlockEdit> blocks;

    public PlayereditEntry(ItemStack block, int countTotal, List<BlockEdit> blocks)
    {
        this.item = new ItemType(block);
        this.countTotal = countTotal;
        this.blocks = blocks;
    }

    public ItemStack getStack()
    {
        return this.item.getStack();
    }

    public int getCountTotal()
    {
        return this.countTotal;
    }

    public List<BlockEdit> getBlocks()
    {
        return this.blocks;
    }
}
