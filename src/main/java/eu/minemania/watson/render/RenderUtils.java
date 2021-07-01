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

    //START TEMP MALILIB
    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void drawBlockBoundingBoxOutlinesBatchedLines(BlockPos pos, Color4f color, double expand, BufferBuilder buffer)
    {
        drawBlockBoundingBoxOutlinesBatchedLines(pos, Vec3d.ZERO, color, expand, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized.
     * The cameraPos value will be subtracted from the absolute coordinate values of the passed in BlockPos.
     * @param pos
     * @param cameraPos
     * @param color
     * @param expand
     * @param buffer
     */
    public static void drawBlockBoundingBoxOutlinesBatchedLines(BlockPos pos, Vec3d cameraPos, Color4f color, double expand, BufferBuilder buffer)
    {
        double minX = pos.getX() - expand - cameraPos.x;
        double minY = pos.getY() - expand - cameraPos.y;
        double minZ = pos.getZ() - expand - cameraPos.z;
        double maxX = pos.getX() + expand - cameraPos.x + 1;
        double maxY = pos.getY() + expand - cameraPos.y + 1;
        double maxZ = pos.getZ() + expand - cameraPos.z + 1;

        drawBoxAllEdgesBatchedLines(minX, minY, minZ, maxX, maxY, maxZ, color, buffer);
    }

    /**
     * Assumes a BufferBuilder in GL_LINES mode has been initialized
     */
    public static void drawBoxAllEdgesBatchedLines(double minX, double minY, double minZ, double maxX, double maxY, double maxZ,
                                                   Color4f color, BufferBuilder buffer)
    {
        // West side
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        // East side
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        // North side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.vertex(maxX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(minX, minY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(minX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(maxX, maxY, minZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        // South side (don't repeat the vertical lines that are done by the east/west sides)
        buffer.vertex(minX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(maxX, minY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();

        buffer.vertex(maxX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
        buffer.vertex(minX, maxY, maxZ).color(color.r, color.g, color.b, color.a).normal(0, 0, 0).next();
    }
    //END TEMP MALILIB

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