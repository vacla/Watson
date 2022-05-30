package eu.minemania.watson.config;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.config.value.BaseOptionListConfigValue;

public class Plugins extends BaseOptionListConfigValue
{
    public static final Plugins NULL = new Plugins("No plugin", "watson.label.plugin.no_plugin");
    public static final Plugins COREPROTECT = new Plugins("CoreProtect", "watson.label.plugin.coreprotect");
    public static final Plugins LOGBLOCK = new Plugins("LogBlock", "watson.label.plugin.logblock");
    public static final Plugins MODMODE = new Plugins("ModMode", "watson.label.plugin.modmode");
    public static final Plugins PRISM = new Plugins("Prism", "watson.label.plugin.prism");
    public static final Plugins LEDGER = new Plugins("Ledger", "watson.label.plugin.ledger");
    public static final ImmutableList<Plugins> VALUES = ImmutableList.of(NULL, COREPROTECT, LOGBLOCK, MODMODE, PRISM, LEDGER);
    public Plugins(String configString, String translationKey)
    {
        super(configString, translationKey);
    }
}