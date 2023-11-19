package eu.minemania.watson.db;

import java.util.Comparator;

public class BlockEditComparator implements Comparator<BlockEdit>
{
    @Override
    public int compare(BlockEdit l, BlockEdit r)
    {
        if (l.time < r.time)
        {
            return -1;
        }
        if (l.time > r.time)
        {
            return +1;
        }
        if (!l.isCreated() && r.isCreated())
        {
            return -1;
        }
        if (l.isCreated() && !r.isCreated())
        {
            return +1;
        }
        if (l.x == r.x && l.y == r.y && l.z == r.z)
        {
            if (!(l.isContAdded() || l.isContRemoved()) && (r.isContAdded() || r.isContRemoved()))
            {
                return -1;
            }
            else if ((l.isContAdded() || l.isContRemoved()) && !(r.isContAdded() || r.isContRemoved()))
            {
                return +1;
            }
            else if ((l.isContAdded() || l.isContRemoved()) && (r.isContAdded() || r.isContRemoved()))
            {
                return +1;
            }
        }
        int dx = l.x - r.x;
        if (dx != 0)
        {
            return dx;
        }
        int dy = l.y - r.y;
        if (dy != 0)
        {
            return dy;
        }
        int dz = l.z - r.z;
        if (dz != 0)
        {
            return dz;
        }
        int nameDifference = l.block.getName().compareTo(r.block.getName());
        if (nameDifference > 0)
        {
            return nameDifference;
        }
        int amount = l.amount - r.amount;
        if (amount >= 0)
        {
            return +1;
        }

        return -1;
    }
}