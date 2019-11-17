package eu.minemania.watson.analysis;

import static eu.minemania.watson.analysis.PrismPatterns.DATE_TIME_WORLD_COORDS;
import static eu.minemania.watson.analysis.PrismPatterns.INSPECTOR_HEADER;
import static eu.minemania.watson.analysis.PrismPatterns.LOOKUP_DEFAULTS;
import static eu.minemania.watson.analysis.PrismPatterns.PLACE_BREAK;

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
import net.minecraft.util.text.ITextComponent;

public class PrismAnalysis extends Analysis {
	protected static final Pattern COUNT_PATTERN            = Pattern.compile(" x\\d+");
	protected static final Pattern RELATIVE_TIME_PATTERN    = Pattern.compile("(\\d+d)?(\\d+h)?(\\d+m)? ago");
	protected String               _player;
	protected WatsonBlock            _type;
	protected boolean              _created;
	protected boolean              _inspectorResult         = false;
	protected boolean              _awaitingFirstResult     = false;
	protected boolean              _expectingDateTimeCoords = false;
	
	public static void main(String[] args) {
		String line1 = " - totemo broke ironore 2m ago (a:break)";
		Matcher m = PLACE_BREAK.matcher(line1);
		if(m.matches()) {
			for(int i = 0; i <= m.groupCount(); ++i) {
				System.out.println(i + ": " + m.group(i));
			}
		}
	}
	
	public PrismAnalysis() {
		addMatchedChatHandler(PLACE_BREAK, new IMatchedChatHandler() {
			
			@Override
			public boolean onMatchedChat(ITextComponent chat, Matcher m) {
				placeBreak(chat, m);
				return true;
			}
		});
		addMatchedChatHandler(DATE_TIME_WORLD_COORDS, new IMatchedChatHandler() {
			
			@Override
			public boolean onMatchedChat(ITextComponent chat, Matcher m) {
				dateTimeWorldCoords(chat, m);
				return true;
			}
		});
		addMatchedChatHandler(LOOKUP_DEFAULTS, new IMatchedChatHandler() {
			
			@Override
			public boolean onMatchedChat(ITextComponent chat, Matcher m) {
				lookupDefaults(chat, m);
				return true;
			}
		});
		addMatchedChatHandler(INSPECTOR_HEADER, new IMatchedChatHandler() {
			
			@Override
			public boolean onMatchedChat(ITextComponent chat, Matcher m) {
				inspectorHeader(chat, m);
				return true;
			}
		});
	}
	
	void placeBreak(ITextComponent chat, Matcher m) {
		_player = m.group(1);
		String blockAndCount = m.group(2);
		boolean parsedNumericType = false;
		int id = 0;
		int data = 0;
		if(m.group(3) != null && m.group(4) != null) {
			try {
				id = Integer.parseInt(m.group(3));
				data = Integer.parseInt(m.group(4));
				parsedNumericType = true;
			} catch (NumberFormatException e) {
				
			}
		}
		
		String time = m.group(5);
		String action = m.group(6);
		
		if(parsedNumericType && id != 0) {
			_type = WatsonBlockRegistery.getInstance().getWatsonBlockByName(blockAndCount);
		} else {
			Matcher countMatch = COUNT_PATTERN.matcher(blockAndCount);
			String block = countMatch.find() ? blockAndCount.substring(0, m.start()) : blockAndCount;
			_type = WatsonBlockRegistery.getInstance().getWatsonBlockByName(block);
		}
		
		_created = !action.equals("break");
		_expectingDateTimeCoords = true;
	}
	
	void dateTimeWorldCoords(ITextComponent chat, Matcher m) {
		if(_expectingDateTimeCoords) {
			_expectingDateTimeCoords = false;
			int month = Integer.parseInt(m.group(1));
			int day = Integer.parseInt(m.group(2));
			int year = 2000 + Integer.parseInt(m.group(3));
			int hour = Integer.parseInt(m.group(4));
			int minute = Integer.parseInt(m.group(5));
			int second = Integer.parseInt(m.group(6));
			boolean pm = m.group(7).equalsIgnoreCase("pm");
			if(pm) {
				hour += 12;
			}
			long millis = TimeStamp.toMillis(year, month, day, hour, minute, second);
			
			int x = Integer.parseInt(m.group(8));
			int y = Integer.parseInt(m.group(9));
			int z = Integer.parseInt(m.group(10));
			EditSelection selection = DataManager.getEditSelection();
			selection.selectPosition(x, y, z);
			
			if(_player != null && _type != null) {
				if(DataManager.getFilters().isAcceptedPlayer(_player)) {
					boolean updateVariables = (!_inspectorResult || _awaitingFirstResult);
					BlockEdit edit = new BlockEdit(millis, _player, _created, x, y, z, _type);
					SyncTaskQueue.getInstance().addTask(new AddBlockEditTask(edit, updateVariables));
					if(_awaitingFirstResult) {
						_awaitingFirstResult = false;
					}
				}
			}
		}
	}
	
	void lookupDefaults(ITextComponent chat, Matcher m) {
		_inspectorResult = false;
	}
	
	void inspectorHeader(ITextComponent chat, Matcher m) {
		_inspectorResult = true;
		_awaitingFirstResult = true;
		
		int x = Integer.parseInt(m.group(1));
		int y = Integer.parseInt(m.group(2));
		int z = Integer.parseInt(m.group(3));
		EditSelection selection = DataManager.getEditSelection();
		selection.selectPosition(x, y, z);
	}
}
