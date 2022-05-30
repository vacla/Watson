package eu.minemania.watson;

import fi.dy.masa.malilib.util.StringUtils;
import fi.dy.masa.malilib.util.data.ModInfo;

public class Reference
{
    public static final String MOD_ID = "watson";
    public static final String MOD_NAME = "Watson";
    public static final String MOD_VERSION = /*StringUtils.getModVersionString(MOD_ID);*/ "?";

    public static final ModInfo MOD_INFO = new ModInfo(MOD_VERSION, MOD_NAME);
    public static final int LEDGER_PROTOCOL = 2;
}