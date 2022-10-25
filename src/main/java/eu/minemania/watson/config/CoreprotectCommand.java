package eu.minemania.watson.config;

import com.google.common.collect.ImmutableList;
import malilib.config.value.BaseOptionListConfigValue;

public class CoreprotectCommand extends BaseOptionListConfigValue
{
    public static final CoreprotectCommand CO = new CoreprotectCommand("co", "watson.label.coreprotectcommand.co");
    public static final CoreprotectCommand CORE = new CoreprotectCommand("core", "watson.label.coreprotectcommand.core");
    public static final CoreprotectCommand COREPROTECT = new CoreprotectCommand("coreprotect", "watson.label.coreprotectcommand.coreprotect");

    public static final ImmutableList<CoreprotectCommand> VALUES = ImmutableList.of(CO, CORE, COREPROTECT);

    public CoreprotectCommand(String configString, String translationKey)
    {
        super(configString, translationKey);
    }
}