package eu.minemania.watson.mixin;

import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;
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

@Mixin(ChatHud.class)
public abstract class MixinChatHud
{
    private boolean delete;

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), argsOnly = true)
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
            if (Configs.Highlights.USE_CHAT_HIGHLIGHTS.getBooleanValue())
            {
                if (componentln.getContent() instanceof TranslatableTextContent)
                {
                    if (((TranslatableTextContent)componentln.getContent()).getKey().contains("chat.type.text"))
                    {
                        return Highlight.setHighlightChatMessage(((TranslatableTextContent) componentln.getContent()).getKey(), (MutableText) componentln, false);
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

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/client/gui/hud/ChatHud;addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V"), cancellable = true)
    public void onDelete(CallbackInfo ci)
    {
        if (delete)
        {
            ci.cancel();
        }
    }
}