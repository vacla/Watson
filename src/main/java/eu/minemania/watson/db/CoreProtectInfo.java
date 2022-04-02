package eu.minemania.watson.db;

import eu.minemania.watson.gui.GuiCoreProtect.ButtonListenerCycleTypePacket.CoreProtectMode;

import java.util.List;

public class CoreProtectInfo
{
    private List<String> actions;
    private List<String> blocks;
    private List<String> dimensions;
    private List<String> entityTypes;
    private List<String> items;
    private String sources;
    private String time;
    private int range;
    private int x;
    private int y;
    private int z;
    private CoreProtectMode coreProtectMode;
    private int pages;

    public CoreProtectInfo(List<String> actions, List<String> blocks, List<String> dimensions, List<String> entityTypes, List<String> items, String sources, String time, int range, int x, int y, int z, CoreProtectMode coreProtectMode, int pages)
    {
        this.actions = actions;
        this.blocks = blocks;
        this.dimensions = dimensions;
        this.entityTypes = entityTypes;
        this.items = items;
        this.sources = sources;
        this.time = time;
        this.range = range;
        this.x = x;
        this.y = y;
        this.z = z;
        this.coreProtectMode = coreProtectMode;
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

    public List<String> getBlocks()
    {
        return blocks;
    }

    public void setBlocks(List<String> blocks)
    {
        this.blocks = blocks;
    }

    public List<String> getDimensions()
    {
        return dimensions;
    }

    public void setDimensions(List<String> dimensions)
    {
        this.dimensions = dimensions;
    }

    public List<String> getEntityTypes()
    {
        return entityTypes;
    }

    public void setEntityTypes(List<String> entityTypes)
    {
        this.entityTypes = entityTypes;
    }

    public List<String> getItems()
    {
        return items;
    }

    public void setItems(List<String> items)
    {
        this.items = items;
    }

    public String getSources()
    {
        return sources;
    }

    public void setSources(String sources)
    {
        this.sources = sources;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
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

    public CoreProtectMode getCoreProtectMode()
    {
        return coreProtectMode;
    }

    public void setCoreProtectMode(CoreProtectMode coreProtectMode)
    {
        this.coreProtectMode = coreProtectMode;
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
