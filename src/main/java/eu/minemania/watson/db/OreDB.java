package eu.minemania.watson.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Locale;

import eu.minemania.watson.Watson;
import eu.minemania.watson.analysis.ServerTime;
import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.chat.Color;
import eu.minemania.watson.client.Teleport;
import eu.minemania.watson.config.Configs;
import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.render.OverlayRenderer;
import eu.minemania.watson.selection.EditSelection;
import net.minecraft.util.text.TextFormatting;

public class OreDB {
	protected LinkedHashMap<WatsonBlock, TypedOreDB> _db = new LinkedHashMap<WatsonBlock, TypedOreDB>();
	protected LinkedHashMap<WatsonBlock, Color> _chatColors = new LinkedHashMap<WatsonBlock, Color>();
	protected int _tpIndex = 0;
	protected ArrayList<OreDeposit> _oreDepositSequence = new ArrayList<OreDeposit>();
	protected boolean _oreDepositSequenceChanged = true;
	protected boolean _lastTimeOrderedDeposits = true;
	
	public OreDB() {
		WatsonBlockRegistery types = WatsonBlockRegistery.getInstance();
		
		//TODO add list malilib custom color
		_db.put(types.getWatsonBlockByName("minecraft:diamond_ore"), new TypedOreDB(200));
	    _db.put(types.getWatsonBlockByName("minecraft:emerald_ore"), new TypedOreDB(200));
	    _db.put(types.getWatsonBlockByName("minecraft:iron_ore"), new TypedOreDB(400));
	    _db.put(types.getWatsonBlockByName("minecraft:gold_ore"), new TypedOreDB(200));
	    _db.put(types.getWatsonBlockByName("minecraft:lapis_ore"), new TypedOreDB(200));
	    _db.put(types.getWatsonBlockByName("minecraft:redstone_ore"), new TypedOreDB(200));
	    _db.put(types.getWatsonBlockByName("minecraft:coal_ore"), new TypedOreDB(800));
	    _db.put(types.getWatsonBlockByName("minecraft:nether_quartz_ore"), new TypedOreDB(400));

	    _chatColors.put(types.getWatsonBlockByName("minecraft:diamond_ore"), Color.aqua);
	    _chatColors.put(types.getWatsonBlockByName("minecraft:emerald_ore"), Color.green);
	    _chatColors.put(types.getWatsonBlockByName("minecraft:iron_ore"), Color.gold);
	    _chatColors.put(types.getWatsonBlockByName("minecraft:gold_ore"), Color.yellow);
	    _chatColors.put(types.getWatsonBlockByName("minecraft:lapis_ore"), Color.blue);
	    _chatColors.put(types.getWatsonBlockByName("minecraft:redstone_ore"), Color.darkred);
	    _chatColors.put(types.getWatsonBlockByName("minecraft:coal_ore"), Color.darkgray);
	    _chatColors.put(types.getWatsonBlockByName("minecraft:nether_quartz_ore"), Color.white);
	}
	
	public void clear() {
		for(TypedOreDB db : _db.values()) {
			db.clear();
		}
		
		_tpIndex = 0;
		invalidateOreDepositSequence();
	}
	
	public void invalidateOreDepositSequence() {
		_oreDepositSequenceChanged = true;
	}
	
	public void listDeposits(int page) {
		int depositCount = getOreDepositCount();
		if(depositCount == 0) {
			ChatMessage.localOutput("There are no ore deposits.", true);
		} else {
			int pages = (depositCount + Configs.Generic.PAGE_LINES.getIntegerValue() - 1) / Configs.Generic.PAGE_LINES.getIntegerValue();
			if(page > pages) {
				ChatMessage.localError(String.format(Locale.US, "The highest page number is %d.", pages), true);
			} else {
				if(depositCount == 1) {
					ChatMessage.localOutput("There is 1 ore deposit", true);
				} else {
					ChatMessage.localOutput(String.format(Locale.US, "There are %d ore deposits.", depositCount), true);
				}
				
				ArrayList<OreDeposit> deposits = getOreDepositSequence();
				int first = 1 + (page - 1) * Configs.Generic.PAGE_LINES.getIntegerValue();
				int last = Math.min(first + Configs.Generic.PAGE_LINES.getIntegerValue() - 1, getOreDepositCount());
				for (int id = first; id <= last; ++id) {
					OreDeposit deposit = deposits.get(id - 1);
					long time = deposit.getTimeStamp();
					OreBlock block = deposit.getKeyOreBlock();
					BlockEdit edit = block.getEdit();
					WatsonBlock watsonblock = edit.block;
					String player = edit.player;
					TextFormatting strike = edit.playereditSet.isVisible() == true ? null : TextFormatting.STRIKETHROUGH;
					String line = String.format(Locale.US, "(%3d) %s (% 5d % 3d % 5d) %s [%2d] %s", id, TimeStamp.formatMonthDayTime(time), block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ(), watsonblock.getName(), deposit.getBlockCount(), player);
					ChatMessage.sendToLocalChat(_chatColors.get(watsonblock).getColor(), strike, line, true);
				}
				if (page < pages) {
					ChatMessage.localOutput(String.format(Locale.US, "Page %d of %d.", page, pages), true);
					ChatMessage.localOutput(String.format(Locale.US, "Use \"/%s ore %d\" to view the next page", Configs.Generic.WATSON_PREFIX.getStringValue(), (page+1)), true);
				}
			}
		}
	}
	
	public int getOreDepositCount() {
		return getOreDepositSequence().size();
	}
	
