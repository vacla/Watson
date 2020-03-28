package eu.minemania.watson.db;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;

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
    private final BlockModelShapes blockModelShapes;
    private Minecraft mc;
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
        this.mc = Minecraft.getInstance();
        this.blockModelShapes = this.mc.getBlockRendererDispatcher().getBlockModelShapes();
    }

    public void drawOutline(BufferBuilder buffer)
    {
        Block blocks = IRegistry.BLOCK.get(new ResourceLocation(block.getName()));
        float lineWidth = block.getLineWidth();
        if(blocks != null && !blocks.getTranslationKey().equals("Air"))
        {
            if(Configs.Generic.ORE_OUTLINE_THICKER.getBooleanValue() && blocks instanceof BlockOre)
            {
                lineWidth = Configs.Generic.ORE_LINEWIDTH.getIntegerValue();
            }
            GlStateManager.lineWidth(lineWidth);
            renderBlocks(buffer, blocks);

        }
        else
        {
            GlStateManager.lineWidth(lineWidth);
            EntityType<?> entity = EntityType.getById(block.getName());
            renderEntities(buffer, entity);
        }
    }

    private void renderBlocks(BufferBuilder buffer, Block blocks)
    {
        if(!block.getName().equals("minecraft:grass"))
        {
            IBlockState state = blocks.getDefaultState();
            IBakedModel model = this.blockModelShapes.getModel(state);
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
            if(!drawn && blocks instanceof BlockOre)
            {
                drawn = true;
            }
        }
        else
        {
            RenderUtils.drawGrassOutlinesBatched(x, y, z, block.getColor(), buffer);
        }
    }

    private void renderEntities(BufferBuilder buffer, EntityType<?> entity)
    {
        if(entity != null)
        {
            if(block.getName().equals("minecraft:item_frame") || block.getName().equals("minecraft:painting"))
            {
                RenderUtils.drawItemFramePaintingOutlinesBatched(x, y, z, block.getColor(), buffer);
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