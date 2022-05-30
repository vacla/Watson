package eu.minemania.watson.analysis;

import java.util.Calendar;
import java.util.HashMap;

import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.config.Plugins;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.TimeStamp;
import fi.dy.masa.malilib.overlay.message.MessageDispatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public class ServerTime extends Analysis
{
    protected static final int MINUTES_TO_MILLISECONDS = 60 * 1000;
    protected HashMap<String, Integer> _localMinusServerMinutes = new HashMap<>();
    protected boolean _showServerTime = false;
    private static final ServerTime INSTANCE = new ServerTime();

    public static ServerTime getInstance()
    {
        return INSTANCE;
    }

    public static Integer getServerTime(String serverip)
    {
        return getInstance()._localMinusServerMinutes.get(serverip);
    }

    public static void putServerTime(String serverip, int servertime)
    {
        getInstance()._localMinusServerMinutes.put(serverip, servertime);
    }

    public static boolean showServerTime()
    {
        return getInstance()._showServerTime;
    }

    public static void toggleServerTime()
    {
        getInstance()._showServerTime = !getInstance()._showServerTime;
    }

    public int getLocalMinusServerMinutes()
    {
        String serverIP = DataManager.getServerIP();
        if (serverIP == null)
        {
            return 0;
        }
        else
        {
            Integer offsetMinutes = _localMinusServerMinutes.get(serverIP);
            return offsetMinutes != null ? offsetMinutes : 0;
        }
    }

    public void queryServerTime(boolean showServerTime)
    {
        String serverIP = DataManager.getServerIP();
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (serverIP != null && player != null)
        {
            if (_localMinusServerMinutes.get(serverIP) == null)
            {
                if (Configs.Plugin.PLUGIN.getValue().equals(Plugins.LOGBLOCK))
                {
                    Calendar pastTime = getPastTime();
                    String date = String.format("%d.%d.%d", pastTime.get(Calendar.DAY_OF_MONTH), pastTime.get(Calendar.MONTH) + 1, pastTime.get(Calendar.YEAR));
                    String query = String.format("/lb player %s since %s 00:00:00 before %s 00:00:01 limit 1", player.getName().getString(), date, date);
                    if (Configs.Generic.DEBUG.getBooleanValue())
                    {
                        Watson.logger.info("Server time query for " + serverIP + ": " + query);
                    }
                    _showServerTime = showServerTime;
                    ChatMessage.sendToServerChat(query);
                }
                else
                {
                    MessageDispatcher.generic("watson.message.info.no_logblock");
                }
            }
            else if (showServerTime)
            {
                showCurrentServerTime();
            }
        }
    }

    public void showCurrentServerTime()
    {
        String serverIP = DataManager.getServerIP();
        Integer localMinusServerMinutes = _localMinusServerMinutes.get(serverIP);
        long serverMillis = System.currentTimeMillis() - localMinusServerMinutes * MINUTES_TO_MILLISECONDS;
        ChatMessage.localOutput(TimeStamp.formatMonthDayTime(serverMillis), true);
    }

    public Calendar getPastTime()
    {
        Calendar pastTime = Calendar.getInstance();
        pastTime.add(Calendar.DAY_OF_MONTH, -2);
        pastTime.add(Calendar.HOUR_OF_DAY, 0);
        pastTime.add(Calendar.MINUTE, 0);
        return pastTime;
    }
}