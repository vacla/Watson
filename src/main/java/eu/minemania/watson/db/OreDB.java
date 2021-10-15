package eu.minemania.watson.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import eu.minemania.watson.Watson;
import eu.minemania.watson.analysis.ServerTime;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.chat.Color;
import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.render.OverlayRenderer;
import eu.minemania.watson.selection.EditSelection;
import net.minecraft.block.Blocks;
import net.minecraft.util.Formatting;

public class OreDB
{
    protected LinkedHashMap<WatsonBlock, TypedOreDB> _db = new LinkedHashMap<>();
    protected LinkedHashMap<WatsonBlock, Color> _chatColors = new LinkedHashMap<>();
    protected int _tpIndex = 0;
    protected ArrayList<OreDeposit> _oreDepositSequence = new ArrayList<>();
    protected boolean _oreDepositSequenceChanged = true;
    protected boolean _lastTimeOrderedDeposits = true;

    public OreDB()
    {
        WatsonBlockRegistery types = WatsonBlockRegistery.getInstance();

        //TODO add list malilib custom color
        _db.put(types.getWatsonBlockByBlock(Blocks.DIAMOND_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.EMERALD_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.IRON_ORE), new TypedOreDB(400));
        _db.put(types.getWatsonBlockByBlock(Blocks.GOLD_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.LAPIS_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.REDSTONE_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.COAL_ORE), new TypedOreDB(800));
        _db.put(types.getWatsonBlockByBlock(Blocks.NETHER_QUARTZ_ORE), new TypedOreDB(400));
        _db.put(types.getWatsonBlockByBlock(Blocks.ANCIENT_DEBRIS), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.GILDED_BLACKSTONE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.NETHER_GOLD_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.COPPER_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_DIAMOND_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_EMERALD_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_IRON_ORE), new TypedOreDB(400));
        _db.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_GOLD_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_LAPIS_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_REDSTONE_ORE), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_COAL_ORE), new TypedOreDB(800));
        _db.put(types.getWatsonBlockByBlock(Blocks.SMALL_AMETHYST_BUD), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.MEDIUM_AMETHYST_BUD), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.LARGE_AMETHYST_BUD), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.AMETHYST_CLUSTER), new TypedOreDB(200));
        _db.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_COPPER_ORE), new TypedOreDB(200));

        _chatColors.put(types.getWatsonBlockByBlock(Blocks.DIAMOND_ORE), Color.aqua);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.EMERALD_ORE), Color.green);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.IRON_ORE), Color.gold);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.GOLD_ORE), Color.yellow);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.LAPIS_ORE), Color.blue);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.REDSTONE_ORE), Color.darkred);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.COAL_ORE), Color.darkgray);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.NETHER_QUARTZ_ORE), Color.white);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.ANCIENT_DEBRIS), Color.gray);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.GILDED_BLACKSTONE), Color.yellow);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.NETHER_GOLD_ORE), Color.yellow);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.COPPER_ORE), Color.yellow);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_DIAMOND_ORE), Color.aqua);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_EMERALD_ORE), Color.green);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_IRON_ORE), Color.gold);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_GOLD_ORE), Color.yellow);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_LAPIS_ORE), Color.blue);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_REDSTONE_ORE), Color.darkred);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_COAL_ORE), Color.darkgray);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.SMALL_AMETHYST_BUD), Color.lightpurple);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.MEDIUM_AMETHYST_BUD), Color.lightpurple);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.LARGE_AMETHYST_BUD), Color.lightpurple);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.AMETHYST_CLUSTER), Color.lightpurple);
        _chatColors.put(types.getWatsonBlockByBlock(Blocks.DEEPSLATE_COPPER_ORE), Color.yellow);
    }

    public void clear()
    {
        for (TypedOreDB db : _db.values())
        {
            db.clear();
        }

        _tpIndex = 0;
        invalidateOreDepositSequence();
    }

    public void invalidateOreDepositSequence()
    {
        _oreDepositSequenceChanged = true;
    }

    public void listDeposits(int page)
    {
        int depositCount = getOreDepositCount();
        if (depositCount == 0)
        {
            ChatMessage.localOutputT("watson.message.deposit.none");
        }
        else
        {
            int pages = (depositCount + Configs.Plugin.PAGE_LINES.getIntegerValue() - 1) / Configs.Plugin.PAGE_LINES.getIntegerValue();
            if (page > pages)
            {
                ChatMessage.localErrorT("watson.message.deposit.highest_page", pages);
            }
            else
            {
                if (depositCount == 1)
                {
                    ChatMessage.localOutputT("watson.message.deposit.ore.1");
                }
                else
                {
                    ChatMessage.localOutputT("watson.message.deposit.ore.more", depositCount);
                }

                ArrayList<OreDeposit> deposits = getOreDepositSequence();
                int first = 1 + (page - 1) * Configs.Plugin.PAGE_LINES.getIntegerValue();
                int last = Math.min(first + Configs.Plugin.PAGE_LINES.getIntegerValue() - 1, getOreDepositCount());
                for (int id = first; id <= last; ++id)
                {
                    OreDeposit deposit = deposits.get(id - 1);
                    long time = deposit.getTimeStamp();
                    OreBlock block = deposit.getKeyOreBlock();
                    BlockEdit edit = block.getEdit();
                    WatsonBlock watsonblock = edit.block;
                    String player = edit.player;
                    Formatting strike = edit.playereditSet.isVisible() ? null : Formatting.STRIKETHROUGH;
                    String line = String.format("(%3d) %s (%5d %3d %5d %s) %s [%2d] %s", id, TimeStamp.formatMonthDayTime(time), block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ(), block.getLocation().getWorld(), watsonblock.getName(), deposit.getBlockCount(), player);
                    ChatMessage.sendToLocalChat(_chatColors.get(watsonblock).getColor(), strike, line, true);
                }
                if (page < pages)
                {
                    ChatMessage.localOutputT("watson.message.blockedit.pages", page, pages);
                    ChatMessage.localOutputT("watson.message.deposit.next_page", Configs.Generic.WATSON_PREFIX.getStringValue(), (page + 1));
                }
            }
        }
    }

    public int getOreDepositCount()
    {
        return getOreDepositSequence().size();
    }

    public OreDeposit getOreDeposit(int index)
    {
        index = limitOreDepositIndex(index);
        return getOreDepositSequence().get(index - 1);
    }

    public void removeDeposits(String player)
    {
        for (TypedOreDB db : _db.values())
        {
            db.removeDeposits(player);
        }
        invalidateOreDepositSequence();
    }

    public void tpNext()
    {
        tpIndex(_tpIndex + 1);
    }

    public void tpPrev()
    {
        tpIndex(_tpIndex - 1);
    }

    public void tpIndex(int index)
    {
        if (getOreDepositCount() == 0)
        {
            ChatMessage.localErrorT("watson.message.deposit.no_teleport");
        }
        else
        {
            _tpIndex = index = limitOreDepositIndex(index);
            OreDeposit deposit = getOreDeposit(index);
            IntCoord coord = deposit.getKeyOreBlock().getLocation();
            Teleport.teleport(coord.getX(), coord.getY(), coord.getZ(), coord.getWorld());
            ChatMessage.localOutputT("watson.message.deposit.teleport", index);
            EditSelection selection = DataManager.getEditSelection();
            selection.selectBlockEdit(deposit.getKeyOreBlock().getEdit());
        }
    }

    public void showRatios()
    {
        ServerTime.getInstance().queryServerTime(false);
        TypedOreDB diamonds = getDB(WatsonBlockRegistery.getInstance().getWatsonBlockByName("minecraft:diamond_ore"));

        if (diamonds.getOreDepositCount() != 0)
        {
            showRatio(diamonds.getOreDeposits().first(), diamonds.getOreDeposits().last());
            int count = 0;
            OreDeposit first = null;
            OreDeposit last = null;
            long lastTime = 0;
            for (OreDeposit deposit : diamonds.getOreDeposits())
            {
                long depositTime = deposit.getKeyOreBlock().getEdit().time;
                if (first == null)
                {
                    first = last = deposit;
                    count = 1;
                }
                else
                {
                    if (Math.abs(depositTime - lastTime) > 7 * 60 * 1000 || deposit == diamonds.getOreDeposits().last())
                    {
                        if (deposit == diamonds.getOreDeposits().last())
                        {
                            last = deposit;
                        }
                        if (count >= 3 && (first != diamonds.getOreDeposits().first() || last != diamonds.getOreDeposits().last()))
                        {
                            showRatio(first, last);
                        }
                        first = last = deposit;
                        count = 1;
                    }
                    else
                    {
                        ++count;
                        last = deposit;
                    }
                }
                lastTime = depositTime;
            }
        }
        else
        {
            ChatMessage.localOutputT("watson.message.deposit.no_diamond");
        }
    }

    public void showTunnels()
    {
        // TODO: This won't work without a way of queueing up commands over a long
        // period of time and waiting for each to complete.
        // // Show at most the configured maximum number of tunnels.
        // TypedOreDB diamonds =
        // getDB(BlockTypeRegistry.instance.getBlockTypeById(56));
        // int count = 0;
        // for (OreDeposit deposit : diamonds.getOreDeposits())
        // {
        // Controller.instance.selectBlockEdit(deposit.getKeyOreBlock().getEdit());
        // Controller.instance.queryPreviousEdits();
        //
        // ++count;
        // // TODO: Replace magic number with configuration setting.
        // if (count > 10)
        // {
        // break;
        // }
        // } // for
    } // showTunnels

    public void addBlockEdit(BlockEdit edit)
    {
        try
        {
            WatsonBlock mergedBlock = edit.block;
            if (!edit.isCreated() && isOre(mergedBlock))
            {
                TypedOreDB db = getDB(mergedBlock);
                db.addBlockEdit(edit);
                invalidateOreDepositSequence();
            }
        }
        catch (Exception e)
        {
            Watson.logger.error("error in OreDB.addBlockEdit()", e);
        }
    }

    public void drawDepositLabels()
    {
        if (Configs.Edits.LABEL_SHOWN.getBooleanValue())
        {
            int id = 1;
            StringBuilder label = new StringBuilder();
            for (OreDeposit deposit : getOreDepositSequence())
            {
                OreBlock block = deposit.getKeyOreBlock();
                if (DataManager.getWorldPlugin().isEmpty() || DataManager.getWorldPlugin().equals(block._location.getWorld()))
                {
                    if (block.getEdit().playereditSet.isVisible())
                    {
                        label.setLength(0);
                        label.ensureCapacity(4);
                        label.append(id);
                        OverlayRenderer.drawBillboard(block.getLocation().getX() + 0.5, block.getLocation().getY() + 0.5, block.getLocation().getZ() + 0.5, 0.03, label.toString());
                    }
                    ++id;
                }
            }
        }
    }

    protected void showRatio(OreDeposit first, OreDeposit last)
    {
        Calendar startTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        startTime.setTimeInMillis(first.getEarliestEdit().time);
        endTime.setTimeInMillis(last.getLatestEdit().time);

        startTime.add(Calendar.MINUTE, -7);
        startTime.set(Calendar.SECOND, 0);

        endTime.add(Calendar.MINUTE, 1);
        endTime.set(Calendar.SECOND, 0);

        String player = first.getKeyOreBlock().getEdit().player;
        String sinceTime = TimeStamp.formatQueryTime(startTime.getTimeInMillis());
        String beforeTime = TimeStamp.formatQueryTime(endTime.getTimeInMillis());

        String query = String.format("/lb player %s since %s before %s sum b block stone diamond_ore", player, sinceTime, beforeTime);
        if (Configs.Generic.DEBUG.getBooleanValue())
        {
            Watson.logger.info(query);
        }
        ChatMessage.getInstance().serverChat(query, false);
    }

    protected TypedOreDB getDB(WatsonBlock block)
    {
        return _db.get(block);
    }

    protected boolean isOre(WatsonBlock block)
    {
        return _db.containsKey(block);
    }

    protected int limitOreDepositIndex(int index)
    {
        if (index < 1)
        {
            return getOreDepositCount();
        }
        else if (index > getOreDepositCount())
        {
            return 1;
        }
        else
        {
            return index;
        }
    }

    protected ArrayList<OreDeposit> getOreDepositSequence()
    {
        if (_lastTimeOrderedDeposits != Configs.Edits.TIME_ORDERED_DEPOSITS.getBooleanValue())
        {
            _oreDepositSequenceChanged = true;
        }
        if (_oreDepositSequenceChanged)
        {
            _oreDepositSequenceChanged = false;
            _lastTimeOrderedDeposits = Configs.Edits.TIME_ORDERED_DEPOSITS.getBooleanValue();
            _oreDepositSequence.clear();
            for (TypedOreDB db : _db.values())
            {
                _oreDepositSequence.addAll(db.getOreDeposits());
            }
            if (Configs.Edits.TIME_ORDERED_DEPOSITS.getBooleanValue())
            {
                _oreDepositSequence.sort((o1, o2) -> Long.signum(o1.getEarliestEdit().time - o2.getEarliestEdit().time));
            }
        }
        return _oreDepositSequence;
    }
}