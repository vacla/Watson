package eu.minemania.watson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.authlib.GameProfile;

import eu.minemania.watson.chat.Highlight;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity
{

    @Shadow
    public abstract Text getDisplayName();

    protected MixinPlayerEntity(World world, GameProfile gameprofile)
    {
        super(EntityType.PLAYER, world);
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE",	target = "Lnet/minecraft/entity/player/PlayerEntity;getName()Lnet/minecraft/text/Text;"))
    private Text getCustomUsername(PlayerEntity player)
    {
        if (Highlight.changeUsername)
        {
            Highlight.changeUsername = false;
            return new LiteralText(Highlight.getUsername());
        }
        else
        {
            return player.getName();
        }
    }
}
