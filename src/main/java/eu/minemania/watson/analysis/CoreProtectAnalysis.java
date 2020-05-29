package eu.minemania.watson.analysis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.chat.IMatchedChatHandler;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.TimeStamp;
import eu.minemania.watson.db.WatsonBlock;
import eu.minemania.watson.db.WatsonBlockRegistery;
import eu.minemania.watson.scheduler.SyncTaskQueue;
import eu.minemania.watson.scheduler.tasks.AddBlockEditTask;
import eu.minemania.watson.selection.EditSelection;
import fi.dy.masa.malilib.util.InfoUtils;
import net.minecraft.text.Text;

//----------------------------------------------------------------------------
/**
 * An {@link Analysis} implementation that recognises inspector and lookup
 * results from CoreProtect.
 * 
 * CoreProtect inspector results look like this:
 * 
 * <pre>
 * ----- CoreProtect ----- (x2/y63/z-6)
 * 0.00/h ago - totemo placed #4 (Cobblestone).
 * 1.36/h ago - totemo removed #4 (Cobblestone).
 * </pre>
 * 
 * Lookup results look like this:
 * 
 * <pre>
 * ----- CoreProtect Lookup Results -----
 * 0.01/h ago - §3totemo removed #4 (Cobblestone).
 *                 ^ (x3/y63/z-7/world)
 * 0.01/h ago - totemo placed #4 (Cobblestone).
 *                 ^ (x3/y63/z-6/world)
 * </pre>
 */
public class CoreProtectAnalysis extends Analysis
{
    protected static final int MS_PER_HOUR = 60 * 60 * 1000;
    protected static final Pattern ABSOLUTE_TIME = Pattern.compile("(\\d{1,2})-(\\d{1,2}) (\\d{1,2}):(\\d{2}):(\\d{2})");
    protected static final Pattern HOURS_AGO_TIME = Pattern.compile("(\\d+.\\d+)/h ago");
    protected boolean _isLookup = false;
    protected boolean _firstInspectorResult = false;
    protected boolean _lookupDetails = false;
    protected String _action;
    protected int _x;
    protected int _y;
    protected int _z;
    protected String _world;
    protected long _millis;
    protected String _player;
    protected WatsonBlock _block;
    protected int _loop;
    protected static int _currentPage = 0;
    protected static int _pageCount = 0;
    protected static boolean _looping;

