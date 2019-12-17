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
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {
	private boolean delete;
	@ModifyVariable(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At("HEAD"))
	private Text chatHighlighter(Text componentln) {
		delete = false;
		boolean allowChat = ChatProcessor.getInstance().onChat(componentln);
		if(allowChat) {
			if(Configs.Generic.USE_CHAT_HIGHLIGHTS.getBooleanValue()) {
				if(componentln instanceof TranslatableText) {
					if(((TranslatableText)componentln).getKey().contains("chat.type.text")) {
						Text newComponent = Highlight.setHighlightChatMessage(((TranslatableText)componentln).getKey(),componentln, false);
						return newComponent;
					}
				} else {
					Text newComponent = Highlight.setHighlightChatMessage(componentln);
					return newComponent;
				}
			}
		} else {
			delete = true;
		}
		return componentln;
	}
	
	@Inject(method = "addMessage(Lnet/minecraft/text/Text;I)V", at = @At(value = "INVOKE", shift = Shift.BEFORE, target = "Lnet/minecraft/client/gui/GuiNewChat;setChatLine(Lnet/minecraft/util/text/ITextComponent;IIZ)V"), cancellable = true)
    public void onDelete(CallbackInfo ci) {
		if(delete) {
			ci.cancel();
		}
	}
	
	
}