	public OreDeposit getOreDeposit(int index) {
		index = limitOreDepositIndex(index);
		return getOreDepositSequence().get(index - 1);
	}
	
	public void removeDeposits(String player) {
		for(TypedOreDB db : _db.values()) {
			db.removeDeposits(player);
		}
		invalidateOreDepositSequence();
	}
	
	public void tpNext() {
		tpIndex(_tpIndex + 1);
	}
	
	public void tpPrev() {
		tpIndex(_tpIndex - 1);
	}
	
	public void tpIndex(int index) {
		if(getOreDepositCount() == 0) {
			ChatMessage.localError("There are no ore deposits to teleport to.", true);
		} else {
			_tpIndex = index = limitOreDepositIndex(index);
			OreDeposit deposit = getOreDeposit(index);
			IntCoord coord = deposit.getKeyOreBlock().getLocation();
			Teleport.teleport(coord.getX(), coord.getY(), coord.getZ());
			ChatMessage.localOutput(String.format(Locale.US, "Teleporting you to ore #%d", index), true);
			EditSelection selection = DataManager.getEditSelection();
			selection.selectBlockEdit(deposit.getKeyOreBlock().getEdit());
		}
	}
	
	public void showRatios() {
		ServerTime.getInstance().queryServerTime(false);
		TypedOreDB diamonds = getDB(WatsonBlockRegistery.getInstance().getWatsonBlockByName("minecraft:diamond_ore"));
		
		if(diamonds.getOreDepositCount() != 0) {
			showRatio(diamonds.getOreDeposits().first(), diamonds.getOreDeposits().last());
			int count = 0;
			OreDeposit first = null;
			OreDeposit last = null;
			long lastTime = 0;
			for (OreDeposit deposit : diamonds.getOreDeposits()) {
				long depositTime = deposit.getKeyOreBlock().getEdit().time;
				if(first == null) {
					first = last = deposit;
					count = 1;
				} else {
					if(Math.abs(depositTime -lastTime) > 7 * 60 * 1000 || deposit == diamonds.getOreDeposits().last()) {
						if(deposit == diamonds.getOreDeposits().last()) {
							last = deposit;
						}
						if (count >= 3 && (first != diamonds.getOreDeposits().first() || last != diamonds.getOreDeposits().last())) {
							showRatio(first, last);
						}
						first = last = deposit;
						count = 1;
					} else {
						++count;
						last = deposit;
					}
				}
				lastTime = depositTime;
			}
		} else {
			ChatMessage.localOutput("There are no diamond ore deposits.", true);
		}
	}
	
	public void showTunnels() {
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
	
	public void addBlockEdit(BlockEdit edit) {
		try {
			WatsonBlock mergedBlock = edit.block;
			if(!edit.creation && isOre(mergedBlock)) {
				TypedOreDB db = getDB(mergedBlock);
				db.addBlockEdit(edit);
				invalidateOreDepositSequence();
			}
		} catch (Exception e) {
			Watson.logger.error("error in OreDB.addBlockEdit()", e);
		}
	}
	
	public void drawDepositLabels(double dx, double dy, double dz) {
		if(Configs.Generic.LABEL_SHOWN.getBooleanValue()) {
			int id = 1;
			StringBuilder label = new StringBuilder();
			for (OreDeposit deposit : getOreDepositSequence()) {
				OreBlock block = deposit.getKeyOreBlock();
				if(block.getEdit().playereditSet.isVisible()) {
					label.setLength(0);
					label.ensureCapacity(4);
					label.append(id);
					OverlayRenderer.drawBillboard(block.getLocation().getX() - dx + 0.5, block.getLocation().getY() - dy + 0.5, block.getLocation().getZ() - dz + 0.5, 0.03, label.toString());
				}
				++id;
			}
		}
	}
	
	protected void showRatio(OreDeposit first, OreDeposit last) {
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
		
		String query = String.format(Locale.US, "/lb player %s since %s before %s sum b block 1 56", player, sinceTime, beforeTime);
		Watson.logger.debug(query);
		ChatMessage.getInstance().serverChat(query);
	}
	
	protected TypedOreDB getDB(WatsonBlock block) {
		return _db.get(block);
	}
	
	protected boolean isOre(WatsonBlock block) {
		return _db.containsKey(block);
	}
	
	protected int limitOreDepositIndex(int index) {
		if(index < 1) {
			return getOreDepositCount();
		} else if (index > getOreDepositCount()) {
			return 1;
		} else {
			return index;
		}
	}
	
	protected ArrayList<OreDeposit> getOreDepositSequence(){
		if(_lastTimeOrderedDeposits != Configs.Generic.TIME_ORDERED_DEPOSITS.getBooleanValue()) {
			_oreDepositSequenceChanged = true;
		}
		if(_oreDepositSequenceChanged) {
			_oreDepositSequenceChanged = false;
			_lastTimeOrderedDeposits = Configs.Generic.TIME_ORDERED_DEPOSITS.getBooleanValue();
			_oreDepositSequence.clear();
			for(TypedOreDB db : _db.values()) {
				for(OreDeposit deposit : db.getOreDeposits()) {
					_oreDepositSequence.add(deposit);
				}
			}
			if(Configs.Generic.TIME_ORDERED_DEPOSITS.getBooleanValue()) {
				Collections.sort(_oreDepositSequence, new Comparator<OreDeposit>() {
					@Override
					public int compare(OreDeposit o1, OreDeposit o2) {
						return Long.signum(o1.getEarliestEdit().time - o2.getEarliestEdit().time);
					}
				});
			}
		}
		return _oreDepositSequence;
	}
}
