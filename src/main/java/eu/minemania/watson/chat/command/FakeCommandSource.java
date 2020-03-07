package eu.minemania.watson.chat.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandSource;

public class FakeCommandSource extends CommandSource
{
    private static List<String> colors = Arrays.asList(new String[] {"black", "darkblue", "darkgreen", "darkaqua", "darkred", "darkpurple", "gold", "grey", "gray", "darkgrey", "darkgray", "blue", "green", "aqua", "red", "lightpurple", "yellow", "white"});
    private static List<String> styles = Arrays.asList(new String[] {"+", "/", "_", "-", "?"});
    public FakeCommandSource(EntityPlayerSP player)
    {
        super(player, player.getPositionVector(), player.getPitchYaw(), null, 0, player.getScoreboardName(), player.getName(), null, player);
    }

    @Override
    public Collection<String> getPlayerNames()
    {
        return Minecraft.getInstance().getConnection().getPlayerInfoMap().stream().map(e -> e.getGameProfile().getName()).collect(Collectors.toList());
    }

    public static Collection<String> getColor()
    {
        return colors;
    }

    public static Collection<String> getStyle()
    {
        return styles;
    }
}