package eu.minemania.watson.selection;

import java.util.List;

import eu.minemania.watson.db.BlockEdit;
import fi.dy.masa.malilib.util.ItemType;
import net.minecraft.item.ItemStack;

public class PlayereditEntry
{
    private final ItemType item;
    private final int countBroken;
    private final int countPlaced;
    private final int countContAdded;
    private final int countContRemoved;
    private final int countTotal;
    private final List<BlockEdit> blocks;

    public PlayereditEntry(ItemStack block, int countBroken, int countPlaced, int countContAdded, int countContRemoved, int countTotal, List<BlockEdit> blocks)
    {
        this.item = new ItemType(block);
        this.countBroken = countBroken;
        this.countPlaced = countPlaced;
        this.countContAdded = countContAdded;
        this.countContRemoved = countContRemoved;
        this.countTotal = countTotal;
        this.blocks = blocks;
    }

    public ItemStack getStack()
    {
        return this.item.getStack();
    }

    public int getCountBroken()
    {
        return this.countBroken;
    }

    public int getCountPlaced()
    {
        return this.countPlaced;
    }

    public int getCountContAdded()
    {
        return this.countContAdded;
    }

    public int getCountContRemoved()
    {
        return this.countContRemoved;
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
