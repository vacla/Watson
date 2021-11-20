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
    private int range;
    private int x;
    private int y;
    private int z;
    private LedgerMode ledgerMode;
    private int pages;

    public LedgerInfo(List<String> actions, List<String> objects, List<String> dimensions, String sources, String timeBefore, String timeAfter, int range, int x, int y, int z, LedgerMode ledgerMode, int pages)
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
        this.pages = pages;
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

    public int getRange()
    {
        return range;
    }

    public void setRange(int range)
    {
        this.range = range;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getZ()
    {
        return z;
    }

    public void setZ(int z)
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

    public int getPages()
    {
        return pages;
    }

    public void setPages(int pages)
    {
        this.pages = pages;
    }
}
