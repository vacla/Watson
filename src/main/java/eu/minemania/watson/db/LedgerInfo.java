package eu.minemania.watson.db;

import eu.minemania.watson.gui.GuiLedger.ButtonListenerCycleTypePacket.LedgerMode;

import java.util.List;

public class LedgerInfo
{
    private List<String> actions;
    private List<String> objects;
    private List<String> dimensions;
    private String sources;
    private String timeBefore;
    private String timeAfter;
    private Integer range;
    private Integer x;
    private Integer y;
    private Integer z;
    private LedgerMode ledgerMode;

    public LedgerInfo(List<String> actions, List<String> objects, List<String> dimensions, String sources, String timeBefore, String timeAfter, Integer range, Integer x, Integer y, Integer z, LedgerMode ledgerMode)
    {
        this.actions = actions;
        this.objects = objects;
        this.dimensions = dimensions;
        this.sources = sources;
        this.timeBefore = timeBefore;
        this.timeAfter = timeAfter;
        this.range = range;
        this.x = x;
        this.y = y;
        this.z = z;
        this.ledgerMode = ledgerMode;
    }

    public List<String> getActions()
    {
        return actions;
    }

    public void setActions(List<String> actions)
    {
        this.actions = actions;
    }

    public List<String> getObjects()
    {
        return objects;
    }

    public void setObjects(List<String> objects)
    {
        this.objects = objects;
    }

    public List<String> getDimensions()
    {
        return dimensions;
    }

    public void setDimensions(List<String> dimensions)
    {
        this.dimensions = dimensions;
    }

    public String getSources()
    {
        return sources;
    }

    public void setSources(String sources)
    {
        this.sources = sources;
    }

    public String getTimeBefore()
    {
        return timeBefore;
    }

    public void setTimeBefore(String timeBefore)
    {
        this.timeBefore = timeBefore;
    }

    public String getTimeAfter()
    {
        return timeAfter;
    }

    public void setTimeAfter(String timeAfter)
    {
        this.timeAfter = timeAfter;
    }

    public Integer getRange()
    {
        return range;
    }

    public void setRange(Integer range)
    {
        this.range = range;
    }

    public Integer getX()
    {
        return x;
    }

    public void setX(Integer x)
    {
        this.x = x;
    }

    public Integer getY()
    {
        return y;
    }

    public void setY(Integer y)
    {
        this.y = y;
    }

    public Integer getZ()
    {
        return z;
    }

    public void setZ(Integer z)
    {
        this.z = z;
    }

    public LedgerMode getLedgerMode()
    {
        return ledgerMode;
    }

    public void setLedgerMode(LedgerMode ledgerMode)
    {
        this.ledgerMode = ledgerMode;
    }
}
