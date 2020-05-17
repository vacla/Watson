package eu.minemania.watson.selection;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.PlayereditSet;
import eu.minemania.watson.db.WatsonBlock;
import eu.minemania.watson.db.data.BlockeditEntry;
import fi.dy.masa.malilib.gui.Message.MessageType;
import fi.dy.masa.malilib.util.InfoUtils;
import fi.dy.masa.malilib.util.ItemType;
import fi.dy.masa.malilib.util.StringUtils;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class PlayereditUtils
{
    public static List<PlayereditEntry> createPlayereditListFor(PlayereditSet playeredit)
    {
        Object2IntOpenHashMap<WatsonBlock> countsTotal = new Object2IntOpenHashMap<>();

        for(BlockEdit edit : playeredit.getBlockEdits())
        {
            countsTotal.addTo(edit.block, edit.amount);
        }

        return getPlayereditList(countsTotal, playeredit);
    }

    public static List<PlayereditEntry> getPlayereditList(Object2IntOpenHashMap<WatsonBlock> countsTotal, PlayereditSet playeredit)
    {
        List<PlayereditEntry> list = new ArrayList<>();
        if(countsTotal.isEmpty() == false)
        {
            Object2IntOpenHashMap<ItemType> itemTypesTotal = new Object2IntOpenHashMap<>();

            convertNameToStack(countsTotal, itemTypesTotal);

            for(ItemType type : itemTypesTotal.keySet())
            {
                List<BlockEdit> watsonBlocks = new ArrayList<>();
                for(BlockEdit edit : playeredit.getBlockEdits())
                {
                    String typeName = type.getStack().getItem().toString();
                    String blockName = getItemStack(edit.block.getName()).getItem().toString();
                    if(typeName.contains("minecraft:") == false)
                    {
                        typeName = "minecraft:"+typeName;
                    }
                    if(blockName.contains("minecraft:") == false)
                    {
                        blockName = "minecraft:"+blockName;
                    }
                    if(blockName.equals(typeName))
                    {
                        watsonBlocks.add(edit);
                    }
                }
                list.add(new PlayereditEntry(type.getStack().copy(), itemTypesTotal.getInt(type), watsonBlocks));
            }
        }

        return list;
    }

    public static List<BlockeditEntry> createDisplayListFor(List<BlockEdit> blockedit)
    {
        List<BlockeditEntry> list = new ArrayList<>();
        if(blockedit.isEmpty() == false)
        {
            for(BlockEdit edit : blockedit)
            {
                list.add(new BlockeditEntry(edit));
            }
        }

        return list;
    }

    private static void convertNameToStack(Object2IntOpenHashMap<WatsonBlock> watsonBlockIn, Object2IntOpenHashMap<ItemType> itemTypeOut)
    {
        for(WatsonBlock block : watsonBlockIn.keySet())
        {
            ItemStack stack = getItemStack(block.getName());

            if(stack.isEmpty() == false)
            {
                ItemType type = new ItemType(stack);
                itemTypeOut.addTo(type, watsonBlockIn.getInt(block));
            }
        }
    }

    public static ItemStack getItemStack(String blocks)
    {
        Block block = Registry.BLOCK.get(Identifier.tryParse(blocks));
        if(block != Blocks.AIR)
        {
            if(block.equals(Blocks.WATER))
            {
                return new ItemStack(Items.WATER_BUCKET);
            }
            else if(block.equals(Blocks.LAVA))
            {
                return new ItemStack(Items.LAVA_BUCKET);
            }
            else if(!block.equals(Blocks.BEDROCK))
            {
                return new ItemStack(block);
            }
        }
        else
        {
            Optional<EntityType<?>> entityType = EntityType.get(blocks);
            if(entityType.isPresent())
            {
                EntityType<?> entity = entityType.get();
                if(entity.equals(EntityType.PAINTING))
                {
                    return new ItemStack(Items.PAINTING);
                }
                else if(entity.equals(EntityType.LEASH_KNOT))
                {
                    return new ItemStack(Items.LEAD);
                }
                else if(entity.equals(EntityType.ITEM_FRAME))
                {
                    return new ItemStack(Items.ITEM_FRAME);
                }
                else if(entity.equals(EntityType.MINECART))
                {
                    return new ItemStack(Items.MINECART);
                }
                else if(entity.equals(EntityType.CHEST_MINECART))
                {
                    return new ItemStack(Items.CHEST_MINECART);
                }
                else if(entity.equals(EntityType.FURNACE_MINECART))
                {
                    return new ItemStack(Items.FURNACE_MINECART);
                }
                else if(entity.equals(EntityType.HOPPER_MINECART))
                {
                    return new ItemStack(Items.HOPPER_MINECART);
                }
                else if(entity.equals(EntityType.TNT_MINECART))
                {
                    return new ItemStack(Items.TNT_MINECART);
                }
                else if(entity.equals(EntityType.BOAT))
                {
                    return new ItemStack(Items.OAK_BOAT);
                }
                else if(entity.equals(EntityType.ARMOR_STAND))
                {
                    return new ItemStack(Items.ARMOR_STAND);
                }
                else if(entity.equals(EntityType.END_CRYSTAL))
                {
                    return new ItemStack(Items.END_CRYSTAL);
                }
                else
                {
                    SpawnEggItem spawnEgg = SpawnEggItem.forEntity(entity);
                    if(spawnEgg == null)
                    {
                        InfoUtils.showGuiMessage(MessageType.WARNING, "watson.error.entity.not_found", entity.getName().getString());
                        return null;
                    }

                    return new ItemStack(spawnEgg);
                }
            }
        }

        InfoUtils.showGuiMessage(MessageType.WARNING, "watson.error.blockentity.not_found", blocks);
        return new ItemStack(Items.BEDROCK);
    }

    public static String blockString(BlockEdit blockedit)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(blockedit.time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return StringUtils.translate("watson.gui.label.blockedit.list.blocks", blockedit.x, blockedit.y, blockedit.z, day, month, year, hour, minute, second, blockedit.world, blockedit.block.getName(), blockedit.amount);
    }
}
