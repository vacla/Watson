package eu.minemania.watson.config;

import javax.annotation.Nullable;

import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.util.StringUtils;

public class ConfigStringExt extends ConfigString
{
    public ConfigStringExt(String name, String defaultValue, String comment)
    {
        super(name, defaultValue, comment);
        this.comment = comment;
    }

    private final String comment;
    private Object[] commentArgs;

    @Override
    @Nullable
    public String getComment()
    {
        return StringUtils.translate(this.comment, getCommentArgs());
    }

    public Object[] getCommentArgs()
    {
        if (this.commentArgs != null)
        {
            return this.commentArgs;
        }
        return new Object[0];
    }

    public ConfigStringExt setCommentArgs(Object... args)
    {
        this.commentArgs = args;
        return this;
    }
}
