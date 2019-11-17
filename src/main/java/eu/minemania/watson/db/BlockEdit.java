package eu.minemania.watson.db;

import eu.minemania.watson.render.RenderUtils;
import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.IBakedModel;
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
	public PlayereditSet playereditSet;
	private final BlockModelShapes blockModelShapes;
	
	public BlockEdit(long time, String player, boolean creation, int x, int y, int z, WatsonBlock block) {
		this.time = time;
		this.player = player;
		this.creation = creation;
		this.x = x;
		this.y = y;
		this.z = z;
		this.block = block;
		Minecraft mc = Minecraft.getInstance();
		this.blockModelShapes = mc.getBlockRendererDispatcher().getBlockModelShapes();
	}
	//TODO change render to litematica, litematica has block render already implemented
	public void drawOutline(BufferBuilder buffer) {
		IBakedModel model;
		IBlockState state = (IRegistry.BLOCK.get(new ResourceLocation(block.getName()))).getDefaultState();
		System.out.println("state: "+ state);
		model = this.blockModelShapes.getModel(state);
		//System.out.println("model: "+model);
		System.out.println("x: " + x + ", y: " + y + ", z: " + z);
		RenderUtils.drawBlockModelQuadOverlayBatched(model, state, new BlockPos(x, y, z), new Color4f(255, 0, 0), 0, buffer);
		//block.getBlockModel().render(block, x, y, z);
	}
}
