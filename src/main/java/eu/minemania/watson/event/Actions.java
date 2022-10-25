package eu.minemania.watson.event;

import eu.minemania.watson.config.Configs;
import malilib.action.ActionUtils;

public class Actions
{
    public static void init()
    {
        ActionUtils.registerBooleanConfigActions(Configs.Generic.OPTIONS);
    }
}
