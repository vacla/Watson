package eu.minemania.watson.analysis;

import java.util.regex.Pattern;

public interface MiscPatterns {
	public static final Pattern WG_REGIONS       = Pattern.compile("^Applicable regions: ([a-zA-Z0-9_-]+(?:, [a-zA-Z0-9_-]+)*)$");

	public static final Pattern MODMODE_ENABLE   = Pattern.compile("^You are now in ModMode!$");

	public static final Pattern MODMODE_DISABLE  = Pattern.compile("^You are no longer in ModMode!$");

	public static final Pattern DUTYMODE_ENABLE  = Pattern.compile("^\\[Duties\\] Duty mode enabled.*");

	public static final Pattern DUTYMODE_DISABLE = Pattern.compile("^\\[Duties\\] Duty mode disabled.*");
}
