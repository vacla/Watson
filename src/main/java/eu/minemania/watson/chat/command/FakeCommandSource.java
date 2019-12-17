package eu.minemania.watson.chat.command;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.command.ServerCommandSource;

public class FakeCommandSource extends ServerCommandSource {
	private static List<String> colors = Arrays.asList(new String[] {"black", "darkblue", "darkgreen", "darkaqua", "darkred", "darkpurple", "gold", "grey", "gray", "darkgrey", "darkgray", "blue", "green", "aqua", "red", "lightpurple", "yellow", "white"});
	private static List<String> styles = Arrays.asList(new String[] {"+", "/", "_", "-", "?"});
	public FakeCommandSource(ClientPlayerEntity player) {
		super(player, player.getPosVector(), player.getRotationClient(), null, 0, player.getEntityName(), player.getName(), null, player);
	}

	@Override
	public Collection<String> getPlayerNames() {
		return MinecraftClient.getInstance().getNetworkHandler().getPlayerList().stream().map(e -> e.getProfile().getName()).collect(Collectors.toList());
	}

	public static Collection<String> getColor() {
		return colors;
	}

	public static Collection<String> getStyle() {
		return styles;
	}
}
