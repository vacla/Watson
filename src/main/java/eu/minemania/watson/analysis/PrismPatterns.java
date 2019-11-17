package eu.minemania.watson.analysis;

import java.util.regex.Pattern;

public interface PrismPatterns {
	public static final Pattern PLACE_BREAK            = Pattern.compile("^ [+-](?:\\s+\\[\\d+\\])? (\\w+) (?:placed|broke|poured|hung) ((?:\\w| )+) (?:(\\d+):(\\d+) )?(\\w+ ago|just now) \\(a:(\\w+)\\).*$");
	public static final Pattern DATE_TIME_WORLD_COORDS = Pattern.compile("^ -- \\d+ - (\\d+)/(\\d+)/(\\d+) (\\d+):(\\d+):(\\d+)([ap]m) - .+ @ (-?\\d+).0 (\\d+).0 (-?\\d+).0\\s*$");
	public static final Pattern LOOKUP_HEADER          = Pattern.compile("^Prism // Showing (\\d+) result(s)?. Page (\\d+) of (\\d+).*$");
	public static final Pattern LOOKUP_DEFAULTS        = Pattern.compile("^Prism // Using defaults: .*$");
	public static final Pattern INSPECTOR_HEADER       = Pattern.compile("^Prism // --- Inspecting (?:\\w| )+ at (-?\\d+) (\\d+) (-?\\d+) ---$");
}
