package eu.minemania.watson.analysis;

import java.util.regex.Pattern;

//----------------------------------------------------------------------------
/**
* Regular expressions describing chat messages output by CoreProtect.
* 
* CoreProtect patterns:
* 
* <pre>
* §f----- §3CoreProtect §f----- §7(x2/y63/z-6)
* §70.00/h ago §f- §3totemo §fplaced §3#4 (Cobblestone)§f.
* §71.36/h ago §f- §3totemo §fremoved §3#4 (Cobblestone)§f.
* §f----- §3CoreProtect Lookup Results §f-----
* §70.01/h ago §f- §3totemo §fremoved §3#4 (Cobblestone)§f.
* §f                 §7^ §o(x3/y63/z-7/world)
* §70.01/h ago §f- §3totemo §fplaced §3#4 (Cobblestone)§f.
* §f                 §7^ §o(x2/y63/z-7/world)
* </pre>
*/
public interface CoreProtectPatterns {
	public static final Pattern INSPECTOR_COORDS = Pattern.compile("^----- CoreProtect ----- \\(x(-?\\d+)\\/y(\\d+)\\/z(-?\\d+)\\)$");
	public static final Pattern DETAILS          = Pattern.compile("^(\\d+\\,\\d+\\/h ago|\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{2}:\\d{2}) - (#?\\w+) (\\w+) (\\w+).+");
	public static final Pattern LOOKUP_COORDS    = Pattern.compile("^ +\\^ \\(x(-?\\d+)\\/y(\\d+)\\/z(-?\\d+)\\/(.+)\\)$");
	public static final Pattern LOOKUP_HEADER    = Pattern.compile("^----- CoreProtect Lookup Results -----$");
}
