package eu.minemania.watson.db;

import java.util.LinkedHashSet;

import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.selection.EditSelection;

public class Filters {
	protected LinkedHashSet<String> _filters = new LinkedHashSet<String>();
	private EditSelection edit = new EditSelection();
	
	public void list() {
		if(_filters.size() == 0) {
			ChatMessage.localOutput("No filters are set. All edits are accepted", true);
		} else {
			ChatMessage.localOutput("Edits by the following players will be accepted", true);
			StringBuilder message = new StringBuilder(' ');
			for(String player : _filters) {
				message.append(' ');
				message.append(player);
			}
			ChatMessage.localOutput(message.toString(), true);
		}
	}
	
	public void clear() {
		ChatMessage.localOutput("Watson filters cleared", true);
		_filters.clear();
	}
	
	public void addPlayer(String player) {
		player = player.toLowerCase();
		ChatMessage.localOutput("Added a filter to accept edits by " + player + ".", true);
		_filters.add(player);
		edit.getVariables().put("player", player);
	}
	
	public void removePlayer(String player) {
		player = player.toLowerCase();
		if(_filters.contains(player)) {
			ChatMessage.localOutput("Removed the filter for " + player + ".", true);
			_filters.remove(player);
		} else {
			ChatMessage.localError("The is no filter for " + player + ".", true);
		}
	}
	
	public boolean isAcceptedPlayer(String player) {
		player = player.toLowerCase();
		return _filters.size() == 0 || _filters.contains(player);
	}
}
