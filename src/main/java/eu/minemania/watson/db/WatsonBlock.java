package eu.minemania.watson.db;

import fi.dy.masa.malilib.util.Color4f;

public final class WatsonBlock
{
    private String name = "";
    private Color4f color = new Color4f(255, 255, 0, 255);
    private float lineWidth = 1.0f;
    private Color4f overrideColor;

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setColor(Color4f color)
    {
        this.color = color;
    }

    public Color4f getColor()
    {
        return this.color;
    }

    public void setOverrideColor(Color4f overrideColor)
    {
        this.overrideColor = overrideColor;
    }

    public Color4f getOverrideColor()
    {
        return this.overrideColor;
    }

    public void setLineWidth(float lineWidth)
    {
        this.lineWidth = lineWidth;
    }

    public float getLineWidth()
    {
        return this.lineWidth;
    }

    /*
     * returns a String in chat will only be available for debugging
     */
    public String toString()
    {
        return getName() +
                " (" +
                ") [" +
                getColor().r +
                ',' +
                getColor().g +
                ',' +
                getColor().b +
                ',' +
                getColor().a +
                "] " +
                getLineWidth() +
                ')';
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (obj instanceof WatsonBlock)
        {
            return ((WatsonBlock) obj).name.equals(this.name);
        }

        return false;
    }
}