package eu.minemania.watson.db;

import java.util.TreeSet;

public class OreDeposit implements Comparable<OreDeposit>
{
    protected TreeSet<OreBlock> _oreBlocks = new TreeSet<OreBlock>();
    protected OreBlock _earliestOreBlock;
    protected OreBlock _latestOreBlock;

    public void addOreBlock(OreBlock block)
    {
        block.setDeposit(this);
        _oreBlocks.add(block);
        if(_earliestOreBlock == null || block.getEdit().time < _earliestOreBlock.getEdit().time)
        {
            _earliestOreBlock = block;
        }
        if(_latestOreBlock == null || block.getEdit().time > _latestOreBlock.getEdit().time)
        {
            _latestOreBlock = block;
        }
    }

    public long getTimeStamp()
    {
        return getKeyOreBlock().getEdit().time;
    }

    public OreBlock getKeyOreBlock()
    {
        return _oreBlocks.first();
    }

    public BlockEdit getEarliestEdit()
    {
        return _earliestOreBlock.getEdit();
    }

    public BlockEdit getLatestEdit()
    {
        return _latestOreBlock.getEdit();
    }

    public WatsonBlock getWatsonBlock()
    {
        return getKeyOreBlock().getEdit().block;
    }

    public int getBlockCount()
    {
        return _oreBlocks.size();
    }

    protected TreeSet<OreBlock> getOreBlocks()
    {
        return _oreBlocks;
    }

    @Override
    public int compareTo(OreDeposit other)
    {
        return Long.signum(getTimeStamp() - other.getTimeStamp());
    }
}