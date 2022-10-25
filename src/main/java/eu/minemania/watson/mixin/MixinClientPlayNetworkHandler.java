package eu.minemania.watson.mixin;

import eu.minemania.watson.network.ClientPacketChannelHandler;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.network.ClientPlayNetworkHandler;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class MixinClientPlayNetworkHandler
{
    @Inject(method = "onCustomPayload", cancellable = true,
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/network/packet/s2c/play/CustomPayloadS2CPacket;getChannel()Lnet/minecraft/util/Identifier;"))
    private void onCustomPayloadWatson(CustomPayloadS2CPacket packet, CallbackInfo ci)
    {
        if (((ClientPacketChannelHandler) ClientPacketChannelHandler.getInstance()).processPacketFromServer(packet, (ClientPlayNetworkHandler) (Object) this))
        {
            ci.cancel();
        }
    }
}
