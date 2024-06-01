package dev.xkmc.l2damagetracker.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.xkmc.l2damagetracker.contents.curios.TotemHelper;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(at = @At("HEAD"), method = "checkTotemDeathProtection", cancellable = true)
	public void l2damagetracker$checkTotemDeathProtection$addCustomTotem(DamageSource pDamageSource, CallbackInfoReturnable<Boolean> cir) {
		LivingEntity self = Wrappers.cast(this);
		if (TotemHelper.process(self, pDamageSource)) cir.setReturnValue(true);
	}

	@WrapOperation(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getDamageAfterMagicAbsorb(Lnet/minecraft/world/damagesource/DamageSource;F)F"), method = "actuallyHurt")
	public float l2damagetracker$actuallyHurt$moveLivingDamagePre(LivingEntity self, DamageSource source, float damage, Operation<Float> original) {
		float ans = original.call(self, source, damage);
		return ForgeHooks.onLivingDamage(self, source, ans);
	}


	@WrapOperation(at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraftforge/common/ForgeHooks;onLivingDamage(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/damagesource/DamageSource;F)F"), method = "actuallyHurt")
	public float l2damagetracker$actuallyHurt$moveLivingDamagePost(LivingEntity self, DamageSource source, float damage, Operation<Float> original) {
		return damage;
	}

}
