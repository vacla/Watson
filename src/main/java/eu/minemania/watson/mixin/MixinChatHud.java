package eu.minemania.watson.mixin;

import net.minecraft.text.MutableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import eu.minemania.watson.chat.ChatProcessor;
import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.config.Configs;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(ChatHud.class)
public abstract class MixinChatHud
{
    private boolean delete;

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"), argsOnly = true)
    private Text chatHighlighter(Text componentln)
    {
        delete = false;
        if (Highlight.getReturnBoolean())
        {
            Highlight.toggleReturnBoolean();
            return componentln;
        }
        boolean allowChat = ChatProcessor.getInstance().onChat((MutableText) componentln);
        if (allowChat)
        {
            if (Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue())
            {
                if (componentln instanceof TranslatableText)
                {
                    if (((TranslatableText) componentln).getKey().contains("chat.type.text"))
                    {
                        return Highlight.setHighlightChatMessage(((TranslatableText) componentln).getKey(), (MutableText) componentln, false);
                    }
                }
                else
                {
                    return Highlight.setHighlightChatMessage((MutableText) componentln);
                }
            }
        }
        else
        {
            delete = true;
        }
        return componentln;
    }

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;IIZ)V"), cancellable = true)
    public void onDelete(CallbackInfo ci)
    {
        if (delete)
        {
            ci.cancel();
        }
    }
}