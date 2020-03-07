package eu.minemania.watson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import eu.minemania.watson.chat.Highlight;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase
{
    @Shadow
    public abstract ITextComponent getDisplayName();

    protected MixinEntityPlayer(EntityType<?> type, World world)
    {
        super(type,world);
    }

    @Redirect(method = "getDisplayName", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/player/EntityPlayer;getName()Lnet/minecraft/util/text/ITextComponent;"))
    private ITextComponent getCustomUsername(EntityPlayer player)
    {
        if (Highlight.changeUsername)
        {
            Highlight.changeUsername = false;
            return new TextComponentString(Highlight.getUsername());
        }
        else
        {
            return player.getName();
        }
    }
}