package eu.minemania.watson.db;

public class OreBlock implements Comparable<OreBlock>
{
    protected IntCoord _location;
    protected BlockEdit _edit;
    protected OreDeposit _deposit;

    public OreBlock(IntCoord location, BlockEdit edit)
    {
        _location = location;
        setEdit(edit);
    }

    public IntCoord getLocation()
    {
        return _location;
    }

    public void setEdit(BlockEdit edit)
    {
        _edit = edit;
    }

    public BlockEdit getEdit()
    {
        return _edit;
    }

    public void setDeposit(OreDeposit deposit)
    {
        _deposit = deposit;
    }

    public OreDeposit getDeposit()
    {
        return _deposit;
    }

    @Override
    public int compareTo(OreBlock other)
    {
        if (getLocation().getY() != other.getLocation().getY())
        {
            return getLocation().getY() - other.getLocation().getY();
        }
        else if (getEdit().time != other.getEdit().time)
        {
            return Long.signum(getEdit().time - other.getEdit().time);
        }
        else if (getLocation().getX() != other.getLocation().getX())
        {
            return getLocation().getX() - other.getLocation().getX();
        }
        else if (getLocation().getZ() != other.getLocation().getZ())
        {
            return getLocation().getZ() - other.getLocation().getZ();
        }
        else
        {
            return 0;
        }
    }
}