package eu.minemania.watson.db;

import java.util.Optional;

import eu.minemania.watson.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

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
	private final BlockRenderManager blockModelShapes;
	private MinecraftClient mc;

	public BlockEdit(long time, String player, boolean creation, int x, int y, int z, WatsonBlock block, String world) {
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

	//TODO later add custom colors
	public void drawOutline(BufferBuilder buffer) {
		BakedModel model;
		Block blocks = Registry.BLOCK.get(Identifier.tryParse(block.getName()));
		if(blocks != null && !blocks.getName().asString().equals("Air")) {
			if(!block.getName().equals("minecraft:grass")) {
				BlockState state = blocks.getDefaultState();
				model = this.blockModelShapes.getModel(state);
				RenderUtils.drawBlockModelOutlinesBatched(model, state, new BlockPos(x, y, z), new Color4f(1f, 0.5f, 0.3f), 0, buffer);
			} else {
				RenderUtils.drawGrassOutlinesBatched(x, y, z, new Color4f(1f, 0.5f, 0.3f), buffer);
			}
		} else {
			Optional<EntityType<?>> entity = EntityType.get(block.getName());
			if(entity != null) {
				if(block.getName().equals("minecraft:item_frame") || block.getName().equals("minecraft:painting")) {
					RenderUtils.drawItemFramePaintingOutlinesBatched(x, y, z, new Color4f(1f, 0.5f, 0.3f), buffer);
				}
			}
		}
	}
}
