package eu.minemania.watson.mixin;

import eu.minemania.watson.data.DataManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Screen.class)
public abstract class MixinScreen extends AbstractParentElement
{
    @Shadow
    protected MinecraftClient client;

    @ModifyVariable(method = "sendMessage(Ljava/lang/String;Z)V", at = @At("HEAD"), argsOnly = true)
    private String onSendMessage(String msg)
    {
        return DataManager.onSendChatMessage(msg);
    }
}
