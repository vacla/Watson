package eu.minemania.watson.db;

import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.render.RenderUtils;
import net.minecraft.block.*;
import net.minecraft.block.OreBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class BlockEdit
{
    public long time;
    public String player;
    public String action;
    public int amount;
    public int x;
    public int y;
    public int z;
    public WatsonBlock block;
    public String world;
    public PlayereditSet playereditSet;
    public boolean disabled;
    private final BlockRenderManager blockModelShapes;
    protected boolean drawn;

    public BlockEdit(long time, String player, String action, int x, int y, int z, WatsonBlock block, String world, int amount)
    {
        this.time = time;
        this.player = player;
        this.action = action;
        this.amount = amount;
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
        this.world = world;
        MinecraftClient mc = MinecraftClient.getInstance();
        this.blockModelShapes = mc.getBlockRenderManager();
    }

    public Object drawOutline(BufferBuilder buffer)
    {
        Block blocks = Registry.BLOCK.get(Identifier.tryParse(block.getName()));
        float lineWidth = block.getLineWidth();
        if (!blocks.getName().getString().toLowerCase().contains("air"))
        {
            if (Configs.Outlines.ORE_OUTLINE_THICKER.getBooleanValue() && blocks instanceof OreBlock)
            {
                lineWidth = Configs.Outlines.ORE_LINEWIDTH.getIntegerValue();
            }
            RenderSystem.lineWidth(lineWidth);
            renderBlocks(buffer, blocks);
        }
        else
        {
            RenderSystem.lineWidth(lineWidth);
            Optional<EntityType<?>> entity = EntityType.get(block.getName());
            renderEntities(buffer, entity);
        }
        return null;
    }

    private void renderBlocks(BufferBuilder buffer, Block blocks)
    {
        if (!block.getName().equals("minecraft:grass") && !block.getName().equals("minecraft:water") &&
                !block.getName().equals("minecraft:lava"))
        {
            BlockState state = blocks.getDefaultState();
            BakedModel model = this.blockModelShapes.getModel(state);
            if (Configs.Lists.SMALLER_RENDER_BOX.getStrings().contains(block.getName()))
            {
                fi.dy.masa.malilib.render.RenderUtils.drawBlockBoundingBoxOutlinesBatchedLines(new BlockPos(x, y, z), block.getColor(), -0.25, buffer);
            }
            else
            {
                if (isOreNotDrawn())
                {
                    if (blocks instanceof SignBlock || blocks instanceof WallSignBlock)
                    {
                        RenderUtils.drawSpecialOutlinesBatched(x, y, z, block, buffer, true);
                    }
                    else if (blocks instanceof ChestBlock)
                    {
                        RenderUtils.drawFullBlockOutlinesBatched(x, y, z, block.getColor(), buffer);
                    }
                    else
                    {
                        RenderUtils.drawBlockModelOutlinesBatched(model, state, new BlockPos(x, y, z), block.getColor(), buffer);
                    }
                }
            }
            if (!drawn && (blocks instanceof OreBlock || blocks.is(Blocks.ANCIENT_DEBRIS) || blocks.is(Blocks.GILDED_BLACKSTONE)))
            {
                drawn = true;
            }
        }
        else
        {
            if (isOreNotDrawn())
            {
                RenderUtils.drawFullBlockOutlinesBatched(x, y, z, block.getColor(), buffer);
            }
        }
    }

    private void renderEntities(BufferBuilder buffer, Optional<EntityType<?>> entity)
    {
        if (entity.isPresent())
        {
            if (block.getName().equals("minecraft:item_frame") || block.getName().equals("minecraft:painting"))
            {
                RenderUtils.drawSpecialOutlinesBatched(x, y, z, block, buffer, false);
            }
            else
            {
                RenderUtils.drawFullBlockOutlinesBatched(x, y, z, block.getColor(), buffer);
            }
        }
        else
        {
            RenderUtils.drawFullBlockOutlinesBatched(x, y, z, block.getColor(), buffer);
        }
    }

    private boolean isOreNotDrawn()
    {
        for (BlockEdit blockEdit : playereditSet._edits)
        {
            if (Configs.Outlines.ONLY_ORE_BLOCK.getBooleanValue() && blockEdit.x == x && blockEdit.y == y && blockEdit.z == z && blockEdit.drawn)
            {
                return this == blockEdit;
            }
        }

        return true;
    }

    public boolean isCreated()
    {
        return this.action.equals("placed") || this.action.equals("created");
    }

    public boolean isBroken()
    {
        return this.action.equals("broke") || this.action.equals("destroyed");
    }

    public boolean isContAdded()
    {
        return this.action.equals("added") || this.action.equals("put");
    }

    public boolean isContRemoved()
    {
        return this.action.equals("removed") || this.action.equals("took") || this.action.equals("remove");
    }
}