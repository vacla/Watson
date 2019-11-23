package eu.minemania.watson.config;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;

public enum Plugins implements IConfigOptionListEntry {
	NULL   ("No plugin",   "watson.label.plugin.no_plugin"),
	COREPROTECT   ("CoreProtect",   "watson.label.plugin.coreprotect"),
    LOGBLOCK ("LogBlock", "watson.label.plugin.logblock"),
    MODMODE    ("ModMode",    "watson.label.plugin.modmode"),
    PRISM    ("Prism",    "watson.label.plugin.prism");

    private final String configString;
    private final String translationKey;

    private Plugins(String configString, String translationKey) {
        this.configString = configString;
        this.translationKey = translationKey;
    }

    @Override
    public String getStringValue() {
        return this.configString;
    }

    @Override
    public String getDisplayName() {
        return StringUtils.translate(this.translationKey);
    }

    @Override
    public IConfigOptionListEntry cycle(boolean forward) {
        int id = this.ordinal();

        if (forward) {
            if (++id >= values().length) {
                id = 0;
            }
        } else {
            if (--id < 0) {
                id = values().length - 1;
            }
        }

        return values()[id % values().length];
    }

    @Override
    public Plugins fromString(String name) {
        return fromStringStatic(name);
    }

    public static Plugins fromStringStatic(String name) {
        for (Plugins action : Plugins.values()) {
            if (action.configString.equalsIgnoreCase(name)) {
                return action;
            }
        }

        return Plugins.NULL;
    }
}
