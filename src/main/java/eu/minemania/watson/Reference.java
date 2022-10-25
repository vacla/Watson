package eu.minemania.watson;

import malilib.util.StringUtils;
import malilib.util.data.ModInfo;

public class Reference
{
    public static final String MOD_ID = "watson";
    public static final String MOD_NAME = "Watson";
    public static final String MOD_VERSION = /*StringUtils.getModVersionString(MOD_ID);*/ "?";

    public static final ModInfo MOD_INFO = new ModInfo(MOD_ID, MOD_NAME);
    public static final int LEDGER_PROTOCOL = 2;
}