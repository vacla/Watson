package eu.minemania.watson.analysis;

import java.util.Calendar;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;

import eu.minemania.watson.Watson;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.chat.Color;
import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.chat.IMatchedChatHandler;
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
import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class LogBlockAnalysis extends Analysis
{
    protected static final Formatting[] _COLOUR_CYCLE = new Formatting[]{Color.red.getColor(), Color.gold.getColor(), Color.yellow.getColor(), Color.green.getColor(), Color.aqua.getColor(), Color.darkpurple.getColor(), Color.lightpurple.getColor()};
    protected int _colourIndex = _COLOUR_CYCLE.length - 1;
    protected static final float _COLOUR_PROXIMITY_LIMIT = 4.0f;
    protected int _lastX, _lastY, _lastZ;
    protected String _world;
    protected int _x;
    protected int _y;
    protected int _z;
    protected long _lbPositionTime = 0;
    protected boolean _expectingFirstEdit = false;
    protected static long STONE_DIAMOND_TIMEOUT_MILLIS = 250;
    protected boolean _parsing;
    protected boolean _gotStone;
    protected boolean _gotDiamond;
    protected int _stoneCount;
    protected int _diamondCount;
    protected long _stoneTime;
    protected long _diamondTime;
    protected int _sinceMinutes;
    protected int _beforeMinutes;
    protected boolean _echoNextNoResults = true;

    public LogBlockAnalysis()
    {
        addMatchedChatHandler(Configs.Analysis.LB_PAGE, (chat, m) -> {
            lbPage(m);
            return sendMessage();
        });
        addMatchedChatHandler(Configs.Analysis.LB_DATA, (chat, m) -> {
            lbData(chat, m);
            return false;
        });
        addMatchedChatHandler(Configs.Analysis.LB_POSITION, (chat, m) -> {
            lbPosition(m);
            return sendMessage();
        });
        addMatchedChatHandler(Configs.Analysis.LB_SUM, this::lbSum);
        IMatchedChatHandler headerHandler = (chat, m) -> {
            lbHeader();
            return true;
        };
        addMatchedChatHandler(Configs.Analysis.LB_TP, (chat, m) -> {
            lbTp(m);
            return true;
        });

        addMatchedChatHandler(Configs.Analysis.LB_HEADER_NO_RESULTS, (chat, m) -> {
            lbHeader();
            return lbHeaderNoResults();
        });
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_CHANGES, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_BLOCKS, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_SUM_BLOCKS, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_SUM_PLAYERS, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_SEARCHING, headerHandler);
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_RATIO, (chat, m) -> {
            lbHeader();
            lbHeaderRatio(m);
            return true;
        });
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_RATIO_CURRENT, (chat, m) -> {
            lbHeader();
            lbHeaderRatioCurrent(m);
            return true;
        });
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_TIME_CHECK, (chat, m) -> {
            lbHeader();
            lbHeaderTimeCheck(m);
            return false;
        });
        addMatchedChatHandler(Configs.Analysis.LB_HEADER_BLOCK, headerHandler);
    }

    void lbData(Text chat, Matcher m)
    {
        try
        {
            int index = 0;
            if (m.group(1) != null)
            {
                index = Integer.parseInt(m.group(1));
            }
            Text sibling = chat.getSiblings().get(0);
            HoverEvent hover;
            if (sibling.getSiblings().isEmpty())
            {
                hover = chat.getSiblings().get(1).getSiblings().get(0).getStyle().getHoverEvent();
            }
            else
            {
                hover = sibling.getSiblings().get(0).getStyle().getHoverEvent();
            }
            String dateTime = m.group(2);
            long millis = 0;
            if (hover != null && hover.getValue(hover.getAction()) != null)
            {
                String text = ((Text) hover.getValue(hover.getAction())).getString().replaceAll("\u00A7.", "");
                millis = TimeStamp.parseTimeExpression(text, dateTime);
                dateTime = text;
            }
            else
            {
                millis = TimeStamp.parseTimeExpression("", dateTime);
            }
            String player = m.group(3);
            String action = m.group(4);
            String totalItemBlockName = m.group(5);
            int count = 1;
            if (totalItemBlockName.contains(" "))
            {
                count = Integer.parseInt(m.group(6));
                totalItemBlockName = totalItemBlockName.split(" ")[1];
            }
            String replacedBlock = "";
            if (m.group(7) != null)
            {
                replacedBlock = m.group(7);
            }
            String sign1 = null;
            String sign2 = null;
            String sign3 = null;
            String sign4 = null;
            if (m.group(8) != null)
            {
                sign1 = m.group(8);
                sign2 = m.group(9);
                sign3 = m.group(10);
                sign4 = m.group(11);
            }
            int coordX = 0;
            int coordY = 0;
            int coordZ = 0;
            MutableText teleportText = Text.translatable("watson.gui.label.blockedit.list.teleport");
            if (m.group(12) != null)
            {
                AtomicBoolean coordTeleport = new AtomicBoolean(false);
                chat.visit((style, text) -> {
                    if (coordTeleport.get())
                    {
                        teleportText.setStyle(style);
                        coordTeleport.set(false);
                    }
                    coordTeleport.set(text.equals(" at "));
                    return Optional.empty();
                }, Style.EMPTY);
                coordX = Integer.parseInt(m.group(12));
                coordY = Integer.parseInt(m.group(13));
                coordZ = Integer.parseInt(m.group(14));
            }
            String weapon = "";
            if (m.group(15) != null)
            {
                weapon = m.group(15);
            }

            WatsonBlock type;
            if (!action.equals("killed"))
            {
                type = WatsonBlockRegistery.getInstance().getWatsonBlockByName(totalItemBlockName);
            }
            else
            {
                type = WatsonBlockRegistery.getInstance().getBlockKillTypeByName(totalItemBlockName);
            }
            if (DataManager.getFilters().isAcceptedPlayer(player))
            {
                BlockEdit edit = new BlockEdit(millis, player, action, coordX, coordY, coordZ, type, _world, count);
                SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, _expectingFirstEdit));

                if (_expectingFirstEdit)
                {
                    _expectingFirstEdit = false;
                }
            }
            Formatting color = Configs.Plugin.RECOLOR_QUERY_RESULTS.getBooleanValue() ? getChatColorFormat(coordX, coordY, coordZ) : null;
            if (Configs.Plugin.REFORMAT_QUERY_RESULTS.getBooleanValue())
            {
                if (!type.getName().equals("minecraft:stone"))
                {
                    MutableText output;
                    if (action.equals("killed"))
                    {
                        output = Text.literal(String.format("(%2d) %s (%d,%d,%d) %s %s %s > %s ", index, dateTime, coordX, coordY, coordZ, _world, player, weapon, totalItemBlockName));
                    }
                    else if (action.equals("replaced"))
                    {
                        WatsonBlock newtype = WatsonBlockRegistery.getInstance().getWatsonBlockByName(replacedBlock);
                        output = Text.literal(String.format("(%2d) %s (%d,%d,%d) %s %s %s %s ", index, dateTime, coordX, coordY, coordZ, type.getName(), action, newtype.getName(), player));
                    }
                    else
                    {
                        String signText = (sign1 != null) ? String.format(" [%s] [%s] [%s] [%s]", sign1, sign2, sign3, sign4) : "";
                        output = Text.literal(String.format("(%2d) %s (%d,%d,%d) %s %s %s%s ", index, dateTime, coordX, coordY, coordZ, type.getName(), action, player, signText));
                    }
                    if (!teleportText.getStyle().isEmpty())
                    {
                        output.append(teleportText);
                    }
                    recolor(output, color);
                }
            }
            else
            {
                recolor(chat, color);
            }
            Paginator.getInstance().lbRequestNextPage();
        }
        catch (Exception ex)
        {
            Watson.logger.info("error parsing lb data", ex);
        }
    }

    void lbPosition(Matcher m)
    {
        if (m.group(1) != null)
        {
            _x = Integer.parseInt(m.group(1));
            _y = Integer.parseInt(m.group(2));
            _z = Integer.parseInt(m.group(3));
        }
        _world = m.group(4);
        if (m.group(1) != null)
        {
            EditSelection selection = DataManager.getEditSelection();
            selection.selectPosition(_x, _y, _z, _world, 1);
        }
        _lbPositionTime = System.currentTimeMillis();
        _expectingFirstEdit = true;
    }

    void lbHeaderRatio(Matcher m)
    {
        reset();
        _parsing = true;
        _sinceMinutes = Integer.parseInt(m.group(1));
        _beforeMinutes = Integer.parseInt(m.group(2));
    }

    void lbHeaderRatioCurrent(Matcher m)
    {
        reset();
        _parsing = true;
        _sinceMinutes = Integer.parseInt(m.group(1));
        _beforeMinutes = 0;
    }

    boolean lbSum(Text chat, Matcher m)
    {
        if (_parsing)
        {
            int created = Integer.parseInt(m.group(1));
            int destroyed = Integer.parseInt(m.group(2));
            String block = m.group(3);
            if (block.equalsIgnoreCase("stone"))
            {
                _stoneCount = destroyed - created;
                _gotStone = true;
                _stoneTime = System.currentTimeMillis();
            }
            else if (block.equalsIgnoreCase("diamond_ore"))
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
                if (Configs.Generic.DEBUG.getBooleanValue())
                {
                    Watson.logger.info("Between " + _sinceMinutes + " and " + _beforeMinutes + " minutes ago ==>");
                    Watson.logger.info(period);
                }
                String message = "";
                if (_diamondCount < 0)
                {
                    message = "watson.message.lb.more_diamond_placed";
                }
                else if (_diamondCount == 0)
                {
                    message = "watson.message.lb.silktouch_diamond";
                }
                else if (_stoneCount > _diamondCount)
                {
                    message = "watson.message.lb.spelunk";
                }

                Highlight.toggleReturnBoolean();
                ChatMessage.sendToLocalChat(chat, true);
                ChatMessage.localOutput(period, true);
                if (!message.isEmpty())
                {
                    ChatMessage.localOutputT(message);
                }
                ChatMessage.localOutput(StringUtils.translate("watson.message.lb.stone_diamond", _stoneCount, _diamondCount, _diamondCount != 0 ? (_stoneCount / (double) _diamondCount) : 0), true);
                reset();

                return false;
            }
        }
        return true;
    }

    void lbHeaderTimeCheck(Matcher m)
    {
        String serverIP = DataManager.getServerIP();
        if (serverIP != null && ServerTime.getServerTime(serverIP) == null)
        {
            int serverMinutes = Integer.parseInt(m.group(1));
            Calendar now = Calendar.getInstance();
            Calendar pastTime = ServerTime.getInstance().getPastTime();
            int localMinutes = (int) ((now.getTimeInMillis() - pastTime.getTimeInMillis()) / ServerTime.MINUTES_TO_MILLISECONDS);
            int localMinusServer = localMinutes - serverMinutes;
            ServerTime.putServerTime(serverIP, localMinusServer);
            if (Configs.Generic.DEBUG.getBooleanValue())
            {
                Watson.logger.info("Past time was " + serverMinutes + " minutes ago on the server and " + localMinutes + " minutes ago on the client.");
                Watson.logger.info("Client is " + localMinusServer + " minutes ahead of the server.");
            }
            if (ServerTime.showServerTime())
            {
                ServerTime.getInstance().showCurrentServerTime();
                ServerTime.toggleServerTime();
            }
            _echoNextNoResults = false;
        }
    }

    void lbTp(Matcher m)
    {
        try
        {
            int x = Integer.parseInt(m.group(1));
            int y = Integer.parseInt(m.group(2));
            int z = Integer.parseInt(m.group(3));

            EditSelection selection = DataManager.getEditSelection();
            String player = (String) selection.getVariables().get("player");
            BlockEdit edit = selection.getBlockEditSet().findEdit(x, y, z, player);
            selection.selectBlockEdit(edit);
        }
        catch (Exception ignored)
        {
        }
    }

    void recolor(Text text, Formatting color)
    {
        if (sendMessage())
        {
            if (Configs.Plugin.RECOLOR_QUERY_RESULTS.getBooleanValue())
            {
                Highlight.toggleReturnBoolean();
                ChatMessage.sendToLocalChat(color, text, true);
            }
            else
            {
                Highlight.toggleReturnBoolean();
                ChatMessage.sendToLocalChat(text, true);
            }
        }
    }

    boolean lbHeaderNoResults()
    {
        boolean echo = _echoNextNoResults;
        _echoNextNoResults = true;
        return echo;
    }

    void lbPage(Matcher m)
    {
        int currentPage = Integer.parseInt(m.group(1));
        int pageCount = Integer.parseInt(m.group(2));

        if (pageCount <= Configs.Plugin.MAX_AUTO_PAGES.getIntegerValue())
        {
            Paginator.getInstance().setCurrentPage(currentPage);
            Paginator.getInstance().setPageCount(pageCount);
        }
        else
        {
            Paginator.getInstance().reset();
        }
    }

    void lbHeader()
    {
        Paginator.getInstance().reset();
    }

    private void reset()
    {
        _parsing = false;
        _gotStone = _gotDiamond = false;
        _stoneCount = _diamondCount = 0;
        _stoneTime = _diamondTime = 0;
        _sinceMinutes = _beforeMinutes = 0;
    }

    private Formatting getChatColorFormat(int x, int y, int z)
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

    private boolean sendMessage()
    {
        return !Configs.Messages.DISABLE_LB_MESSAGES.getBooleanValue();
    }
}