package eu.minemania.watson.analysis;

import java.util.Locale;
import java.util.regex.Matcher;

import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.chat.Color;
import eu.minemania.watson.chat.IMatchedChatHandler;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.TimeStamp;
import eu.minemania.watson.db.WatsonBlock;
import eu.minemania.watson.db.WatsonBlockRegistery;
import eu.minemania.watson.scheduler.SyncTaskQueue;
import eu.minemania.watson.scheduler.tasks.AddBlockEditTask;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

public class LbCoordsAnalysis extends Analysis
{
    protected static final TextFormatting _COLOUR_CYCLE[] = {Color.red.getColor(), Color.gold.getColor(), Color.yellow.getColor(), Color.green.getColor(), Color.aqua.getColor(), Color.darkpurple.getColor(), Color.lightpurple.getColor()};
    protected int _colourIndex = _COLOUR_CYCLE.length - 1;
    protected static final float _COLOUR_PROXIMITY_LIMIT = 4.0f;
    protected int _lastX, _lastY, _lastZ;
    protected int _currentPage = 0;
    protected int _pageCount = 0;
    protected String _world; 

    public LbCoordsAnalysis()
    {
        addMatchedChatHandler(Configs.Analysis.LB_COORD_POSITION, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(ITextComponent chat, Matcher m)
            {
                lbCoordPosition(chat, m);
                return false;
            }
        });

