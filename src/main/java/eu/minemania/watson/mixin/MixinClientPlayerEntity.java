package eu.minemania.watson.mixin;

import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.brigadier.StringReader;

import eu.minemania.watson.chat.command.ClientCommandManager;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity
{
    @ModifyVariable(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), argsOnly = true)
    private String onSendCommand(String message)
    {
        if ((message.startsWith("pr l") || message.startsWith("pr i")) && !message.contains("-extended"))
        {
            return message + " -extended";
        }

        return message;
    }

    @Inject(method = "sendCommand(Ljava/lang/String;Lnet/minecraft/text/Text;)V", at = @At("HEAD"), cancellable = true)
    private void sendCommand(String message, Text preview, CallbackInfo ci)
    {
        StringReader reader = new StringReader(message);
        int cursor = reader.getCursor();
        String commandName = reader.canRead() ? reader.readUnquotedString() : "";
        reader.setCursor(cursor);
        if (ClientCommandManager.isClientSideCommand(commandName))
        {
            ClientCommandManager.executeCommand(reader, message);
            ci.cancel();
        }
    }
}
