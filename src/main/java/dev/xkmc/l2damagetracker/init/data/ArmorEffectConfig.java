package dev.xkmc.l2damagetracker.init.data;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2library.serial.config.BaseConfig;
import dev.xkmc.l2library.serial.config.CollectType;
import dev.xkmc.l2library.serial.config.ConfigCollect;
import dev.xkmc.l2library.util.annotation.DataGenOnly;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.world.effect.MobEffect;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@SerialClass
public class ArmorEffectConfig extends BaseConfig {

	public static ArmorEffectConfig get() {
		return L2DamageTracker.ARMOR.getMerged();
	}

	@SerialClass.SerialField
	@ConfigCollect(CollectType.MAP_COLLECT)
	private final LinkedHashMap<String, LinkedHashSet<MobEffect>> immune = new LinkedHashMap<>();

	public boolean isEffectBlocking(String prefix) {
		return immune.containsKey(prefix);
	}

	public Set<MobEffect> getImmunity(String prefix) {
		return immune.get(prefix);
	}

	@DataGenOnly
	public ArmorEffectConfig add(String id, MobEffect... effects) {
		immune.put(id, new LinkedHashSet<>(List.of(effects)));
		return this;
	}

}
