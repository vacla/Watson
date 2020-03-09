package eu.minemania.watson.analysis;

import static eu.minemania.watson.analysis.CoreProtectPatterns.DETAILS;
import static eu.minemania.watson.analysis.CoreProtectPatterns.INSPECTOR_COORDS;
import static eu.minemania.watson.analysis.CoreProtectPatterns.LOOKUP_COORDS;
import static eu.minemania.watson.analysis.CoreProtectPatterns.LOOKUP_HEADER;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.minemania.watson.chat.IMatchedChatHandler;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.db.BlockEdit;
import eu.minemania.watson.db.TimeStamp;
import eu.minemania.watson.db.WatsonBlock;
import eu.minemania.watson.db.WatsonBlockRegistery;
import eu.minemania.watson.scheduler.SyncTaskQueue;
import eu.minemania.watson.scheduler.tasks.AddBlockEditTask;
import eu.minemania.watson.selection.EditSelection;
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
    protected boolean _creation;
    protected int _x;
    protected int _y;
    protected int _z;
    protected String _world;
    protected long _millis;
    protected String _player;
    protected WatsonBlock _block;

    public CoreProtectAnalysis()
    {
        addMatchedChatHandler(INSPECTOR_COORDS, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                inspectorCoords(chat, m);
                return true;
            }
        });
        addMatchedChatHandler(DETAILS, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                details(chat, m);
                return true;
            }
        });
        addMatchedChatHandler(LOOKUP_COORDS, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                lookupCoords(chat, m);
                return true;
            }
        });
        addMatchedChatHandler(LOOKUP_HEADER, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                lookupHeader(chat, m);
                return true;
            }
        });
    }

    void inspectorCoords(Text chat, Matcher m)
    {
        _isLookup = false;
        _x = Integer.parseInt(m.group(1));
        _y = Integer.parseInt(m.group(2));
        _z = Integer.parseInt(m.group(3));
        EditSelection selection = DataManager.getEditSelection();
        selection.selectPosition(_x, _y, _z, _world);
        _firstInspectorResult = true;
    }

    void details(Text chat, Matcher m)
    {
        _lookupDetails = false;
        if(m.group(3).equals("placed") || m.group(3).equals("removed"))
        {
            _millis = parseTimeExpression(m.group(1));
            _player = m.group(2);
            _creation = m.group(3).equals("placed");
            String block = m.group(4);
            _block = WatsonBlockRegistery.getInstance().getWatsonBlockByName(block);
            if(_isLookup)
            {
                // Record that we can use these details at the next
                // coreprotect.lookupcoords only.
                _lookupDetails = true;
            }
            else
            {
                if(DataManager.getFilters().isAcceptedPlayer(_player))
                {
                    BlockEdit edit = new BlockEdit(_millis, _player, _creation, _x, _y, _z, _block, _world);
                    SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, _firstInspectorResult));

                    if(_firstInspectorResult)
                    {
                        _firstInspectorResult = false;
                    }
                }
            }
        }
    }

    void lookupHeader(Text chat, Matcher m)
    {
        _isLookup = true;
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

            BlockEdit edit = new BlockEdit(_millis, _player, _creation, _x, _y, _z, _block, _world);
            SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, true));
            _lookupDetails = false;
        }
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
}
