package eu.minemania.watson.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class TypedOreDB
{
    protected HashMap<IntCoord, OreBlock> _oreBlocks;
    protected TreeSet<OreDeposit> _oreDeposits = new TreeSet<>();

    public TypedOreDB(int initialCapacity)
    {
        _oreBlocks = new HashMap<>(initialCapacity);
    }

    public void clear()
    {
        _oreBlocks.clear();
        _oreDeposits.clear();
    }

    public TreeSet<OreDeposit> getOreDeposits()
    {
        return _oreDeposits;
    }

    public int getOreDepositCount()
    {
        return _oreDeposits.size();
    }

    public void addBlockEdit(BlockEdit edit)
    {
        IntCoord coord = new IntCoord(edit.x, edit.y, edit.z, edit.world);
        OreBlock block = getOreBlock(coord);
        if (block == null)
        {
            block = new OreBlock(coord, edit);
            _oreBlocks.put(coord, block);

            TreeSet<OreDeposit> deposits = getAdjacentDeposits(coord);
            if (deposits.size() == 0)
            {
                OreDeposit deposit = new OreDeposit();
                deposit.addOreBlock(block);
                _oreDeposits.add(deposit);
            }
            else if (deposits.size() == 1)
            {
                OreDeposit deposit = deposits.first();
                _oreDeposits.remove(deposit);
                deposit.addOreBlock(block);
                _oreDeposits.add(deposit);
            }
            else
            {
                ArrayList<OreBlock> blocks = new ArrayList<>();
                for (OreDeposit deposit : deposits)
                {
                    _oreDeposits.remove(deposit);
                    blocks.addAll(deposit.getOreBlocks());
                }
                OreDeposit merged = new OreDeposit();
                merged.addOreBlock(block);
                for (OreBlock b : blocks)
                {
                    merged.addOreBlock(b);
                }
                _oreDeposits.add(merged);
            }
        }
    }

    public void removeDeposits(String player)
    {
        ArrayList<BlockEdit> retainedEdits = new ArrayList<>();
        for (OreBlock block : _oreBlocks.values())
        {
            if (!block.getEdit().player.equalsIgnoreCase(player))
            {
                retainedEdits.add(block.getEdit());
            }
        }
        _oreBlocks.clear();
        _oreDeposits.clear();
        for (BlockEdit edit : retainedEdits)
        {
            addBlockEdit(edit);
        }
    }

    protected OreBlock getOreBlock(IntCoord location)
    {
        return _oreBlocks.get(location);
    }

    protected TreeSet<OreDeposit> getAdjacentDeposits(IntCoord location)
    {
        TreeSet<OreDeposit> deposits = new TreeSet<>();
        IntCoord adjacent = new IntCoord();
        for (int dx = -1; dx <= 1; ++dx)
        {
            for (int dy = -1; dy <= 1; ++dy)
            {
                for (int dz = -1; dz <= 1; ++dz)
                {
                    if (dx != 0 || dy != 0 || dz != 0)
                    {
                        adjacent.setX(location.getX() + dx);
                        adjacent.setY(location.getY() + dy);
                        adjacent.setZ(location.getZ() + dz);
                        OreBlock neighbour = getOreBlock(adjacent);
                        if (neighbour != null)
                        {
                            deposits.add(neighbour.getDeposit());
                        }
                    }
                }
            }
        }
        return deposits;
    }
}