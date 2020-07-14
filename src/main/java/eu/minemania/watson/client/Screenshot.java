package eu.minemania.watson.client;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import net.minecraft.util.Formatting;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.text.ClickEvent;

public class Screenshot
{

    /**
     * Makes screenshot and custom directory.
     */
    public static void makeScreenshot()
    {
        Date now = new Date();
        String player2 = (String) DataManager.getEditSelection().getVariables().get("player");
        String subdirectoryName = (!player2.isEmpty() && Configs.Generic.SS_PLAYER_DIRECTORY.getBooleanValue()) ? player2 : new SimpleDateFormat(Configs.Generic.SS_DATE_DIRECTORY.getStringValue()).format(now);
        MinecraftClient mc = MinecraftClient.getInstance();
        File screenshotsDir = new File(mc.runDirectory, "screenshots");
        File subdirectory = new File(screenshotsDir, subdirectoryName);
        File file = Screenshot.getUniqueFilename(subdirectory, player2, now);
        mc.inGameHud.getChatHud().addMessage(Screenshot.save(file, mc.getWindow().getWidth(), mc.getWindow().getHeight()));
    }

    /**
     * Returns if screenshot is saved or not.
     *
     * @param file   Sets filename
     * @param width  Sets width
     * @param height Sets height
     * @return TextComponentTranslation of screenshot save
     */
    public static Text save(File file, int width, int height)
    {
        try
        {
            file.getParentFile().mkdirs();

            ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
            GL11.glReadBuffer(GL11.GL_FRONT);
            // GL11.glReadBuffer() unexpectedly sets an error state (invalid enum).
            GL11.glGetError();
            GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++)
            {
                for (int y = 0; y < height; y++)
                {
                    int i = (x + width * y) * 4;
                    int r = buffer.get(i) & 0xFF;
                    int g = buffer.get(i + 1) & 0xFF;
                    int b = buffer.get(i + 2) & 0xFF;
                    image.setRGB(x, (height - 1) - y, (0xFF << 24) | (r << 16) | (g << 8) | b);
                }
            }

            ImageIO.write(image, "png", file);
            LiteralText text = new LiteralText(file.getName());
            text.getStyle().withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()));
            text.getStyle().withFormatting(Formatting.UNDERLINE);
            return new TranslatableText("screenshot.success", text);
        }
        catch (Exception ex)
        {
            return new TranslatableText("screenshot.failure", ex.getMessage());
        }
    }

    /**
     * Returns unique file name for screenshot, adds username if filled in.
     *
     * @param dir    Directory gets saved
     * @param player Username
     * @param now    Current date
     * @return Unique PNG file name for screenshot
     */
    public static File getUniqueFilename(File dir, String player, Date now)
    {
        String baseName = _DATE_FORMAT.format(now);

        int count = 1;
        String playerSuffix = (player.isEmpty() || !Configs.Generic.SS_PLAYER_SUFFIX.getBooleanValue()) ? "" : "-" + player;
        while (true)
        {
            File result = new File(dir, baseName + playerSuffix + (count == 1 ? "" : "-" + count) + ".png");
            if (!result.exists())
            {
                return result;
            }
            ++count;
        }
    }

    // --------------------------------------------------------------------------
    /**
     * Used to format dates for making screenshot filenames.
     */
    private static final DateFormat _DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
}