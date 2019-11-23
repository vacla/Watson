package eu.minemania.watson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import eu.minemania.watson.chat.ChatProcessor;
import eu.minemania.watson.chat.Highlight;
import eu.minemania.watson.config.Configs;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat {
	@ModifyVariable(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"))
	private ITextComponent chatHighlighter(ITextComponent componentln) {
		/*if(componentln instanceof TextComponentTranslation) {
			if(((TextComponentTranslation)componentln).getKey().contains("chat.type.text")) {
				ITextComponent newComponent = BlockTypeRegistery.highlightChatMessage(((TextComponentTranslation)componentln).getKey(), componentln);
				return newComponent;
			}
		}*/
		boolean allowChat = ChatProcessor.getInstance().onChat(componentln);
		if(allowChat) {
			if(Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue()) {
				if(componentln instanceof TextComponentTranslation) {
					if(((TextComponentTranslation)componentln).getKey().contains("chat.type.text")) {
						ITextComponent newComponent = Highlight.setHighlightChatMessage(((TextComponentTranslation)componentln).getKey(),componentln, false);
						return newComponent;
					}
				} else {
					ITextComponent newComponent = Highlight.setHighlightChatMessage(componentln);
					return newComponent;
				}
			}
		}
		return componentln;
	}
}
