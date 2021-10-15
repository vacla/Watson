package eu.minemania.watson.mixin;

import eu.minemania.watson.data.DataManager;
import eu.minemania.watson.network.ledger.PluginInspectPacketHandler;
import eu.minemania.watson.network.ledger.PluginSearchPacketHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.AbstractParentElement;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "sendMessage(Ljava/lang/String;)V", at = @At("HEAD"), cancellable = true)
    public void onLedgerMessage(String message, CallbackInfo ci)
    {
        if (!message.startsWith("/"))
        {
            return;
        }
        if (!(message.contains("lg") || message.contains("ledger")))
        {
            return;
        }

        String[] temp = message.trim().split(" ");
        if (temp.length == 5)
        {
            if (temp[1].equals("inspect") || temp[1].equals("i"))
            {
                if (temp[2].equals("on") || temp[2].equals("off"))
                {
                    return;
                }
                new PluginInspectPacketHandler().sendPacket(Double.parseDouble(temp[2]), Double.parseDouble(temp[3]), Double.parseDouble(temp[4]), client);
                ci.cancel();
            }
        }
        if (temp.length >= 3)
        {
            if (temp[1].equals("search") || temp[1].equals("s"))
            {
                message = message.replaceAll(temp[0] + " " + temp[1], "").trim();
                new PluginSearchPacketHandler().sendPacket(message, client);
                ci.cancel();
            }
        }
    }
}
