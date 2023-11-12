package eu.minemania.watson.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.config.Configs;

public class RegionInfoAnalysis extends Analysis
{
    protected long _lastCommandTime;
    protected Pattern _regionNames = Pattern.compile("[a-zA-Z0-9_-]+");

    public RegionInfoAnalysis()
    {
        addMatchedChatHandler(Configs.Analysis.WG_REGIONS, (chat, m) -> {
            wgRegions(m);
            return true;
        });
    }

    void wgRegions(Matcher m)
    {
        long now = System.currentTimeMillis();
        if (now - _lastCommandTime > (long) (Configs.Plugin.REGION_INFO_TIMEOUT.getDoubleValue() * 1000))
        {
            int regionCount = 0;
            Matcher names = _regionNames.matcher(m.group(1));
            while (names.find())
            {
                ChatMessage.sendToServerChat("region info " + names.group());
                ++regionCount;
            }

            _lastCommandTime = now + (long) (1000 * Configs.Plugin.REGION_INFO_TIMEOUT.getDefaultDoubleValue()) * Math.max(0, regionCount - 1);
        }
    }
}