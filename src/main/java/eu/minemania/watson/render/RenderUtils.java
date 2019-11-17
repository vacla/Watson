package eu.minemania.watson.render;

import java.util.List;
import java.util.Random;

import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class RenderUtils {
	private static final Random RAND = new Random();
	public static final EnumFacing[] FACING_ALL = new EnumFacing[] { EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST };
	/**
     * Assumes a BufferBuilder in the GL_LINES mode has been initialized
     */
	public static void drawBlockModelQuadOverlayBatched(IBakedModel model, IBlockState state, BlockPos pos, Color4f color, double expand, BufferBuilder buffer) {
        for (final EnumFacing side : FACING_ALL) {
            renderModelQuadOverlayBatched(pos, buffer, color, model.getQuads(state, side, RAND));
        }

        renderModelQuadOverlayBatched(pos, buffer, color, model.getQuads(state, null, RAND));
    }

    public static void drawBlockModelQuadOverlayBatched(IBakedModel model, IBlockState state, BlockPos pos, EnumFacing side, Color4f color, double expand, BufferBuilder buffer) {
        renderModelQuadOverlayBatched(pos, buffer, color, model.getQuads(state, side, RAND));
    }

    private static void renderModelQuadOverlayBatched(BlockPos pos, BufferBuilder buffer, Color4f color, List<BakedQuad> quads) {
        final int size = quads.size();

        for (int i = 0; i < size; i++)
        {
            renderModelQuadOverlayBatched(pos, buffer, color, quads.get(i).getVertexData());
        }
    }

    private static void renderModelQuadOverlayBatched(BlockPos pos, BufferBuilder buffer, Color4f color, int[] vertexData) {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        float fx, fy, fz;

        for (int index = 0; index < 4; ++index) {
            fx = x + Float.intBitsToFloat(vertexData[index * 7 + 0]);
            fy = y + Float.intBitsToFloat(vertexData[index * 7 + 1]);
            fz = z + Float.intBitsToFloat(vertexData[index * 7 + 2]);
            System.out.println("fx: " + fx + ", fy: " + fy + ", fz: " + fz);
            System.out.println("r: "+ color.r + ", g:"+ color.g + ", b:"+ color.b + ", a:"+ color.a);
            buffer.pos(fx, fy, fz).color(color.r, color.g, color.b, color.a).endVertex();
        }
    }
}
