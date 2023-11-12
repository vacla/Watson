package eu.minemania.watson.analysis;

import java.util.regex.Matcher;

import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.client.Paginator;
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
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

//----------------------------------------------------------------------------

/**
 * An {@link Analysis} implementation that recognises inspector and lookup
 * results from CoreProtect.
 * <p>
 * CoreProtect inspector results look like this:
 *
 * <pre>
 * ----- CoreProtect ----- (x2/y63/z-6)
 * 0.00/h ago - totemo placed #4 (Cobblestone).
 * 1.36/h ago - totemo removed #4 (Cobblestone).
 * </pre>
 * <p>
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

    protected static boolean _looping;

    public CoreProtectAnalysis()
    {
        addMatchedChatHandler(Configs.Analysis.CP_BUSY, (chat, m) -> {
            busy();
            return sendMessage();
        });
        addMatchedChatHandler(Configs.Analysis.CP_DETAILS, (chat, m) -> {
            details(chat, m);
            return sendMessage();
        });
        addMatchedChatHandler(Configs.Analysis.CP_DETAILS_SESSION, ((chat, m) -> {
            detailsSession(chat, m);
            return sendMessage();
        }));
        addMatchedChatHandler(Configs.Analysis.CP_DETAILS_SIGN, (chat, m) -> {
            detailsSign(chat, m);
            return sendMessage();
        });
        addMatchedChatHandler(Configs.Analysis.CP_INSPECTOR_COORDS, (chat, m) -> {
            inspectorCoords(m);
            return true;
        });
        addMatchedChatHandler(Configs.Analysis.CP_LOOKUP_COORDS, (chat, m) -> {
            lookupCoords(m);
            return sendMessage();
        });
        addMatchedChatHandler(Configs.Analysis.CP_LOOKUP_HEADER, (chat, m) -> {
            lookupHeader();
            return sendMessage();
        });
        addMatchedChatHandler(Configs.Analysis.CP_NO_RESULT, (chat, m) -> {
            noResult();
            return true;
        });
        addMatchedChatHandler(Configs.Analysis.CP_PAGE, (chat, m) -> {
            page(m);
            return sendMessage();
        });
        addMatchedChatHandler(Configs.Analysis.CP_SEARCH, (chat, m) -> {
            search();
            return sendMessage();
        });
    }

    void busy()
    {
        if (_looping && Configs.Plugin.AUTO_PAGE.getBooleanValue() && Configs.Messages.DISABLE_CP_MESSAGES.getBooleanValue())
        {
            ChatMessage.localErrorT("watson.message.cp.auto_page.error");
        }
        reset();
    }

    void details(Text chat, Matcher m)
    {
        _lookupDetails = false;
        HoverEvent hover = chat.getSiblings().get(0).getStyle().getHoverEvent();
        if (hover != null && hover.getValue(hover.getAction()) != null)
        {
            String text = ((Text) hover.getValue(hover.getAction())).getString().replaceAll("\u00A7.", "");
            _millis = TimeStamp.parseTimeExpression(text, m.group(1));
        }
        else
        {
            _millis = TimeStamp.parseTimeExpression("", m.group(1));
        }
        _player = m.group(2);
        _action = m.group(3);
        String block = m.group(4);
        _loop = 1;
        if (m.group(5) != null)
        {
            block = m.group(4).split(" ")[1];
            _loop = Integer.parseInt(m.group(5));
        }
        _block = WatsonBlockRegistery.getInstance().getWatsonBlockByName(block);
        if (_isLookup)
        {
            // Record that we can use these details at the next
            // coreprotect.lookupcoords only.
            _lookupDetails = true;
        }
        else
        {
            if (DataManager.getFilters().isAcceptedPlayer(_player))
            {
                BlockEdit edit = new BlockEdit(_millis, _player, _action, _x, _y, _z, _block, _world, _loop);
                SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, _firstInspectorResult));

                if (_firstInspectorResult)
                {
                    _firstInspectorResult = false;
                }
            }
        }
    }

    void detailsSession(Text chat, Matcher m)
    {
        _lookupDetails = false;
        HoverEvent hover = chat.getSiblings().get(0).getStyle().getHoverEvent();
        if (hover != null && hover.getValue(hover.getAction()) != null)
        {
            String text = ((Text) hover.getValue(hover.getAction())).getString().replaceAll("\u00A7.", "");
            _millis = TimeStamp.parseTimeExpression(text, m.group(1));
        }
        else
        {
            _millis = TimeStamp.parseTimeExpression("", m.group(1));
        }
        _player = m.group(2);
        _action = "session"+m.group(3);
        String block = "minecraft:player";
        _loop = 1;
        _block = WatsonBlockRegistery.getInstance().getWatsonBlockByName(block);
        // Record that we can use these details at the next
        // coreprotect.lookupcoords only.
        _lookupDetails = true;
    }

    void detailsSign(Text chat, Matcher m)
    {
        _lookupDetails = false;
        HoverEvent hover = chat.getSiblings().get(0).getStyle().getHoverEvent();
        if (hover != null && hover.getValue(hover.getAction()) != null)
        {
            String text = ((Text) hover.getValue(hover.getAction())).getString().replaceAll("\u00A7.", "");
            _millis = TimeStamp.parseTimeExpression(text, m.group(1));
        }
        else
        {
            _millis = TimeStamp.parseTimeExpression("", m.group(1));
        }
        _player = m.group(2);
        _action = m.group(3);
        String block = "minecraft:oak_sign";
        if (_isLookup)
        {
            block = "minecraft:player";
            _x = _y = _z = 2;
        }
        _world = DataManager.getWorldPlugin().equals("") ? "" : DataManager.getWorldPlugin();
        _block = WatsonBlockRegistery.getInstance().getWatsonBlockByName(block);
        if (DataManager.getFilters().isAcceptedPlayer(_player))
        {
            BlockEdit edit = new BlockEdit(_millis, _player, _action, _x, _y, _z, _block, _world, 1);
            SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, _firstInspectorResult));

            if (_firstInspectorResult)
            {
                _firstInspectorResult = false;
            }
        }
    }

    void inspectorCoords(Matcher m)
    {
        _isLookup = false;
        _x = Integer.parseInt(m.group(1));
        _y = Integer.parseInt(m.group(2));
        _z = Integer.parseInt(m.group(3));
        _world = DataManager.getWorldPlugin().equals("") ? "" : DataManager.getWorldPlugin();
        EditSelection selection = DataManager.getEditSelection();
        selection.selectPosition(_x, _y, _z, _world, 1);
        _firstInspectorResult = true;
    }

    void lookupCoords(Matcher m)
    {
        _isLookup = true;
        if (_lookupDetails)
        {
            _x = Integer.parseInt(m.group(1));
            _y = Integer.parseInt(m.group(2));
            _z = Integer.parseInt(m.group(3));
            _world = m.group(4);

            BlockEdit edit = new BlockEdit(_millis, _player, _action, _x, _y, _z, _block, _world, _loop);
            SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, true));

            _lookupDetails = false;
        }
    }

    void lookupHeader()
    {
        _isLookup = true;
    }

    void noResult()
    {
        reset();
    }

    void page(Matcher m)
    {
        int currentPage = Integer.parseInt(m.group(1));
        int pageCount = Integer.parseInt(m.group(2));
        if (currentPage == 1)
        {
            _looping = true;
            Paginator.getInstance().setPageCount(pageCount);
        }
        if (Configs.Plugin.AUTO_PAGE.getBooleanValue())
        {
            if (pageCount <= Configs.Plugin.MAX_AUTO_PAGES.getIntegerValue())
            {
                Paginator.getInstance().setCurrentPage(currentPage);
                if (_looping)
                {
                    if (Configs.Messages.DISABLE_CP_MESSAGES.getBooleanValue())
                    {
                        InfoUtils.printActionbarMessage("watson.message.cp.auto_page.page", currentPage, pageCount);
                        if (currentPage == 1)
                        {
                            ChatMessage.localOutputT("watson.message.cp.auto_page.start", pageCount);
                        }
                        else if (currentPage == pageCount)
                        {
                            ChatMessage.localOutputT("watson.message.cp.auto_page.finished");
                        }
                    }
                    Paginator.getInstance().cpRequestNextPage();
                }
            }
            else
            {
                reset();
            }
        }
        if (currentPage == pageCount || !Configs.Plugin.AUTO_PAGE.getBooleanValue())
        {
            reset();
        }
    }

    void search()
    {
        _looping = true;
    }

    public static void reset()
    {
        _looping = false;
        Paginator.getInstance().reset();
    }

    private boolean sendMessage()
    {
        return !_looping || !Configs.Plugin.AUTO_PAGE.getBooleanValue() || !Configs.Messages.DISABLE_CP_MESSAGES.getBooleanValue();
    }
}