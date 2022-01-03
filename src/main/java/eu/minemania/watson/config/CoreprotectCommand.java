package eu.minemania.watson.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum CoreprotectCommand implements IConfigOptionListEntry
{
    CO("co", "watson.label.coreprotectcommand.co"),
    CORE("core", "watson.label.coreprotectcommand.core"),
    COREPROTECT("coreprotect", "watson.label.coreprotectcommand.coreprotect");

    private final String configString;
    private final String translationKey;

    CoreprotectCommand(String configString, String translationKey)
    {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public String getStringValue()
    {
        return this.configString;
    }

    @Override
    public String getDisplayName()
    {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward)
    {
        int id = this.ordinal();

        if (forward)
        {
            if (++id >= values().length)
            {
                id = 0;
            }
        }
        else
        {
            if (--id < 0)
            {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public CoreprotectCommand fromString(String name)
    {
        return fromStringStatic(name);
    }

    public static CoreprotectCommand fromStringStatic(String name)
    {
        for (CoreprotectCommand action : CoreprotectCommand.values())
        {
            if (action.configString.equalsIgnoreCase(name))
            {
                return action;
            }
        }

        return CoreprotectCommand.CO;
    }
}