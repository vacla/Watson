package eu.minemania.watson.db;

import java.util.HashMap;
import java.util.Optional;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.block.*;
import net.minecraft.block.OreBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
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
    private HashMap<String,Object> additional;

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

    public void setAdditional(HashMap<String,Object> additional)
    {
        this.additional = additional;
    }

    public HashMap<String,Object> getAdditional()
    {
        return this.additional;
    }

    public Object drawOutline(BufferBuilder buffer, MatrixStack matrices)
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
            renderBlocks(buffer, blocks, matrices);
        }
        else
        {
            RenderSystem.lineWidth(lineWidth);
            renderEntities(buffer);
        }
        return null;
    }

    private void renderBlocks(BufferBuilder buffer, Block blocks, MatrixStack matrices)
    {
        Color4f color = block.getOverrideColor() != Color4f.ZERO && block.getOverrideColor() != null ? block.getOverrideColor() : block.getColor();
        if (!block.getName().equals("minecraft:grass") && !block.getName().equals("minecraft:water") &&
                !block.getName().equals("minecraft:lava"))
        {
            BlockState state = blocks.getDefaultState();
            BakedModel model = this.blockModelShapes.getModel(state);
            if (Configs.Lists.SMALLER_RENDER_BOX.getStrings().contains(block.getName()))
            {
                RenderUtils.drawBlockBoundingBoxOutlinesBatchedLines(new BlockPos(x, y, z), color, -0.25, buffer);
            }
            else
            {
                if (isOreNotDrawn())
                {
                    if (blocks instanceof SignBlock || blocks instanceof WallSignBlock)
                    {
                        RenderUtils.drawSpecialOutlinesBatched(x, y, z, color, buffer, true);
                    }
                    else if (blocks instanceof ChestBlock || blocks instanceof ShulkerBoxBlock)
                    {
                        RenderUtils.drawFullBlockOutlinesBatched(x, y, z, color, buffer);
                    }
                    else if (blocks instanceof BedBlock)
                    {
                        RenderUtils.drawBedOutlineBatched(x, y, z, color, buffer);
                    }
                    else
                    {
                        RenderUtils.drawBlockModelOutlinesBatched(model, state, new BlockPos(x, y, z), color, buffer);
                    }
                }
            }
            if (!drawn && (blocks instanceof OreBlock || blocks.equals(Blocks.ANCIENT_DEBRIS) || blocks.equals(Blocks.GILDED_BLACKSTONE)))
            {
                drawn = true;
            }
        }
        else
        {
            if (isOreNotDrawn())
            {
                RenderUtils.drawFullBlockOutlinesBatched(x, y, z, color, buffer);
            }
        }
    }

    private void renderEntities(BufferBuilder buffer)
    {
        Optional<EntityType<?>> entity = EntityType.get(block.getName());
        Color4f color = block.getOverrideColor() != Color4f.ZERO && block.getOverrideColor() != null ? block.getOverrideColor() : block.getColor();
        if (entity.isPresent())
        {
            if (block.getName().equals("minecraft:item_frame") || block.getName().equals("minecraft:painting"))
            {
                RenderUtils.drawSpecialOutlinesBatched(x, y, z, color, buffer, false);
            }
            else
            {
                RenderUtils.drawFullBlockOutlinesBatched(x, y, z, color, buffer);
            }
        }
        else
        {
            RenderUtils.drawFullBlockOutlinesBatched(x, y, z, color, buffer);
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
        return this.action.equals("placed") || this.action.equals("created") || this.action.equals("block-place") || (!(boolean) this.additional.getOrDefault("isContainer", false) && (boolean) this.additional.getOrDefault("added", false));
    }

    public boolean isBroken()
    {
        return this.action.equals("broke") || this.action.equals("destroyed") || this.action.equals("block-break") || (!(boolean) this.additional.getOrDefault("isContainer", false) && !(boolean) this.additional.getOrDefault("added", false));
    }

    public boolean isContAdded()
    {
        return this.action.equals("added") || this.action.equals("put") || this.action.equals("item-insert") || ((boolean) this.additional.getOrDefault("isContainer", false) && (boolean) this.additional.getOrDefault("added", false));
    }

    public boolean isContRemoved()
    {
        return this.action.equals("removed") || this.action.equals("took") || this.action.equals("remove") || this.action.equals("item-remove") || ((boolean) this.additional.getOrDefault("isContainer", false) && !(boolean) this.additional.getOrDefault("added", false));
    }
}