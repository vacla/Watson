package eu.minemania.watson.render;

import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;
import eu.minemania.watson.db.WatsonBlock;
import fi.dy.masa.malilib.util.Color4f;
import fi.dy.masa.malilib.util.PositionUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.*;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.util.math.*;

public class RenderUtils
{
    private static final Random RAND = new Random();

    public static void startDrawingLines(BufferBuilder buffer)
    {
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesShader);
        buffer.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
    }

    public static void drawFullBlockOutlinesBatched(float x, float y, float z, Color4f color, BufferBuilder buffer)
    {
        buffer.vertex(x, y, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x + 1F, y, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x, y, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x, y, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x, y, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x + 1F, y, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x + 1F, y, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x + 1F, y, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x, y + 1F, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x + 1F, y + 1F, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x, y + 1F, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x, y + 1F, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x, y + 1F, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x + 1F, y + 1F, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x + 1F, y + 1F, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x + 1F, y + 1F, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x, y, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x, y + 1F, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x + 1F, y, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x + 1F, y + 1F, z).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x, y, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x, y + +1F, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(x + 1F, y, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(x + 1F, y + 1, z + 1F).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
    }

    public static void drawSpecialOutlinesBatched(float x, float y, float z, WatsonBlock watsonBlock, BufferBuilder buffer, boolean sign)
    {
        float posX = x + 0.25F / 2;
        float posY = y + 0.25F / 2;
        float posZ = z + 0.015F;
        float widthX = (12 / 32.0F) * 2;
        float heightY = (12 / 32.0F) * 2;
        float widthZ = (1.0F / 32.0F) * 2;
        Color4f color = watsonBlock.getColor();

        if (sign)
        {
            posX = posX - 0.1F;
            posY = posY + 0.2F;
            widthX = widthX + 0.2F;
            heightY = heightY - 0.3F;
        }

        buffer.vertex(posX, posY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX + widthX, posY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX + widthX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX, posY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX + widthX, posY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX + widthX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX + widthX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX + widthX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX + widthX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX + widthX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX + widthX, posY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX + widthX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX, posY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX, posY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX + widthX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX + widthX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(posX, posY + heightY, posZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(posX, posY + heightY, posZ + widthZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
    }

    /**
     * Assumes a BufferBuilder in the GL_LINES mode has been initialized
     */
    public static void drawBlockModelOutlinesBatched(BakedModel model, BlockState state, BlockPos pos, Color4f color, BufferBuilder buffer)
    {
        for (final Direction side : PositionUtils.ALL_DIRECTIONS)
        {
            renderModelQuadOutlines(pos, color, model.getQuads(state, side, RAND), buffer);
        }

        renderModelQuadOutlines(pos, color, model.getQuads(state, null, RAND), buffer);
    }

    private static void renderModelQuadOutlines(BlockPos pos, Color4f color, List<BakedQuad> quads, BufferBuilder buffer)
    {
        for (BakedQuad quad : quads)
        {
            renderQuadOutlinesBatched(pos, color, quad, buffer);
        }
    }

    private static void renderQuadOutlinesBatched(BlockPos pos, Color4f color, BakedQuad quad, BufferBuilder buffer)
    {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        int[] vertexData = quad.getVertexData();
        Vec3i vec3i = quad.getFace().getVector();
        Vec3f vec3f = new Vec3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
        final int vertexSize = vertexData.length / 4;
        float[] fx = new float[4];
        float[] fy = new float[4];
        float[] fz = new float[4];

        for (int index = 0; index < 4; ++index)
        {
            fx[index] = x + Float.intBitsToFloat(vertexData[index * vertexSize]);
            fy[index] = y + Float.intBitsToFloat(vertexData[index * vertexSize + 1]);
            fz[index] = z + Float.intBitsToFloat(vertexData[index * vertexSize + 2]);
        }

        buffer.vertex(fx[0], fy[0], fz[0]).color(color.r, color.g, color.b, color.a).normal(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next();
        buffer.vertex(fx[1], fy[1], fz[1]).color(color.r, color.g, color.b, color.a).normal(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next();

        buffer.vertex(fx[1], fy[1], fz[1]).color(color.r, color.g, color.b, color.a).normal(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next();
        buffer.vertex(fx[2], fy[2], fz[2]).color(color.r, color.g, color.b, color.a).normal(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next();

        buffer.vertex(fx[2], fy[2], fz[2]).color(color.r, color.g, color.b, color.a).normal(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next();
        buffer.vertex(fx[3], fy[3], fz[3]).color(color.r, color.g, color.b, color.a).normal(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next();

        buffer.vertex(fx[3], fy[3], fz[3]).color(color.r, color.g, color.b, color.a).normal(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next();
        buffer.vertex(fx[0], fy[0], fz[0]).color(color.r, color.g, color.b, color.a).normal(vec3f.getX(), vec3f.getY(), vec3f.getZ()).next();
    }
}