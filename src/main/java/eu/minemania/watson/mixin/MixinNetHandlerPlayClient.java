package eu.minemania.watson.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;

import eu.minemania.watson.chat.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketCommandList;

@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {
	@Shadow
	@Final
    private CommandDispatcher<CommandSource> commandDispatcher;
	
	@SuppressWarnings("unchecked")
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onInit(Minecraft mc, GuiScreen screen, NetworkManager connection, GameProfile profile, CallbackInfo ci) {
        Command.registerCommands((CommandDispatcher<CommandSource>) (Object) commandDispatcher);
    }
	
    @SuppressWarnings("unchecked")
    @Inject(method = "handleCommandList", at = @At("TAIL"))
    public void onOnCommandTree(SPacketCommandList packet, CallbackInfo ci) {
        Command.registerCommands((CommandDispatcher<CommandSource>) (Object) commandDispatcher);
    }
}
