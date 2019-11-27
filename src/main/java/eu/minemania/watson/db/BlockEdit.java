package eu.minemania.watson.db;

import eu.minemania.watson.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;

public class BlockEdit {
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
	
	public BlockEdit(long time, String player, boolean creation, int x, int y, int z, WatsonBlock block, String world) {
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
	
	//TODO later add custom colors
	public void drawOutline(BufferBuilder buffer) {
		IBakedModel model;
		Block blocks = IRegistry.BLOCK.get(new ResourceLocation(block.getName()));
		if(blocks != null) {
			if(!block.getName().equals("minecraft:grass")) {
				IBlockState state = blocks.getDefaultState();
				model = this.blockModelShapes.getModel(state);
				RenderUtils.drawBlockModelOutlinesBatched(model, state, new BlockPos(x, y, z), new Color4f(1f, 0.5f, 0.3f), 0, buffer);
			} else {
				RenderUtils.drawGrassOutlinesBatched(x, y, z, new Color4f(1f, 0.5f, 0.3f), buffer);
			}
		} else {
			EntityType<?> entity = EntityType.getById(block.getName());
			if(entity != null) {
				if(block.getName().equals("item_frame") || block.getName().equals("painting")) {
					RenderUtils.drawItemFramePaintingOutlinesBatched(x, y, z, new Color4f(1f, 0.5f, 0.3f), buffer);
				}
			}
		}
	}
}
