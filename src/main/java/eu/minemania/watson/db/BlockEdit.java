package eu.minemania.watson.db;

import java.util.Optional;
import com.mojang.blaze3d.systems.RenderSystem;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
    public boolean creation;
    public int x;
    public int y;
    public int z;
    public WatsonBlock block;
    public String world;
    public PlayereditSet playereditSet;
    private final BlockRenderManager blockModelShapes;
    private MinecraftClient mc;
    protected boolean drawn;

    public BlockEdit(long time, String player, boolean creation, int x, int y, int z, WatsonBlock block, String world)
    {
        this.time = time;
        this.player = player;
        this.creation = creation;
        this.x = x;
        this.y = y;
        this.z = z;
        this.block = block;
        this.world = world;
        this.mc = MinecraftClient.getInstance();
        this.blockModelShapes = this.mc.getBlockRenderManager();
    }

    public void drawOutline(BufferBuilder buffer)
    {
        Block blocks = Registry.BLOCK.get(Identifier.tryParse(block.getName()));
        float lineWidth = block.getLineWidth();
        if(blocks != null && !blocks.getName().asString().equals("Air"))
        {
            if(Configs.Generic.ORE_OUTLINE_THICKER.getBooleanValue() && blocks instanceof OreBlock)
            {
                lineWidth = Configs.Generic.ORE_LINEWIDTH.getIntegerValue();
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
    }

    private void renderBlocks(BufferBuilder buffer, Block blocks)
    {
        if(!block.getName().equals("minecraft:grass"))
        {
            BlockState state = blocks.getDefaultState();
            BakedModel model = this.blockModelShapes.getModel(state);
            if(Configs.Lists.SMALLER_RENDER_BOX.getStrings().contains(block.getName()))
            {
                fi.dy.masa.malilib.render.RenderUtils.drawBlockBoundingBoxOutlinesBatchedLines(new BlockPos(x, y, z), block.getColor(), -0.25, buffer);
            }
            else
            {
                if(!isOreDrawn())
                {
                    RenderUtils.drawBlockModelOutlinesBatched(model, state, new BlockPos(x, y, z), block.getColor(), buffer);
                }
            }
            if(!drawn && blocks instanceof OreBlock)
            {
                drawn = true;
            }
        }
        else
        {
            RenderUtils.drawFullBlockOutlinesBatched(x, y, z, block.getColor(), buffer);
        }
    }

    private void renderEntities(BufferBuilder buffer, Optional<EntityType<?>> entity)
    {
        if(entity != null)
        {
            if(block.getName().equals("minecraft:item_frame") || block.getName().equals("minecraft:painting"))
            {
                RenderUtils.drawItemFramePaintingOutlinesBatched(x, y, z, block.getColor(), buffer);
            }
            else
            {
                RenderUtils.drawFullBlockOutlinesBatched(x, y, z, block.getColor(), buffer);
            }
        }
    }

    public boolean isOreDrawn()
    {
        for(BlockEdit blockEdit : playereditSet._edits)
        {
            if(Configs.Generic.ONLY_ORE_BLOCK.getBooleanValue() && blockEdit.x == x && blockEdit.y == y && blockEdit.z == z && blockEdit.drawn)
            {
                if(this == blockEdit)
                {
                    return false;
                }

                return true;
            }
        }

        return false;
    }
}