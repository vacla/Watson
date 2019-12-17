package eu.minemania.watson.chat;

import java.util.HashMap;

import net.minecraft.util.Formatting;

public enum Color {

	black(Formatting.BLACK),
	darkblue(Formatting.DARK_BLUE),
	darkgreen(Formatting.DARK_GREEN),
	darkaqua(Formatting.DARK_AQUA),
	darkred(Formatting.DARK_RED), 
	darkpurple(Formatting.DARK_PURPLE),
	gold(Formatting.GOLD),
	grey(Formatting.GRAY),
	gray(Formatting.GRAY),
	darkgrey(Formatting.DARK_GRAY),
	darkgray(Formatting.DARK_GRAY),
	blue(Formatting.BLUE),
	green(Formatting.GREEN),
	aqua(Formatting.AQUA),
	red(Formatting.RED),
	lightpurple(Formatting.LIGHT_PURPLE),
	yellow(Formatting.YELLOW),
	white(Formatting.WHITE);

	private Formatting _color;
	private static HashMap<Formatting, Color> _byTextFormatColor = new HashMap<Formatting, Color>();

	static {
		_byTextFormatColor.put(Formatting.BLACK, Color.black);
		_byTextFormatColor.put(Formatting.DARK_BLUE, Color.darkblue);
		_byTextFormatColor.put(Formatting.DARK_GREEN, Color.darkgreen);
		_byTextFormatColor.put(Formatting.DARK_AQUA, Color.darkaqua);
		_byTextFormatColor.put(Formatting.DARK_RED, Color.darkred);
		_byTextFormatColor.put(Formatting.DARK_PURPLE, Color.darkpurple);
		_byTextFormatColor.put(Formatting.GOLD, Color.gold);
		_byTextFormatColor.put(Formatting.GRAY, Color.gray);
		_byTextFormatColor.put(Formatting.DARK_GRAY, Color.darkgray);
		_byTextFormatColor.put(Formatting.BLUE, Color.blue);
		_byTextFormatColor.put(Formatting.GREEN, Color.green);
		_byTextFormatColor.put(Formatting.AQUA, Color.aqua);
		_byTextFormatColor.put(Formatting.RED, Color.red);
		_byTextFormatColor.put(Formatting.LIGHT_PURPLE, Color.lightpurple);
		_byTextFormatColor.put(Formatting.YELLOW, Color.yellow);
		_byTextFormatColor.put(Formatting.WHITE, Color.white);
	};

	public Formatting getColor() {
		return _color;
	}

	public static Color getByTextFormatColor(Formatting colorTextFormat) {
		Color color = _byTextFormatColor.get(colorTextFormat);
		if(color == null) {
			throw new IllegalArgumentException("invalid color code: " + color);
		}
		return color;
	}

	public static Color getByColorOrName(String colorOrName) {
		if(colorOrName.contains("Formatting.")) {
			return getByTextFormatColor((Formatting)(Object)colorOrName);
		} else {
			return Color.valueOf(colorOrName.toLowerCase());
		}
	}

	private Color(Formatting formattingName) {
		_color = formattingName;
	}
}
