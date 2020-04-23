package eu.minemania.watson.analysis;

import java.util.Calendar;
import java.util.regex.Matcher;

import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.chat.IMatchedChatHandler;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.db.TimeStamp;
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.text.Text;

public class RatioAnalysis extends Analysis
{
    protected static long STONE_DIAMOND_TIMEOUT_MILLIS = 250;
    protected boolean     _parsing;
    protected boolean     _gotStone;
    protected boolean     _gotDiamond;
    protected int         _stoneCount;
    protected int         _diamondCount;
    protected long        _stoneTime;
    protected long        _diamondTime;
    protected int         _sinceMinutes;
    protected int         _beforeMinutes;

    public RatioAnalysis()
    {
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_RATIO, new IMatchedChatHandler()
        {

            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                lbHeaderRatio(chat, m);
                return true;
            }
        });
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_RATIO_CURRENT, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                lbHeaderRatioCurrent(chat, m);
                return true;
            }
        });
        IMatchedChatHandler headerHandler = new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                lbHeader(chat, m);
                return true;
            }
        };

        addMatchedChatHandler(Configs.Analysis.LB_HEADER_NO_RESULTS, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_CHANGES, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_BLOCKS, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_SEARCHING, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_TIME_CHECK, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_BLOCK, headerHandler);

        addMatchedChatHandler(Configs.Analysis.LB_SUM, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(Text chat, Matcher m)
            {
                return lbSum(chat, m);
            }
        });
    }

    void lbHeader(Text chat, Matcher m)
    {
        reset();
    }

    void lbHeaderRatio(Text chat, Matcher m)
    {
        reset();
        _parsing = true;
        _sinceMinutes = Integer.parseInt(m.group(1));
        _beforeMinutes = Integer.parseInt(m.group(2));
    }

    void lbHeaderRatioCurrent(Text chat, Matcher m)
    {
        reset();
        _parsing = true;
        _sinceMinutes = Integer.parseInt(m.group(1));
        _beforeMinutes = 0;
    }

    boolean lbSum(Text chat, Matcher m)
    {
        if(_parsing)
        {
            int created = Integer.parseInt(m.group(1));
            int destroyed = Integer .parseInt(m.group(2));
            String block = m.group(3);
            if(block.equalsIgnoreCase("stone"))
            {
                _stoneCount = destroyed;
                _gotStone = true;
                _stoneTime = System.currentTimeMillis();
            }
            else if (block.equalsIgnoreCase("diamond ore"))
            {
                _diamondCount = destroyed - created;
                _gotDiamond = true;
                _diamondTime = System.currentTimeMillis();
            }
            if (_gotStone && _gotDiamond && Math.abs(_stoneTime - _diamondTime) <= STONE_DIAMOND_TIMEOUT_MILLIS)
            {
                int localMinusServer = ServerTime.getInstance().getLocalMinusServerMinutes();
                Calendar since = Calendar.getInstance();
                since.set(Calendar.SECOND, 0);
                since.add(Calendar.MINUTE, -(localMinusServer + _sinceMinutes));
                Calendar before = Calendar.getInstance();
                before.set(Calendar.SECOND, 0);
                before.add(Calendar.MINUTE, -(localMinusServer + _beforeMinutes));
                String period = StringUtils.translate("watson.message.lb.between_period", TimeStamp.formatQueryTime(since.getTimeInMillis()), TimeStamp.formatQueryTime(before.getTimeInMillis()));
                if(Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("Between " + _sinceMinutes + " and " + _beforeMinutes + " minutes ago ==>");
                    Watson.logger.info(period);
                }

                String message;
                if (_stoneCount <= 0)
                {
                    message = "watson.message.lb.spelunk";
                }
                else if (_diamondCount < 0)
                {
                    message = "watson.message.lb.more_diamond_placed";
                }
                else if (_diamondCount == 0)
                {
                    message = "watson.message.lb.silktouch_diamond";
                }
                else
                {
                    message = StringUtils.translate("watson.message.lb.stone_diamond", _stoneCount, _diamondCount, (_stoneCount / (double) _diamondCount));
                }

                ChatMessage.sendToLocalChat(chat, true);
                ChatMessage.localOutput(period, true);
                ChatMessage.localOutputT(message);
                reset();

                return false;
            }
        }
        return true;
    }

    private void reset()
    {
        _parsing = false;
        _gotStone = _gotDiamond = false;
        _stoneCount = _diamondCount = 0;
        _stoneTime = _diamondTime = 0;
        _sinceMinutes = _beforeMinutes = 0;
    }
}