        addMatchedChatHandler(Configs.Analysis.LB_COORD, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(ITextComponent chat, Matcher m)
            {
                lbCoord(chat, m);
                return false;
            }
        });

        addMatchedChatHandler(Configs.Analysis.LB_KILLS, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(ITextComponent chat, Matcher m)
            {
                lbKills(chat, m);
                return true;
            }
        });

        addMatchedChatHandler(Configs.Analysis.LB_COORD_KILLS, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(ITextComponent chat, Matcher m)
            {
                lbCoordKills(chat, m);
                return false;
            }
        });

        addMatchedChatHandler(Configs.Analysis.LB_COORD_REPLACED, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(ITextComponent chat, Matcher m)
            {
                lbCoordReplaced(chat, m);
                return false;
            }
        });

        addMatchedChatHandler(Configs.Analysis.LB_PAGE, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(ITextComponent chat, Matcher m)
            {
                lbPage(chat, m);
                return true;
            }
        });

        IMatchedChatHandler headerHandler = new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(ITextComponent chat, Matcher m)
            {
                lbHeader(chat, m);
                return true;
            }
        };

        addMatchedChatHandler(Configs.Analysis.LB_HEADER_NO_RESULTS, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_CHANGES, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_BLOCKS, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_SUM_BLOCKS, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_SUM_PLAYERS, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_SEARCHING, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_RATIO, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_RATIO_CURRENT, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_TIME_CHECK, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_BLOCK, headerHandler);
    }

    void lbCoord(ITextComponent chat, Matcher m)
    {
        try
        {
            int index = Integer.parseInt(m.group(1));
            int[] ymd = TimeStamp.parseYMD(m.group(2));
            int hour = Integer.parseInt(m.group(3));
            int minute = Integer.parseInt(m.group(4));
            int second = Integer.parseInt(m.group(5));
            long millis = TimeStamp.toMillis(ymd, hour, minute, second);

            String player = m.group(6);
            String action = m.group(7);
            String block = m.group(8);

            String sign1 = null, sign2 = null, sign3 = null, sign4 = null;
            int x, y, z;
            if (m.groupCount() == 15)
            {
                sign1 = m.group(9);
                sign2 = m.group(10);
                sign3 = m.group(11);
                sign4 = m.group(12);
                x = Integer.parseInt(m.group(13));
                y = Integer.parseInt(m.group(14));
                z = Integer.parseInt(m.group(15));
            }
            else
            {
                x = Integer.parseInt(m.group(9));
                y = Integer.parseInt(m.group(10));
                z = Integer.parseInt(m.group(11));
            }
            WatsonBlock type = WatsonBlockRegistery.getInstance().getWatsonBlockByName(block);
            boolean created = action.equals("created");
            BlockEdit edit = new BlockEdit(millis, player, created, x, y, z, type, _world);
            SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, true));

            TextFormatting color = Configs.Generic.RECOLOR_QUERY_RESULTS.getBooleanValue() ? getChatColorFormat(x, y, z) : null;
            if (Configs.Generic.REFORMAT_QUERY_RESULTS.getBooleanValue())
            {
                if (!type.getName().equals("minecraft:stone"))
                {
                    String signText = (sign1 != null) ? String.format(Locale.US, " [%s] [%s] [%s] [%s]", sign1, sign2, sign3, sign4) : "";

                    String year = (ymd[0] != 0) ? String.format(Locale.US, "%02d-", ymd[0]) : "";
                    String output = String.format(Locale.US, "(%2d) %s%02d-%02d %02d:%02d:%02d (%d,%d,%d) %C%s %s%s", index, year, ymd[1], ymd[2], hour, minute, second, x, y, z, (created ? '+' : '-'), type.getName(), player, signText);
                    ChatMessage.sendToLocalChat(color, null, output, true);
                }
            }
            else
            {
                if (Configs.Generic.RECOLOR_QUERY_RESULTS.getBooleanValue())
                {
                    ChatMessage.sendToLocalChat(color, null, chat.getUnformattedComponentText(), true);
                }
                else
                {
                    ChatMessage.sendToLocalChat(chat, true);
                }
            }

            requestNextPage();
        }
        catch (Exception ex)
        {
            Watson.logger.info("error parsing lb coords", ex);
        }
    }

    void lbKills(ITextComponent chat, Matcher m)
    {
        _world = m.group(1);
    }

    void lbCoordPosition(ITextComponent chat, Matcher m)
    {
        _world = m.group(1);
    }

    void lbCoordKills(ITextComponent chat, Matcher m)
    {
        try
        {
            int index = Integer.parseInt(m.group(1));
            int[] ymd = TimeStamp.parseYMD(m.group(2));
            int hour = Integer.parseInt(m.group(3));
            int minute = Integer.parseInt(m.group(4));
            int second = Integer.parseInt(m.group(5));
            long millis = TimeStamp.toMillis(ymd, hour, minute, second);

            String player = m.group(6);
            String victim = m.group(7);

            int x = Integer.parseInt(m.group(8));
            int y = Integer.parseInt(m.group(9));
            int z = Integer.parseInt(m.group(10));
            String weapon = m.group(11);

            WatsonBlock type = WatsonBlockRegistery.getInstance().getBlockKillTypeByName(victim);
            BlockEdit edit = new BlockEdit(millis, player, false, x, y, z, type, _world);
            SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, true));

            TextFormatting color = Configs.Generic.RECOLOR_QUERY_RESULTS.getBooleanValue() ? getChatColorFormat(x, y, z) : null;
            if (Configs.Generic.REFORMAT_QUERY_RESULTS.getBooleanValue())
            {

                if (!type.getName().equals("minecraft:stone"))
                {
                    String year = (ymd[0] != 0) ? String.format(Locale.US, "%02d-", ymd[0]) : "";
                    String output = String.format(Locale.US, "(%2d) %s%02d-%02d %02d:%02d:%02d (%d,%d,%d) %s %s %s > %s", index, year, ymd[1], ymd[2], hour, minute, second, x, y, z, _world, player, weapon, victim);
                    ChatMessage.sendToLocalChat(output, true);
                }
            }
            else
            {
                if (Configs.Generic.RECOLOR_QUERY_RESULTS.getBooleanValue())
                {
                    ChatMessage.sendToLocalChat(color, null, chat.getUnformattedComponentText(), true);
                }
                else
                {
                    ChatMessage.sendToLocalChat(chat, true);
                }
            }

            requestNextPage();
        }
        catch (Exception ex)
        {
            Watson.logger.info("error parsing lb kills coords", ex);
        }
    }

    void lbCoordReplaced(ITextComponent chat, Matcher m)
    {
        try
        {
            int index = Integer.parseInt(m.group(1));
            int[] ymd = TimeStamp.parseYMD(m.group(2));
            int hour = Integer.parseInt(m.group(3));
            int minute = Integer.parseInt(m.group(4));
            int second = Integer.parseInt(m.group(5));
            long millis = TimeStamp.toMillis(ymd, hour, minute, second);

            String player = m.group(6);
            String oldBlock = m.group(7);
            String newBlock = m.group(8);
            int x = Integer.parseInt(m.group(9));
            int y = Integer.parseInt(m.group(10));
            int z = Integer.parseInt(m.group(11));
            WatsonBlock type = WatsonBlockRegistery.getInstance().getWatsonBlockByName(oldBlock);
            WatsonBlock newtype = WatsonBlockRegistery.getInstance().getWatsonBlockByName(newBlock);
            BlockEdit edit = new BlockEdit(millis, player, false, x, y, z, type, _world);
            SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, true));

            TextFormatting color = Configs.Generic.RECOLOR_QUERY_RESULTS.getBooleanValue() ? getChatColorFormat(x, y, z) : null;
            if (Configs.Generic.REFORMAT_QUERY_RESULTS.getBooleanValue())
            {
                if (!type.getName().equals("minecraft:stone"))
                {
                    String year = (ymd[0] != 0) ? String.format(Locale.US, "%02d-", ymd[0]) : "";

                    String output = String.format(Locale.US, "(%2d) %s%02d-%02d %02d:%02d:%02d (%d,%d,%d) %C%s %C%s %s", index, year, ymd[1], ymd[2], hour, minute, second, x, y, z, '-', type.getName(), '+', newtype.getName(), player);
                    ChatMessage.sendToLocalChat(output, true);
                }
            }
            else
            {
                if (Configs.Generic.RECOLOR_QUERY_RESULTS.getBooleanValue())
                {
                    ChatMessage.sendToLocalChat(color, null, chat.getUnformattedComponentText(), true);
                }
                else
                {
                    ChatMessage.sendToLocalChat(chat, true);
                }
            }
            requestNextPage();
        }
        catch (Exception ex)
        {
        }
    }

    void lbPage(ITextComponent chat, Matcher m)
    {
        int currentPage = Integer.parseInt(m.group(1));
        int pageCount = Integer.parseInt(m.group(2));

        if (pageCount <= Configs.Generic.MAX_AUTO_PAGES.getIntegerValue())
        {
            _currentPage = currentPage;
            _pageCount = pageCount;
        }
        else
        {
            _currentPage = _pageCount = 0;
        }
    }

    void lbHeader(ITextComponent chat, Matcher m)
    {
        _currentPage = _pageCount = 0;
    }

    private void requestNextPage()
    {
        if (Configs.Generic.AUTO_PAGE.getBooleanValue())
        {
            if (_currentPage != 0 && _currentPage < _pageCount && _pageCount <= Configs.Generic.MAX_AUTO_PAGES.getIntegerValue())
            {
                ChatMessage.sendToServerChat(String.format(Locale.US, "/lb page %d", _currentPage + 1));

                _currentPage = _pageCount = 0;
            }
        }
    }

    private TextFormatting getChatColorFormat(int x, int y, int z)
    {
        int dx = x - _lastX;
        int dy = y - _lastY;
        int dz = z - _lastZ;

        float distance = dx * dx + dy * dy + dz * dz;
        if (distance > _COLOUR_PROXIMITY_LIMIT * _COLOUR_PROXIMITY_LIMIT)
        {
            _colourIndex = (_colourIndex + 1) % _COLOUR_CYCLE.length;
        }
        _lastX = x;
        _lastY = y;
        _lastZ = z;

        return _COLOUR_CYCLE[_colourIndex];
    }
}