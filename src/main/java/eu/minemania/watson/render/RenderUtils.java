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
	
	public static void drawItemFrameOutlinesBatched(double x, double y, double z, Color4f color, BufferBuilder buffer) {
		double posX = x + 0.25D / 2;
		double posY = y + 0.25D / 2;
		double posZ = z + 0.5D - (double)EnumFacing.NORTH.getZOffset() * 0.46875D;
		double widthX = (12 / 32.0D) * 2;
		double heightY = (12 / 32.0D) * 2;
		double widthZ = (1.0D / 32.0D) * 2;
		
		buffer.pos(posX, posY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX + widthX, posY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX + widthX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX, posY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX + widthX, posY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX + widthX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX + widthX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX + widthX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX + widthX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX + widthX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX + widthX, posY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX + widthX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX, posY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX + widthX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX + widthX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
		
		buffer.pos(posX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).endVertex();
		buffer.pos(posX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).endVertex();
	}
	
	/**
     * Assumes a BufferBuilder in the GL_LINES mode has been initialized
     */
    public static void drawBlockModelOutlinesBatched(IBakedModel model, IBlockState state, BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        for (final EnumFacing side : FACING_ALL)
        {
            renderModelQuadOutlines(pos, buffer, color, model.getQuads(state, side, RAND));
        }

        renderModelQuadOutlines(pos, buffer, color, model.getQuads(state, null, RAND));
    }

    private static void renderModelQuadOutlines(BlockPos pos, BufferBuilder buffer, Color4f color, List<BakedQuad> quads)
    {
        final int size = quads.size();

        for (int i = 0; i < size; i++)
        {
            renderQuadOutlinesBatched(pos, buffer, color, quads.get(i).getVertexData());
        }
    }

    private static void renderQuadOutlinesBatched(BlockPos pos, BufferBuilder buffer, Color4f color, int[] vertexData)
    {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        float fx[] = new float[4];
        float fy[] = new float[4];
        float fz[] = new float[4];

        for (int index = 0; index < 4; ++index)
        {
            fx[index] = x + Float.intBitsToFloat(vertexData[index * 7 + 0]);
            fy[index] = y + Float.intBitsToFloat(vertexData[index * 7 + 1]);
            fz[index] = z + Float.intBitsToFloat(vertexData[index * 7 + 2]);
        }

        buffer.pos(fx[0], fy[0], fz[0]).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(fx[1], fy[1], fz[1]).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(fx[1], fy[1], fz[1]).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(fx[2], fy[2], fz[2]).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(fx[2], fy[2], fz[2]).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(fx[3], fy[3], fz[3]).color(color.r, color.g, color.b, color.a).endVertex();

        buffer.pos(fx[3], fy[3], fz[3]).color(color.r, color.g, color.b, color.a).endVertex();
        buffer.pos(fx[0], fy[0], fz[0]).color(color.r, color.g, color.b, color.a).endVertex();
    }
}
