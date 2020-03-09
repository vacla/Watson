package eu.minemania.watson.db;

import java.util.Comparator;

public class BlockEditComparator implements Comparator<BlockEdit>
{
    @Override
    public int compare(BlockEdit l, BlockEdit r)
    {
        if(l.time < r.time)
        {
            return -1;
        }
        else if (l.time > r.time)
        {
            return +1;
        }
        else
        {
            if(!l.creation && r.creation)
            {
                return -1;
            }
            else if (l.creation && !r.creation)
            {
                return +1;
            }
            else
            {
                int dx = l.x - r.x;
                if(dx != 0)
                {
                    return dx;
                }
                else
                {
                    int dy = l.y - r.y;
                    if (dy != 0)
                    {
                        return dy;
                    }
                    else
                    {
                        return (l.z - r.z);
                    }
                }
            }
        }
    }
}
