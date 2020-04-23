package eu.minemania.watson.db;

import java.util.LinkedHashSet;

import eu.minemania.watson.chat.ChatMessage;
import eu.minemania.watson.data.DataManager;

public class Filters
{
    protected LinkedHashSet<String> _filters = new LinkedHashSet<String>();

    public void list()
    {
        if(_filters.size() == 0)
        {
            ChatMessage.localOutputT("watson.message.filters.filters_edits");
        }
        else
        {
            ChatMessage.localOutputT("watson.message.filters.edits_accepted");
            StringBuilder message = new StringBuilder(' ');
            for(String player : _filters)
            {
                message.append(' ');
                message.append(player);
            }
            ChatMessage.localOutput(message.toString(), true);
        }
    }

    public void clear()
    {
        ChatMessage.localOutputT("watson.message.filters.filters_clear");
        _filters.clear();
    }

    public void addPlayer(String player)
    {
        player = player.toLowerCase();
        ChatMessage.localOutputT("watson.message.filters.filter_added", player);
        _filters.add(player);
        DataManager.getEditSelection().getVariables().put("player", player);
    }

    public void removePlayer(String player)
    {
        player = player.toLowerCase();
        if(_filters.contains(player))
        {
            ChatMessage.localOutputT("watson.message.filters.filter_removed", player);
            _filters.remove(player);
        }
        else
        {
            ChatMessage.localErrorT("watson.message.filters.filter_none", player);
        }
    }

    public boolean isAcceptedPlayer(String player)
    {
        player = player.toLowerCase();
        return _filters.size() == 0 || _filters.contains(player);
    }
}
