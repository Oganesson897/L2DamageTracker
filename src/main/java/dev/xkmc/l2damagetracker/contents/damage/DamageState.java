package dev.xkmc.l2damagetracker.contents.damage;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

import java.util.Comparator;
import java.util.TreeSet;
import java.util.function.Consumer;

public interface DamageState {

	static TreeSet<DamageState> newSet() {
		return new TreeSet<>(Comparator.comparing(DamageState::getId));
	}

	void gatherTags(Consumer<TagKey<DamageType>> collector);

	void removeTags(Consumer<TagKey<DamageType>> collector);

	ResourceLocation getId();

	default boolean overrides(DamageState state) {
		return false;
	}

}