    public CoreProtectAnalysis()
    {
        addMatchedChatHandler(Configs.Analysis.CP_BUSY, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                cpBusy(chat, m);
                return sendMessage();
            }
        });
        addMatchedChatHandler(Configs.Analysis.CP_DETAILS, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                details(chat, m);
                return sendMessage();
            }
        });
        addMatchedChatHandler(Configs.Analysis.CP_INSPECTOR_COORDS, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                inspectorCoords(chat, m);
                return true;
            }
        });
        addMatchedChatHandler(Configs.Analysis.CP_LOOKUP_COORDS, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                lookupCoords(chat, m);
                return sendMessage();
            }
        });
        addMatchedChatHandler(Configs.Analysis.CP_LOOKUP_HEADER, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                lookupHeader(chat, m);
                return sendMessage();
            }
        });
        addMatchedChatHandler(Configs.Analysis.CP_NO_RESULT, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                cpNoResult(chat, m);
                return true;
            }
        });
        addMatchedChatHandler(Configs.Analysis.CP_PAGE, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                cpPage(chat, m);
                return sendMessage();
            }
        });
        addMatchedChatHandler(Configs.Analysis.CP_SEARCH, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                cpSearch(chat, m);
                return sendMessage();
            }
        });
    }

    void cpBusy(Text chat, Matcher m)
    {
        if(_looping && Configs.Generic.AUTO_PAGE.getBooleanValue() && Configs.Generic.DISABLE_CP_MESSAGES.getBooleanValue())
        {
            ChatMessage.localErrorT("watson.message.cp.auto_page.error");
        }
        reset();
    }

    void cpNoResult(Text chat, Matcher m)
    {
        reset();
    }

    void cpPage(Text chat, Matcher m)
    {
        int currentPage = Integer.parseInt(m.group(1));
        int pageCount = Integer.parseInt(m.group(2));

        if(Configs.Generic.AUTO_PAGE.getBooleanValue() && currentPage > _currentPage)
        {
            if (pageCount <= Configs.Generic.MAX_AUTO_PAGES.getIntegerValue())
            {
                _currentPage = currentPage;
                _pageCount = pageCount;
                if(_looping)
                {
                    if(Configs.Generic.DISABLE_CP_MESSAGES.getBooleanValue())
                    {
                        InfoUtils.printActionbarMessage("%d / %d", _currentPage, _pageCount);
                        if(_currentPage == 1)
                        {
                            ChatMessage.localOutputT("watson.message.cp.auto_page.start", _pageCount);
                        }
                        else if(_currentPage == _pageCount)
                        {
                            ChatMessage.localOutputT("watson.message.cp.auto_page.finished");
                        }
                    }
                    requestNextPage();
                }
            }
            else
            {
                reset();
            }
        }
        else if(_currentPage == _pageCount)
        {
            reset();
        }
    }

    void cpSearch(Text chat, Matcher m)
    {
        _looping = true;
    }

    void details(Text chat, Matcher m)
    {
        _lookupDetails = false;
        _millis = parseTimeExpression(m.group(1));
        _player = m.group(2);
        _action = m.group(3);
        String block = m.group(4);
        String[] blockStuff = block.split(" ");
        if(blockStuff.length == 2)
        {
            block = blockStuff[1];
        }
        _block = WatsonBlockRegistery.getInstance().getWatsonBlockByName(block);
        _loop = 1;
        if(_isLookup)
        {
            // Record that we can use these details at the next
            // coreprotect.lookupcoords only.
            _lookupDetails = true;
            if(blockStuff.length == 2)
            {
                String number = blockStuff[0].substring(1);
                _loop = Integer.valueOf(number);
            }
        }
        else
        {
            if(DataManager.getFilters().isAcceptedPlayer(_player))
            {
                BlockEdit edit = new BlockEdit(_millis, _player, _action, _x, _y, _z, _block, _world, _loop);
                SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, _firstInspectorResult));

                if(_firstInspectorResult)
                {
                    _firstInspectorResult = false;
                }
            }
        }
    }

    void inspectorCoords(Text chat, Matcher m)
    {
        _isLookup = false;
        _x = Integer.parseInt(m.group(1));
        _y = Integer.parseInt(m.group(2));
        _z = Integer.parseInt(m.group(3));
        EditSelection selection = DataManager.getEditSelection();
        selection.selectPosition(_x, _y, _z, _world, 1);
        _firstInspectorResult = true;
    }

    void lookupCoords(Text chat, Matcher m)
    {
        _isLookup = true;
        if(_lookupDetails)
        {
            _x = Integer.parseInt(m.group(1));
            _y = Integer.parseInt(m.group(2));
            _z = Integer.parseInt(m.group(3));
            _world = m.group(4);
            // https://github.com/totemo/watson/issues/23

            BlockEdit edit = new BlockEdit(_millis, _player, _action, _x, _y, _z, _block, _world, _loop);
            SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, true));

            _lookupDetails = false;
        }
    }

    void lookupHeader(Text chat, Matcher m)
    {
        _isLookup = true;
    }

    private long parseTimeExpression(String time)
    {
        Matcher absolute = ABSOLUTE_TIME.matcher(time);
        if(absolute.matches())
        {
            int month = Integer.parseInt(absolute.group(1));
            int day = Integer.parseInt(absolute.group(2));
            int hour = Integer.parseInt(absolute.group(3));
            int minute = Integer.parseInt(absolute.group(4));
            int second = Integer.parseInt(absolute.group(5));
            return TimeStamp.toMillis(month, day, hour, minute, second);
        }
        else
        {
            Matcher relative = HOURS_AGO_TIME.matcher(time);
            if(relative.matches())
            {
                String timed = relative.group(1).replace(",", ".");
                float hours = Float.parseFloat(timed);
                long millis = System.currentTimeMillis() - (long) (hours * MS_PER_HOUR);

                millis -= millis % (MS_PER_HOUR / 100);
                return millis;
            }
        }
        return 0;
    }

    private void requestNextPage()
    {
        if (_currentPage != 0 && _currentPage < _pageCount)
        {
            ChatMessage.getInstance().serverChat(String.format("/co l %d", _currentPage + 1), _currentPage == 1);
        }
    }

    public static void reset()
    {
        _looping = false;
        _currentPage = _pageCount = 0;
    }

    private boolean sendMessage()
    {
        if(_looping && Configs.Generic.AUTO_PAGE.getBooleanValue() && Configs.Generic.DISABLE_CP_MESSAGES.getBooleanValue())
        {
            return false;
        }
        return true;
    }
}