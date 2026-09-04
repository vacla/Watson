package eu.minemania.watson.mixin;

import eu.minemania.watson.analysis.Analysis;
import eu.minemania.watson.analysis.CoreProtectAnalysis;
import eu.minemania.watson.config.Plugins;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
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
    @Unique
    private boolean delete;

    @ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), argsOnly = true)
    private Text chatHighlighter(Text componentln)
    {
        delete = false;
        Analysis.colorBlock = 0;
        if (Highlight.getReturnBoolean())
        {
            Highlight.toggleReturnBoolean();
            return componentln;
        }
        boolean allowChat = ChatProcessor.getInstance().onChat((MutableText) componentln);
        if (allowChat)
        {
            if (Configs.Plugin.PLUGIN.getOptionListValue() != Plugins.NULL &&
                Configs.Highlights.USE_CUSTOM_ROLLED_BACK_TEXT_COLOR.getBooleanValue() &&
                CoreProtectAnalysis.isCpMessage
            ) {
                if (componentln instanceof MutableText && componentln.getString().contains("§m"))
                {
                    MutableText newComponentln = MutableText.of(componentln.getContent());
                    newComponentln.setStyle(componentln.getStyle());
                    for (Text sibling : componentln.getSiblings())
                    {
                        if (sibling instanceof MutableText && ((MutableText) sibling).getString().contains("§m"))
                        {
                            if (sibling.getString().contains("§f§m") || sibling.getString().contains("§m§f"))
                            {
                                sibling = Text.of(sibling.getString().replaceAll("§f", ""));
                            }
                            ((MutableText) sibling).setStyle(sibling.getStyle().withColor(Configs.Highlights.ROLLED_BACK_TEXT_COLOR.getIntegerValue()));
                        }
                        newComponentln.append(sibling);
                    }
                    componentln = newComponentln;
                }
            }
            if (Configs.Highlights.COLOR_BLOCK_CHAT.getBooleanValue() && componentln instanceof MutableText && Analysis.colorBlock != 0)
            {
                MutableText newComponentln = MutableText.of(componentln.getContent());
                newComponentln.setStyle(componentln.getStyle());
                for (Text sibling : componentln.getSiblings())
                {
                    newComponentln.append(sibling);
                }
                MutableText colorBlockText = Text.literal(" ⬤").withColor(Analysis.colorBlock);
                newComponentln.append(colorBlockText);
                componentln = newComponentln;
            }
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

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), cancellable = true)
    public void onDelete(CallbackInfo ci)
    {
        if (delete)
        {
            ci.cancel();
        }
    }
}