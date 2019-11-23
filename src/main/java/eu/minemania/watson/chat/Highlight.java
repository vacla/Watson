package eu.minemania.watson.chat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.MutablePair;

import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class Highlight {
	private static Minecraft mc = Minecraft.getInstance();
	private static final Highlight INSTANCE = new Highlight();
	public static boolean changeUsername;
	static protected String username;
	private static final HashSet<MutablePair<Pattern, MutablePair<TextFormatting, TextFormatting>>> highlights = new HashSet<>();
	private static final String tempkey = "chat.type.text";
	
	private static Highlight getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Highlights player chat message in vanilla.
	 * 
	 * @param key Key to translate this component
	 * @param message Chat message to highlight
	 * @return Highlighted TextComponent
	 */
	public static ITextComponent setHighlightChatMessage(String key,ITextComponent message, boolean watsonMessage) {
		//String user = "";
		String textChat = "";
		int i = 0;
		ITextComponent endMessage;
		if(!watsonMessage) {
			for (ITextComponent chatComponent : message) {
				if(i == 1){
					//user = chatComponent.toString();
				}
				if(i>2) {
					textChat += chatComponent.getFormattedText();
				}
				i++;
			}
			//setUsername(user);
			endMessage = new TextComponentTranslation(key, new Object[] {mc.player.getDisplayName(), Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue() ? highlight(textChat) : textChat});
		} else {
			endMessage = message;
		}
		return endMessage;
	}
	
	/**
	 * Highlights player chat message on modded server (Paper/Spigot).
	 * 
	 * @param message Chat message to highlight
	 * @return Highlighted TextComponent
	 */
	public static ITextComponent setHighlightChatMessage(ITextComponent message) {
		//String username = "";
		String textChat = "";
		String chat = "";
		ITextComponent endMessage;
		ITextComponent prefix = new TextComponentString("");
		int i = 0;
		String serverBrand = mc.player.getServerBrand().toLowerCase();
		//boolean prefix = false;
		if(serverBrand.contains("spigot") || serverBrand.contains("paper")) {
			for(ITextComponent chatComponent : message) {
				if(i > 0) {
					chat += chatComponent.getString();
				}
				i++;
			}
			//prefix = chat.contains("[") && chat.contains("]");
			if(chat.contains("<") && chat.contains(">")) {
				int startUsername = chat.indexOf("<") + 1;
				int endUsername = chat.indexOf(">");
				if((chat.contains("[") && chat.contains("]")) && chat.indexOf("]") < startUsername - 1) {
					prefix = new TextComponentString(chat.substring(chat.indexOf("["), chat.indexOf("]") + 1)); 
				}
				if(!prefix.equals(new TextComponentString("")) || chat.startsWith("<")) {
					username = chat.substring(startUsername, endUsername);
				} else {
					return endMessage = message;
				}
				textChat = chat.substring(endUsername + 2);
				changeUsername = true;
				setUsername(username);
				
				endMessage = new TextComponentTranslation(tempkey, new Object[] { mc.player.getDisplayName(), highlight(textChat)});
				if(!prefix.equals(new TextComponentString(""))) {
					prefix.appendSibling(endMessage);
					endMessage = prefix;
				}
			} else {
				endMessage = message;
			}
		} else {
			endMessage = message;
		}
		return endMessage;
	}
	
	//private static int i = 0;
	/**
	 * Highlights text of player chat message.
	 * 
	 * @param chatText Text that player send
	 * @return Highlighted text
	 */
	private static String highlight(String chatText) {
		String endString = "";
		for(MutablePair<Pattern, MutablePair<TextFormatting, TextFormatting>> item_highlight : highlights) {
			Matcher matcher = item_highlight.getLeft().matcher(chatText);
			if(!matcher.find()) {
				endString = chatText;
			} else {
				matcher.reset();
				while (matcher.find()) {
					int start = matcher.start();
					int stop = matcher.end();
					if(item_highlight.getRight().getLeft() != null && item_highlight.getRight().getRight() == null) {
						endString = matcher.replaceAll(item_highlight.getRight().getLeft() + chatText.substring(start, stop) + TextFormatting.RESET);
					} else if(item_highlight.getRight().getLeft() == null && item_highlight.getRight().getRight() != null){
						endString = matcher.replaceAll(item_highlight.getRight().getRight() + chatText.substring(start, stop) + TextFormatting.RESET);
					} else {
						endString = matcher.replaceAll(item_highlight.getRight().getLeft() + "" + item_highlight.getRight().getRight() + chatText.substring(start, stop) + TextFormatting.RESET);
					}
				}
			}
		}
		return endString;
	}
	
	private static void setUsername(String user) {
		username = user;
	}
	
	public static String getUsername() {
		return username;
	}
	
	/**
	 * Converts character style to TextFormatting style.
	 * 
	 * @param charac Character of style
	 * @return Style in TextFormatting
	 */
	private TextFormatting getStyle(String charac) {
		TextFormatting result = TextFormatting.RESET;
		switch (charac) {
		case "+":
			result = TextFormatting.BOLD;
			break;
		case "/":
			result = TextFormatting.ITALIC;
			break;
		case "_":
			result = TextFormatting.UNDERLINE;
			break;
		case "-":
			result = TextFormatting.STRIKETHROUGH;
			break;
		case "?":
			result = TextFormatting.OBFUSCATED;
			break;
		default:
			break;
		}
		return result;
	}
	
	/**
	 * Sets lists from where to get highlight list config.
	 * 
	 * @param list Config list of highlight items
	 */
	public static void setHighlightList(List<String> list){
		 highlights.clear();
		 
		 getInstance().populateHighlightList(highlights, list);
    }
	
	/**
	 * Checks if style character.
	 * 
	 * @param style String of 1 character that might be a style
	 * @return True if style character used
	 */
	private boolean isStyle(String style) {
		switch (style) {
			case "+":
			case "/":
			case "_":
			case "-":
			case "?":
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Checks if color string.
	 * 
	 * @param color String that might be a color
	 * @return True if color string
	 */
	private boolean isColor(String color) {
		switch (color) {
			case "black":
			case "darkblue":
			case "darkgreen":
			case "darkaqua":
			case "darkred":
			case "darkpurple":
			case "gold":
			case "grey":
			case "gray":
			case "darkgrey":
			case "darkgray":
			case "blue":
			case "green":
			case "aqua":
			case "red":
			case "lightpurple":
			case "yellow":
			case "white":
				return true;
			default:
				return false;
		}
	}
	
	public static void listHighlights() {
		if(highlights.isEmpty()) {
			InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.empty");
		} else {
			int index = 0;
			for(MutablePair<Pattern, MutablePair<TextFormatting, TextFormatting>> item_highlight : highlights) {
				TextFormatting color = item_highlight.getRight().getLeft();
				TextFormatting style = item_highlight.getRight().getRight();
				
				ChatMessage.localOutputT("watson.message.highlight.list_string", index + 1, item_highlight.getLeft(), color != null ? color.getFriendlyName() : color, style != null ? style.getFriendlyName() : style);
				++index;
			}
		}
	}
	
	public static void remove(String pattern) {
		if(highlights.isEmpty()) {
			InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.empty");
			return;
		}
        List<String> orig = Configs.Lists.HIGHLIGHT.getStrings();
        List<String> copy = new ArrayList<>(orig);
        copy.removeIf((str) -> str.contains(pattern));

        if (copy.size() != orig.size()) {
            Configs.Lists.HIGHLIGHT.setStrings(copy);
            InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.removed", pattern);
        }
    }
	
	public static void add(String pattern, String color, String style) {
		if(highlights.isEmpty()) {
			InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.empty");
			return;
		}
		List<String> orig = Configs.Lists.HIGHLIGHT.getStrings();
		List<String> copy = new ArrayList<String>(orig);
		if(pattern != null && (style != null || color != null)) {
			copy.add(style + color + ";" + pattern);
			Configs.Lists.HIGHLIGHT.setStrings(copy);
			InfoUtils.showInGameMessage(MessageType.INFO, "watson.message.highlight.added", copy.size(), pattern);
		}
	}
	
	/**
	 * Populates highlight list with config highlight list.
	 * 
	 * @param highlightpair List for highlight
	 * @param names Config highlight list items
	 */
	private void populateHighlightList(HashSet<MutablePair<Pattern, MutablePair<TextFormatting, TextFormatting>>> highlightpair, List<String> names) {
		for (String str : names) {
			try {
				if(str.isEmpty() == false) {
					int index = str.indexOf(";");
		            if (index != -1){
		                String format = str.substring(0, index);
		                String pattern = str.substring(index+1);
		                MutablePair<Pattern, MutablePair<TextFormatting, TextFormatting>> pr = new MutablePair<>();
		                MutablePair<TextFormatting, TextFormatting> pr2 = new MutablePair<>();
		                if (format.length() > 0) {
		                	if(format.length() == 1) {
		                		if(isStyle(format)) {
		                			pr2.setRight(getStyle(format));
		                		}
		                	} else {
		                		String style = format.substring(0, 1);
		                		String color = format.substring(1);
		                		if(isStyle(style)) {
		                			pr2.setRight(getStyle(style));
		                		}
		                		if(isColor(format)) {
		                			pr2.setLeft(Color.getByColorOrName(format).getColor());
		                		} else if(isColor(color)) {
		                			pr2.setLeft(Color.getByColorOrName(color).getColor());
		                		}
		                	}
		                	pr.setRight(pr2);
		                }
		                if (pattern.length() > 0) {
		                    pr.setLeft(Pattern.compile(pattern));
		                }
		                if(pr.getLeft() != null && pr.getRight() != null) {
		                	highlightpair.add(pr);
		                }
		            }
				}
			} catch (Exception e) {
				Watson.logger.warn("Invalid highlight: '{}'", str);
			}
        }
	}
}
