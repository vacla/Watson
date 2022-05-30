package eu.minemania.watson.config;

import fi.dy.masa.malilib.config.option.StringConfig;

public class ConfigStringExt extends StringConfig
{
    public ConfigStringExt(String name, String defaultValue, String comment)
    {
        super(name, defaultValue, comment);
    }

    public ConfigStringExt setCommentArguments(Object... args)
    {
        this.commentArgs = args;
        return this;
    }
}
