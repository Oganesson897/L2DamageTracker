package dev.xkmc.l2damagetracker.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/event/entity/player/CriticalHitEvent;getDamageModifier()F", remap = false), method = "attack")
	public void l2damagetracker$attack$allowSweep(Entity target, CallbackInfo ci, @Local(ordinal = 2) LocalBooleanRef flag2) {
		flag2.set(false);
	}

}
