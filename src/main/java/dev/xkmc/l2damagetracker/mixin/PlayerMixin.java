package dev.xkmc.l2damagetracker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
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

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getFireAspect(Lnet/minecraft/world/entity/LivingEntity;)I"), method = "attack")
	public void l2damagetracker$attack$critParticle(Entity target, CallbackInfo ci, @Local(ordinal = 2) LocalBooleanRef flag2, @Local LocalRef<CriticalHitEvent> hitResult) {
		flag2.set(hitResult.get() != null);
	}


	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"), method = "actuallyHurt")
	public float l2damagetracker$actuallyHurt$moveLivingDamagePre(Player self, DamageSource source, float damage, Operation<Float> original) {
		float ans = original.call(self, source, damage);
		return ForgeHooks.onLivingDamage(self, source, ans);
	}


	@WrapOperation(at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraftforge/common/ForgeHooks;onLivingDamage(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)F"), method = "actuallyHurt")
	public float l2damagetracker$actuallyHurt$moveLivingDamagePost(LivingEntity entity, DamageSource src, float damage, Operation<Float> original) {
		return damage;
	}

}
