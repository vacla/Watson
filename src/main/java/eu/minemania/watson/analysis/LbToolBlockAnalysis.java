package eu.minemania.watson.analysis;

import java.util.regex.Matcher;

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
import net.minecraft.text.Text;

public class LbToolBlockAnalysis extends Analysis
{
    protected int _x;
    protected int _y;
    protected int _z;
    protected String _world;
    protected long _lbPositionTime = 0;
    protected boolean _expectingFirstEdit = false;
    private static final long POSITION_TIMEOUT_MILLIS = 250;

    public LbToolBlockAnalysis()
    {
        addMatchedChatHandler(Configs.Analysis.LB_POSITION, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                lbPosition(chat, m);
                return true;
            }
        });
        addMatchedChatHandler(Configs.Analysis.LB_EDIT, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                lbEdit(chat, m);
                return true;
            }
        });
        addMatchedChatHandler(Configs.Analysis.LB_EDIT_REPLACED, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                lbEditReplaced(chat, m);
                return true;
            }
        });
    }

    void lbPosition(Text chat, Matcher m)
    {
        _x = Integer.parseInt(m.group(1));
        _y = Integer.parseInt(m.group(2));
        _z = Integer.parseInt(m.group(3));
        _world = m.group(4);
        EditSelection selection = DataManager.getEditSelection();
        selection.selectPosition(_x, _y, _z, _world, 1);
        _lbPositionTime = System.currentTimeMillis();
        _expectingFirstEdit = true;
    }

    void lbEdit(Text chat, Matcher m)
    {
        if((System.currentTimeMillis() - _lbPositionTime) < POSITION_TIMEOUT_MILLIS)
        {
            int[] ymd = TimeStamp.parseYMD(m.group(1));
            int hour = Integer.parseInt(m.group(2));
            int minute = Integer.parseInt(m.group(3));
            int second = Integer.parseInt(m.group(4) != null ? m.group(4) : String.valueOf(0));
            long millis = TimeStamp.toMillis(ymd, hour, minute, second);
            String player = m.group(5);
            String action = m.group(6);
            boolean created = action.equals("created");
            String block = m.group(7);
            WatsonBlock type = WatsonBlockRegistery.getInstance().getWatsonBlockByName(block);
            addBlockEdit(millis, player, created, type, _world);
        }
    }

    void lbEditReplaced(Text chat, Matcher m)
    {
        if((System.currentTimeMillis() - _lbPositionTime) < POSITION_TIMEOUT_MILLIS)
        {
            int[] ymd = TimeStamp.parseYMD(m.group(1));
            int hour = Integer.parseInt(m.group(2));
            int minute = Integer.parseInt(m.group(3));
            int second = Integer.parseInt(m.group(4));
            long millis = TimeStamp.toMillis(ymd, hour, minute, second);
            String player = m.group(5);
            String oldBlock = m.group(6);
            WatsonBlock type = WatsonBlockRegistery.getInstance().getWatsonBlockByName(oldBlock);

            addBlockEdit(millis, player, false, type, _world);
        }
    }

    protected void addBlockEdit(long millis, String player, boolean created, WatsonBlock type, String world)
    {
        if(DataManager.getFilters().isAcceptedPlayer(player))
        {
            BlockEdit edit = new BlockEdit(millis, player, created, _x, _y, _z, type, world, 1);
            SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, _expectingFirstEdit));

            if(_expectingFirstEdit)
            {
                _expectingFirstEdit = false;
            }
        }
    }
}