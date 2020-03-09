package eu.minemania.watson.render;

import java.util.Arrays;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import fi.dy.masa.malilib.render.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class OverlayRenderer
{
    private static long loginTime;
    private static boolean canRender;

    // https://stackoverflow.com/questions/470690/how-to-automatically-generate-n-distinct-colors
    public static final int[] KELLY_COLORS = {
            0xFFB300,    // Vivid Yellow
            0x803E75,    // Strong Purple
            0xFF6800,    // Vivid Orange
            0xA6BDD7,    // Very Light Blue
            0xC10020,    // Vivid Red
            0xCEA262,    // Grayish Yellow
            0x817066,    // Medium Gray
            // The following don't work well for people with defective color vision
            0x007D34,    // Vivid Green
            0xF6768E,    // Strong Purplish Pink
            0x00538A,    // Strong Blue
            0xFF7A5C,    // Strong Yellowish Pink
            0x53377A,    // Strong Violet
            0xFF8E00,    // Vivid Orange Yellow
            0xB32851,    // Strong Purplish Red
            0xF4C800,    // Vivid Greenish Yellow
            0x7F180D,    // Strong Reddish Brown
            0x93AA00,    // Vivid Yellowish Green
            0x593315,    // Deep Yellowish Brown
            0xF13A13,    // Vivid Reddish Orange
            0x232C16     // Dark Olive Green
    };

    public static void resetRenderTimeout()
    {
        canRender = false;
        loginTime = System.currentTimeMillis();
    }

    public static void renderOverlays(MinecraftClient mc, float partialTicks)
    {
        Entity entity = mc.getCameraEntity();

        if (canRender == false){
            // Don't render before the player has been placed in the actual proper position,
            // otherwise some of the renderers mess up.
            // The magic 8.5, 65, 8.5 comes from the WorldClient constructor
            if (System.currentTimeMillis() - loginTime >= 5000 || entity.x != 8.5 || entity.y != 65 || entity.z != 8.5)
            {
                canRender = true;
            }
            else
            {
                return;
            }
        }

        Vec3d cameraPos = mc.gameRenderer.getCamera().getPos();

        DataManager.getEditSelection().getBlockEditSet().getOreDB().drawDepositLabels(cameraPos.x, cameraPos.y, cameraPos.z);
        DataManager.getEditSelection().getBlockEditSet().drawAnnotations(cameraPos.x, cameraPos.y, cameraPos.z);
    }

    public static void drawBillboard(double x, double y, double z, double scale, String text)
    {
        final float scaled = MathHelper.clamp((float) scale, 0.01f, 1f);
        MinecraftClient mc = MinecraftClient.getInstance();
        Entity entity = mc.getCameraEntity();
        if(entity != null)
        {
            RenderUtils.drawTextPlate(Arrays.asList(text), x, y, z, entity.yaw, entity.pitch, scaled, Configs.Generic.BILLBOARD_FOREGROUND.getIntegerValue(), Configs.Generic.BILLBOARD_BACKGROUND.getIntegerValue(), true);
        }
    }
}