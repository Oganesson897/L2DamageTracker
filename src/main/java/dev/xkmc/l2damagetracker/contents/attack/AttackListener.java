package dev.xkmc.l2damagetracker.contents.attack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface AttackListener {

	default void onPlayerAttack(PlayerAttackCache cache) {
	}

	default boolean onCriticalHit(PlayerAttackCache cache, CriticalHitEvent event) {
		return false;
	}

	default void setupProfile(AttackCache attackCache, BiConsumer<LivingEntity, ItemStack> setupProfile) {
	}

	default void onAttack(AttackCache cache, ItemStack weapon) {
	}

	default void onHurt(AttackCache cache, ItemStack weapon) {
	}

	default void onHurtMaximized(AttackCache cache, ItemStack weapon) {
	}

	default void onDamage(AttackCache cache, ItemStack weapon) {
	}

	default void onDamageFinalized(AttackCache cache, ItemStack weapon) {
	}

	default void onCreateSource(CreateSourceEvent event) {
	}

}
