package eu.minemania.watson.render;

import java.util.List;
import java.util.Random;

import fi.dy.masa.malilib.util.Color4f;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.BlockPos;

public class RenderUtils {
	private static final Random RAND = new Random();
	public static final Direction[] FACING_ALL = new Direction[] { Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST };

	public static void drawGrassOutlinesBatched(float x, float y, float z, Color4f color, BufferBuilder buffer) {
		//Matrix4f matrix4f = matrices.peek().getModel();
		
		buffer.vertex(x, y, z).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x + 1F, y, z).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x, y, z).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x, y, z + 1F).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x, y, z + 1F).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x + 1F, y, z + 1F).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x + 1F, y, z).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x + 1F, y, z + 1F).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x, y + 1F, z).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x + 1F, y + 1F, z).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x, y + 1F, z).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x, y + 1F, z + 1F).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x, y + 1F, z + 1F).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x + 1F, y + 1F, z + 1F).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x + 1F, y + 1F, z).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x + 1F, y + 1F, z + 1F).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x, y, z).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x, y + 1F, z).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x + 1F, y, z).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x + 1F, y + 1F, z).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x, y, z + 1F).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x, y + + 1F, z + 1F).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(x + 1F, y, z + 1F).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(x + 1F, y + 1, z + 1F).color(color.r, color.g, color.b, color.a).next();
	}

	public static void drawItemFramePaintingOutlinesBatched(float x, float y, float z, Color4f color, BufferBuilder buffer) {
		float posX = x + 0.25F / 2;
		float posY = y + 0.25F / 2;
		float posZ = z + 0.5F - Direction.NORTH.getOffsetZ() * 0.46875F;
		float widthX = (12 / 32.0F) * 2;
		float heightY = (12 / 32.0F) * 2;
		float widthZ = (1.0F / 32.0F) * 2;

		buffer.vertex(posX, posY, posZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX + widthX, posY, posZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX + widthX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX, posY, posZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX + widthX, posY, posZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX + widthX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX + widthX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX + widthX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX + widthX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX + widthX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX + widthX, posY, posZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX + widthX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX, posY, posZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX + widthX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX + widthX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(posX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(posX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).next();
	}

	/**
	 * Assumes a BufferBuilder in the GL_LINES mode has been initialized
	 */
	public static void drawBlockModelOutlinesBatched(BakedModel model, BlockState state, BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
	{
		for (final Direction side : FACING_ALL)
		{
			renderModelQuadOutlines(pos, color, model.getQuads(state, side, RAND), buffer);
		}

		renderModelQuadOutlines(pos, color, model.getQuads(state, null, RAND), buffer);
	}

	private static void renderModelQuadOutlines(BlockPos pos, Color4f color, List<BakedQuad> quads, BufferBuilder buffer)
	{
		final int size = quads.size();

		for (int i = 0; i < size; i++)
		{
			renderQuadOutlinesBatched(pos, color, quads.get(i).getVertexData(), buffer);
		}
	}

	private static void renderQuadOutlinesBatched(BlockPos pos, Color4f color, int[] vertexData, BufferBuilder buffer)
	{
		final int x = pos.getX();
		final int y = pos.getY();
		final int z = pos.getZ();
		float fx[] = new float[4];
		float fy[] = new float[4];
		float fz[] = new float[4];

		for (int index = 0; index < 4; ++index)
		{
			fx[index] = x + Float.intBitsToFloat(vertexData[index * 8 + 0]);
			fy[index] = y + Float.intBitsToFloat(vertexData[index * 8 + 1]);
			fz[index] = z + Float.intBitsToFloat(vertexData[index * 8 + 2]);
		}
		//System.out.println(fx[0] + " " + fy[0] + " " + fz[0] + " " + fx[1] + " " + fy[1] + " " + fz[1] + " " + fx[2] + " " + fy[2] + " " + fz[2]);

		buffer.vertex(fx[0], fy[0], fz[0]).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(fx[1], fy[1], fz[1]).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(fx[1], fy[1], fz[1]).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(fx[2], fy[2], fz[2]).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(fx[2], fy[2], fz[2]).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(fx[3], fy[3], fz[3]).color(color.r, color.g, color.b, color.a).next();

		buffer.vertex(fx[3], fy[3], fz[3]).color(color.r, color.g, color.b, color.a).next();
		buffer.vertex(fx[0], fy[0], fz[0]).color(color.r, color.g, color.b, color.a).next();
	}
}
