package eu.minemania.watson.client;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import eu.minemania.watson.Watson;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;

import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import net.minecraft.client.MinecraftClient;

public class Screenshot
{

    /**
     * Makes screenshot and custom directory.
     */
    public static void makeScreenshot()
    {
        Date now = new Date();
        String player2 = (String) DataManager.getEditSelection().getVariables().get("player");
        String subdirectoryName = (player2 != null && !player2.isEmpty() && Configs.Generic.SS_PLAYER_DIRECTORY.getBooleanValue()) ? player2 : new SimpleDateFormat(Configs.Generic.SS_DATE_DIRECTORY.getStringValue()).format(now);
        subdirectoryName = subdirectoryName.replaceAll(":", "-").replaceAll(" ", "-");
        MinecraftClient mc = MinecraftClient.getInstance();
        File screenshotsDir = new File(mc.runDirectory, "screenshots");
        File subdirectory = new File(screenshotsDir, subdirectoryName);
        File file = Screenshot.getUniqueFilename(subdirectory, player2, now);
        Screenshot.save(file, mc);
    }

    public static void save(File file, MinecraftClient mc)
    {
        NativeImage nativeImage = ScreenshotRecorder.takeScreenshot(mc.getFramebuffer());
        Util.getIoWorkerExecutor().execute(() -> {
            try {
                nativeImage.writeTo(file);
                MutableText text = Text.literal(file.getName()).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath())));
                mc.inGameHud.getChatHud().addMessage(Text.translatable("screenshot.success", text));
            }
            catch (Exception text) {
                Watson.logger.warn("Couldn't save screenshot", (Throwable)text);
                mc.inGameHud.getChatHud().addMessage(Text.translatable("screenshot.failure", text.getMessage()));
            }
            finally {
                nativeImage.close();
            }
        });
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
        String playerSuffix = (player == null || player.isEmpty() || !Configs.Generic.SS_PLAYER_SUFFIX.getBooleanValue()) ? "" : "-" + player;
        while (true)
        {
            File result = new File(dir, baseName + playerSuffix + (count == 1 ? "" : "-" + count) + ".png");
            if (!result.exists())
            {
                dir.mkdir();
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