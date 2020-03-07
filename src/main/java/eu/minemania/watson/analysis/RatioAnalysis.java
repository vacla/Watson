package eu.minemania.watson.analysis;

import static eu.minemania.watson.analysis.LogBlockPatterns.LB_HEADER_BLOCK;
import static eu.minemania.watson.analysis.LogBlockPatterns.LB_HEADER_BLOCKS;
import static eu.minemania.watson.analysis.LogBlockPatterns.LB_HEADER_CHANGES;
import static eu.minemania.watson.analysis.LogBlockPatterns.LB_HEADER_NO_RESULTS;
import static eu.minemania.watson.analysis.LogBlockPatterns.LB_HEADER_RATIO;
import static eu.minemania.watson.analysis.LogBlockPatterns.LB_HEADER_RATIO_CURRENT;
import static eu.minemania.watson.analysis.LogBlockPatterns.LB_HEADER_SEARCHING;
import static eu.minemania.watson.analysis.LogBlockPatterns.LB_HEADER_TIME_CHECK;
import static eu.minemania.watson.analysis.LogBlockPatterns.LB_SUM;

import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;

import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.chat.IMatchedChatHandler;
import eu.minemania.watson.db.TimeStamp;
import net.minecraft.util.text.ITextComponent;

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
        addMatchedChatHandler(LB_HEADER_RATIO, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(ITextComponent chat, Matcher m)
            {
                lbHeaderRatio(chat, m);
                return true;
            }
        });

        addMatchedChatHandler(LB_HEADER_RATIO_CURRENT, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(ITextComponent chat, Matcher m)
            {
                lbHeaderRatioCurrent(chat, m);
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

        addMatchedChatHandler(LB_HEADER_NO_RESULTS, headerHandler);
        addMatchedChatHandler(LB_HEADER_CHANGES, headerHandler);
        addMatchedChatHandler(LB_HEADER_BLOCKS, headerHandler);
        addMatchedChatHandler(LB_HEADER_SEARCHING, headerHandler);
        addMatchedChatHandler(LB_HEADER_TIME_CHECK, headerHandler);
        addMatchedChatHandler(LB_HEADER_BLOCK, headerHandler);

        addMatchedChatHandler(LB_SUM, new IMatchedChatHandler()
        {
            @Override
            public boolean onMatchedChat(ITextComponent chat, Matcher m)
            {
                return lbSum(chat, m);
            }
        });
    }

    void lbHeader(ITextComponent chat, Matcher m)
    {
        reset();
    }

    void lbHeaderRatio(ITextComponent chat, Matcher m)
    {
        reset();
        _parsing = true;
        _sinceMinutes = Integer.parseInt(m.group(1));
        _beforeMinutes = Integer.parseInt(m.group(2));
    }

    void lbHeaderRatioCurrent(ITextComponent chat, Matcher m)
    {
        reset();
        _parsing = true;
        _sinceMinutes = Integer.parseInt(m.group(1));
        _beforeMinutes = 0;
    }

    boolean lbSum(ITextComponent chat, Matcher m)
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
                String period = String.format(Locale.US, "Between %s and %s:", TimeStamp.formatQueryTime(since.getTimeInMillis()), TimeStamp.formatQueryTime(before.getTimeInMillis()));
                Watson.logger.debug("Between " + _sinceMinutes + " and " + _beforeMinutes + " minutes ago ==>");
                Watson.logger.debug(period);

                String message;
                if (_stoneCount <= 0)
                {
                    message = "Was the player spelunking?";
                }
                else if (_diamondCount < 0)
                {
                    message = "Player placed more diamonds than were destroyed.";
                }
                else if (_diamondCount == 0)
                {
                    message = "Did the player place and destroy previously silk touched diamonds?";
                }
                else
                {
                    message = String.format(Locale.US, "stone:diamond = %d / %d = %.3g", _stoneCount, _diamondCount, (_stoneCount / (double) _diamondCount));
                }

                ChatMessage.sendToLocalChat(chat, true);
                ChatMessage.localOutput(period, true);
                ChatMessage.localOutput(message, true);
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