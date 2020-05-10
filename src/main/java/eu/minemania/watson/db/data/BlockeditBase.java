package eu.minemania.watson.db.data;

import com.google.common.collect.ImmutableList;

public abstract class BlockeditBase
{
    protected ImmutableList<BlockeditEntry> blockeditAll = ImmutableList.of();

    public ImmutableList<BlockeditEntry> getBlockeditAll()
    {
        return this.blockeditAll;
    }

    public abstract void reCreateBlockedits();
}
