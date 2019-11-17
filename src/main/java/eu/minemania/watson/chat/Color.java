package eu.minemania.watson.chat;

import java.util.HashMap;

import net.minecraft.util.text.TextFormatting;

public enum Color {
	
	black(TextFormatting.BLACK),
	darkblue(TextFormatting.DARK_BLUE),
	darkgreen(TextFormatting.DARK_GREEN),
	darkaqua(TextFormatting.DARK_AQUA),
	darkred(TextFormatting.DARK_RED), 
	darkpurple(TextFormatting.DARK_PURPLE),
	gold(TextFormatting.GOLD),
	grey(TextFormatting.GRAY),
	gray(TextFormatting.GRAY),
	darkgrey(TextFormatting.DARK_GRAY),
	darkgray(TextFormatting.DARK_GRAY),
	blue(TextFormatting.BLUE),
	green(TextFormatting.GREEN),
	aqua(TextFormatting.AQUA),
	red(TextFormatting.RED),
	lightpurple(TextFormatting.LIGHT_PURPLE),
	yellow(TextFormatting.YELLOW),
	white(TextFormatting.WHITE);
	
	private TextFormatting _color;
	private static HashMap<TextFormatting, Color> _byTextFormatColor = new HashMap<TextFormatting, Color>();
	
	static {
		_byTextFormatColor.put(TextFormatting.BLACK, Color.black);
		_byTextFormatColor.put(TextFormatting.DARK_BLUE, Color.darkblue);
		_byTextFormatColor.put(TextFormatting.DARK_GREEN, Color.darkgreen);
		_byTextFormatColor.put(TextFormatting.DARK_AQUA, Color.darkaqua);
		_byTextFormatColor.put(TextFormatting.DARK_RED, Color.darkred);
		_byTextFormatColor.put(TextFormatting.DARK_PURPLE, Color.darkpurple);
		_byTextFormatColor.put(TextFormatting.GOLD, Color.gold);
		_byTextFormatColor.put(TextFormatting.GRAY, Color.gray);
		_byTextFormatColor.put(TextFormatting.DARK_GRAY, Color.darkgray);
		_byTextFormatColor.put(TextFormatting.BLUE, Color.blue);
		_byTextFormatColor.put(TextFormatting.GREEN, Color.green);
		_byTextFormatColor.put(TextFormatting.AQUA, Color.aqua);
		_byTextFormatColor.put(TextFormatting.RED, Color.red);
		_byTextFormatColor.put(TextFormatting.LIGHT_PURPLE, Color.lightpurple);
		_byTextFormatColor.put(TextFormatting.YELLOW, Color.yellow);
		_byTextFormatColor.put(TextFormatting.WHITE, Color.white);
	};
	
	public TextFormatting getColor() {
		return _color;
	}
	
	public static Color getByTextFormatColor(TextFormatting colorTextFormat) {
		Color color = _byTextFormatColor.get(colorTextFormat);
		if(color == null) {
			throw new IllegalArgumentException("invalid color code: " + color);
		}
		return color;
	}
	
	public static Color getByColorOrName(String colorOrName) {
		if(colorOrName.contains("TextFormatting.")) {
			return getByTextFormatColor((TextFormatting)(Object)colorOrName);
		} else {
			return Color.valueOf(colorOrName.toLowerCase());
		}
	}
	
	private Color(TextFormatting formattingName) {
		_color = formattingName;
	}
}
