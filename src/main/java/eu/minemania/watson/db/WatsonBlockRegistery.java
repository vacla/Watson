package eu.minemania.watson.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public final class WatsonBlockRegistery {
	private static final WatsonBlockRegistery INSTANCE = new WatsonBlockRegistery();
	//public static final Set<Block> watson_blocks = new HashSet<>();
	public static final Map<String, WatsonBlock> _byName = new HashMap<String, WatsonBlock>();
	protected String blockname = "";
	protected float lineWidth;
	protected Color4f color;
	
	public static WatsonBlockRegistery getInstance() {
		return INSTANCE;
	}
	
	/*public static void loadWatsonBlocks(JsonObject obj) {
		try {
			WatsonBlockRegistery registry = getInstance();
			registry.loadFromJson(obj);
		} catch(Exception e) {
			InfoUtils.showGuiOrInGameMessage(MessageType.ERROR, "error loading block types: ", e);
		}
	}*/
	
	public static void setWatsonBlockList(List<String> list){
		 //watson_blocks.clear();
		clear();
		 
		getInstance().populateWatsonBlockList(list);
		 
		WatsonBlock unknown = getInstance().getWatsonBlockByName("minecraft:bedrock");
		if(unknown == null) {
			unknown = new WatsonBlock();
			unknown.setName("minecraft:bedrock");
			getInstance().addWatsonBlock(unknown);
		}
    }
	
	private void populateWatsonBlockList(List<String> names) {
		for (String entry : names) {
			try {
				if(entry.isEmpty() == false) {
					String[] watsonBlockData = entry.split(";");
					if(watsonBlockData.length == 3) {
						Block block = IRegistry.BLOCK.getOrDefault(new ResourceLocation(watsonBlockData[0]));
						if(block != null) {
							WatsonBlock watsonBlock = new WatsonBlock();
							String blockName = IRegistry.ITEM.getKey(new ItemStack(block).getItem()).toString();
							watsonBlock.setName(blockName);
							float lineWidth = Float.parseFloat(watsonBlockData[1]);
							if(lineWidth != 0) {
								watsonBlock.setLineWidth(lineWidth);
							}
							int colorTemp = Integer.parseInt(watsonBlockData[2]);
							if(colorTemp != 0) {
								float alpha = (colorTemp >>> 24) & 0xFF;
								Color4f color;
								if(alpha == 0) {
									color = Color4f.fromColor(colorTemp, (int) (0.8 * 255));
								} else {
									color = Color4f.fromColor(colorTemp);
								}
								watsonBlock.setColor(color);
							}
							addWatsonBlock(watsonBlock);
						}
					}
				}
			} catch (Exception e) {
				Watson.logger.warn("Invalid block: '{}'", entry);
			}
        }
	}
	
	public static void clear() {
		_byName.clear();
	}
	
	/*public void loadFromJson(JsonObject obj) {
		this.clear();
		
		if(JsonUtils.hasArray(obj, "blocks")) {
			JsonArray blocksArr = obj.get("blocks").getAsJsonArray();
			
			for(int i = 0; i < blocksArr.size(); ++i) {
				JsonElement el = blocksArr.get(i);
				
				if(el.isJsonObject()) {
					JsonObject block = el.getAsJsonObject();
					
					loadWatsonBlock(block);
				}
			}
			
			WatsonBlock unknown = getWatsonBlockByName("minecraft:bedrock");
			if(unknown == null) {
				unknown = new WatsonBlock();
				unknown.setName("unknown");
				addWatsonBlock(unknown);
			}
		} else {
			Watson.logger.warn("Failed to load blocks in");
		}
	}*/
	
	/*private void loadWatsonBlock(JsonObject obj) {
		if(JsonUtils.hasString(obj, "blockname")) {
			this.blockname = JsonUtils.getString(obj, "blockname");
			
			if(JsonUtils.hasFloat(obj, "lineWidth")) {
				this.lineWidth = JsonUtils.getFloat(obj, "lineWidth");
			} else {
				this.lineWidth = 3.0f;
				Watson.logger.warn("Block name '{}' had a non-numberic/null linewidth. Defaulting to '{}'", blockname, lineWidth);
			}
			
			if(JsonUtils.hasInteger(obj, "color")) {
				float alpha = (JsonUtils.getInteger(obj, "color") >>> 24) & 0xFF;
				if(alpha == 0) {
					this.color = Color4f.fromColor(color, (int) (0.8 * 255));
				} else {
					this.color = Color4f.fromColor(JsonUtils.getInteger(obj, "color"));
				}
			} else {
				Watson.logger.warn("Block name '{}' had a malformed color value and was set to the default color", blockname);
				this.color = null;
			}
			
			if(JsonUtils.hasArray(obj, "bounds")) {
				this.bounds = obj.get("bounds").getAsJsonArray();
			} else {
				Watson.logger.warn("Block name '{}' had a badly formed bounds setting; the default will be used", blockname);
				JsonArray arr = new JsonArray();
				arr.add(0.005);
				arr.add(0.005);
				arr.add(0.005);
				arr.add(0.995);
				arr.add(0.995);
				arr.add(0.995);
				bounds = arr;
			}
			
			WatsonBlock watsonBlock = new WatsonBlock();
			watsonBlock.setName(blockname);
			watsonBlock.setLineWidth(lineWidth);
			watsonBlock.setColor(color);
			watsonBlock.setBounds(bounds.get(0).getAsFloat(), bounds.get(1).getAsFloat(), bounds.get(2).getAsFloat(), bounds.get(3).getAsFloat(), bounds.get(4).getAsFloat(), bounds.get(5).getAsFloat());
			
			addWatsonBlock(watsonBlock);
		} else {
			InfoUtils.showGuiAndInGameMessage(MessageType.ERROR, "watson.message.config.cant_load_blockname");
			Watson.logger.warn("a block name was specified without a valid name");
			blockname = null;
		}
	}*/
	
	private void addWatsonBlock(WatsonBlock watsonBlock) {
		if(Configs.Generic.DEBUG.getBooleanValue()) {
			Watson.logger.debug("watson block: '{}'", watsonBlock.toString());
		}
		String name = watsonBlock.getName();
		addWatsonBlockName(name, watsonBlock);
		
		String noSpaces = name.replaceAll(" ", "");
		if(!name.equals(noSpaces)) {
			addWatsonBlockName(noSpaces, watsonBlock);
		}
		String underscores = name.replaceAll(" ", "_");
		addWatsonBlockName(underscores, watsonBlock);
	}
	
	private void addWatsonBlockName(String name, WatsonBlock watsonBlock) {
		WatsonBlock oldWatsonBlock = _byName.get(name);
	    if (oldWatsonBlock == null) {
	    	_byName.put(name, watsonBlock);
	    }
	}
	
	public WatsonBlock getWatsonBlockByName(String name) {
		WatsonBlock result = _byName.get("minecraft:" +name.toLowerCase());
		if(name.contains("minecraft:")) {
			result = _byName.get(name.toLowerCase());
		}
	    if (result == null) {
	      // Return the "unknown" WatsonBlock.
	    	return _byName.get("minecraft:bedrock");
	    } else {
	    	return result;
	    }
	}
	
	public WatsonBlock getBlockKillTypeByName(String name) {
		WatsonBlock result = _byName.get(name.toLowerCase());
		if(result == null) {
			//TODO RETURN SOMETHING
			return result;
		} else {
			return result;
		}
	}
}
