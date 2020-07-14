package eu.minemania.watson.db;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import eu.minemania.watson.Watson;
import eu.minemania.watson.config.Configs;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

public final class WatsonBlockRegistery
{
    private static final WatsonBlockRegistery INSTANCE = new WatsonBlockRegistery();
    public static final Map<String, WatsonBlock> _byName = new HashMap<>();
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
        if (unknown == null)
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
                if (!entry.isEmpty())
                {
                    String[] watsonBlockData = entry.split(";");
                    if (watsonBlockData.length == 3)
                    {
                        Block block = Registry.BLOCK.get(Identifier.tryParse(watsonBlockData[0]));
                        WatsonBlock watsonBlock = new WatsonBlock();
                        if (block != Blocks.AIR)
                        {
                            String blockName = Registry.BLOCK.getId(block).toString();
                            watsonBlock.setName(blockName);
                            float lineWidth = Float.parseFloat(watsonBlockData[1]);
                            if (lineWidth != 0)
                            {
                                watsonBlock.setLineWidth(lineWidth);
                            }
                            int colorst = StringUtils.getColor(watsonBlockData[2], 0);
                            int colorTemp = MathHelper.clamp(colorst, Integer.MIN_VALUE, Integer.MAX_VALUE);
                            if (colorTemp != 0)
                            {
                                Color4f color = Color4f.fromColor(colorTemp);
                                watsonBlock.setColor(color);
                            }
                            addWatsonBlock(watsonBlock);
                        }
                        else
                        {
                            Item item = Registry.ITEM.get(Identifier.tryParse(watsonBlockData[0]));
                            if (item != Items.AIR)
                            {
                                watsonBlock.setName(watsonBlockData[0]);
                                float lineWidth = Float.parseFloat(watsonBlockData[1]);
                                if (lineWidth != 0)
                                {
                                    watsonBlock.setLineWidth(lineWidth);
                                }
                                int colorst = StringUtils.getColor(watsonBlockData[2], 0);
                                int colorTemp = MathHelper.clamp(colorst, Integer.MIN_VALUE, Integer.MAX_VALUE);
                                if (colorTemp != 0)
                                {
                                    Color4f color = Color4f.fromColor(colorTemp);
                                    watsonBlock.setColor(color);
                                }
                                addWatsonBlock(watsonBlock);
                            }
                            Optional<EntityType<?>> entity = EntityType.get(watsonBlockData[0]);
                            if (entity.isPresent())
                            {
                                watsonBlock.setName(watsonBlockData[0]);
                                float lineWidth = Float.parseFloat(watsonBlockData[1]);
                                if (lineWidth != 0)
                                {
                                    watsonBlock.setLineWidth(lineWidth);
                                }
                                int colorst = StringUtils.getColor(watsonBlockData[2], 0);
                                int colorTemp = MathHelper.clamp(colorst, Integer.MIN_VALUE, Integer.MAX_VALUE);
                                if (colorTemp != 0)
                                {
                                    Color4f color = Color4f.fromColor(colorTemp);
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
        if (Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.info("watson block: '{}'", watsonBlock.toString());
        }
        String name = watsonBlock.getName();
        addWatsonBlockName(name, watsonBlock);

        String noSpaces = name.replaceAll(" ", "");
        if (!name.equals(noSpaces))
        {
            addWatsonBlockName(noSpaces, watsonBlock);
        }
        String underscores = name.replaceAll(" ", "_");
        addWatsonBlockName(underscores, watsonBlock);
    }

    private void addWatsonBlockName(String name, WatsonBlock watsonBlock)
    {
        _byName.putIfAbsent(name, watsonBlock);
    }

    public WatsonBlock getWatsonBlockByName(String name)
    {
        WatsonBlock result = _byName.get("minecraft:" + name.toLowerCase());
        if (result == null)
        {
            result = _byName.get(name.toLowerCase());
        }
        if (name.contains("minecraft:"))
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
        WatsonBlock result = _byName.get("minecraft:" + name.toLowerCase());
        if (result == null)
        {
            return _byName.get("minecraft:player");
        }
        else
        {
            return result;
        }
    }
}