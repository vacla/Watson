package eu.minemania.watson.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;

public final class WatsonBlockRegistery
{
    private static final WatsonBlockRegistery INSTANCE = new WatsonBlockRegistery();
    public static final Map<String, WatsonBlock> _byName = new HashMap<String, WatsonBlock>();
    protected String blockname = "";
    protected float lineWidth;
    protected Color4f color;

    public static WatsonBlockRegistery getInstance()
    {
        return INSTANCE;
    }

    public static void setWatsonBlockList(List<String> list)
    {
        clear();

        getInstance().populateWatsonBlockList(list);

        WatsonBlock unknown = getInstance().getWatsonBlockByName("minecraft:bedrock");
        if(unknown == null)
        {
            unknown = new WatsonBlock();
            unknown.setName("minecraft:bedrock");
            getInstance().addWatsonBlock(unknown);
        }
    }

    private void populateWatsonBlockList(List<String> names)
    {
        for (String entry : names)
        {
            try
            {
                if(entry.isEmpty() == false)
                {
                    String[] watsonBlockData = entry.split(";");
                    if(watsonBlockData.length == 3)
                    {
                        Block block = IRegistry.BLOCK.getOrDefault(new ResourceLocation(watsonBlockData[0]));
                        WatsonBlock watsonBlock = new WatsonBlock();
                        if(block != Blocks.AIR)
                        {
                            String blockName = IRegistry.ITEM.getKey(new ItemStack(block).getItem()).toString();
                            watsonBlock.setName(blockName);
                            float lineWidth = Float.parseFloat(watsonBlockData[1]);
                            if(lineWidth != 0)
                            {
                                watsonBlock.setLineWidth(lineWidth);
                            }
                            int colorTemp = Integer.parseInt(watsonBlockData[2]);
                            if(colorTemp != 0)
                            {
                                float alpha = (colorTemp >>> 24) & 0xFF;
                                Color4f color;
                                if(alpha == 0)
                                {
                                    color = Color4f.fromColor(colorTemp, (int) (0.8 * 255));
                                }
                                else
                                {
                                    color = Color4f.fromColor(colorTemp);
                                }
                                watsonBlock.setColor(color);
                            }
                            addWatsonBlock(watsonBlock);
                        }
                        else
                        {
                            EntityType<?> entity = EntityType.getById(watsonBlockData[0]);
                            if(entity != null)
                            {
                                watsonBlock.setName(watsonBlockData[0]);
                                float lineWidth = Float.parseFloat(watsonBlockData[1]);
                                if(lineWidth != 0)
                                {
                                    watsonBlock.setLineWidth(lineWidth);
                                }
                                int colorTemp = Integer.parseInt(watsonBlockData[2]);
                                if(colorTemp != 0)
                                {
                                    float alpha = (colorTemp >>> 24) & 0xFF;
                                    Color4f color;
                                    if(alpha == 0)
                                    {
                                        color = Color4f.fromColor(colorTemp, (int) (0.8 * 255));
                                    }
                                    else
                                    {
                                        color = Color4f.fromColor(colorTemp);
                                    }
                                    watsonBlock.setColor(color);
                                }
                                addWatsonBlock(watsonBlock);
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Watson.logger.warn("Invalid block: '{}'", entry);
            }
        }
    }

    public static void clear()
    {
        _byName.clear();
    }

    private void addWatsonBlock(WatsonBlock watsonBlock)
    {
        if(Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.debug("watson block: '{}'", watsonBlock.toString());
        }
        String name = watsonBlock.getName();
        addWatsonBlockName(name, watsonBlock);

        String noSpaces = name.replaceAll(" ", "");
        if(!name.equals(noSpaces))
        {
            addWatsonBlockName(noSpaces, watsonBlock);
        }
        String underscores = name.replaceAll(" ", "_");
        addWatsonBlockName(underscores, watsonBlock);
    }

    private void addWatsonBlockName(String name, WatsonBlock watsonBlock)
    {
        WatsonBlock oldWatsonBlock = _byName.get(name);
        if (oldWatsonBlock == null)
        {
            _byName.put(name, watsonBlock);
        }
    }

    public WatsonBlock getWatsonBlockByName(String name)
    {
        WatsonBlock result = _byName.get("minecraft:" +name.toLowerCase());
        if(result == null)
        {
            result = _byName.get(name.toLowerCase());
        }
        if(name.contains("minecraft:"))
        {
            result = _byName.get(name.toLowerCase());
        }
        if (result == null)
        {
            // Return the "unknown" WatsonBlock.
            return _byName.get("minecraft:bedrock");
        }
        else
        {
            return result;
        }
    }

    public WatsonBlock getBlockKillTypeByName(String name)
    {
        WatsonBlock result = _byName.get("minecraft:" +name.toLowerCase());
        if(result == null)
        {
            return _byName.get("minecraft:player");
        }
        else
        {
            return result;
        }
    }
}