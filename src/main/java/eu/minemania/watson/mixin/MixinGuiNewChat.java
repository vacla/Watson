package eu.minemania.watson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.minemania.watson.chat.ChatProcessor;
import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.config.Configs;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat
{
    private boolean delete;
    @ModifyVariable(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
    private ITextComponent chatHighlighter(ITextComponent componentln)
    {
        delete = false;
        boolean allowChat = ChatProcessor.getInstance().onChat(componentln);
        if(allowChat)
        {
            if(Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue())
            {
                if(componentln instanceof TextComponentTranslation)
                {
                    if(((TextComponentTranslation)componentln).getKey().contains("chat.type.text"))
                    {
                        ITextComponent newComponent = Highlight.setHighlightChatMessage(((TextComponentTranslation)componentln).getKey(),componentln, false);
                        return newComponent;
                    }
                }
                else
                {
                    ITextComponent newComponent = Highlight.setHighlightChatMessage(componentln);
                    return newComponent;
                }
            }
        }
        else
        {
            delete = true;
        }
        return componentln;
    }

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/client/gui/GuiNewChat;setChatLine(Lnet/minecraft/util/text/ITextComponent;IIZ)V"), cancellable = true)
    public void onDelete(CallbackInfo ci)
    {
        if(delete)
        {
            ci.cancel();
        }
    }
}