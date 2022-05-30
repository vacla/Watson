package eu.minemania.watson.mixin;

import eu.minemania.watson.data.DataManager;
import net.minecraft.client.gui.screen.ChatScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatScreen.class)
public class MixinScreen
{
    @ModifyVariable(method = "sendMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), argsOnly = true)
    private String onSendMessage(String msg)
    {
        return DataManager.onSendChatMessage(msg);
    }
}
