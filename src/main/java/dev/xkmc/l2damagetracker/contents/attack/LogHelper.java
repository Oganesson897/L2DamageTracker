package dev.xkmc.l2damagetracker.contents.attack;

import dev.xkmc.l2damagetracker.init.data.L2DamageTrackerConfig;
import net.minecraft.world.entity.player.Player;

public class LogHelper {

	public static boolean savePlayerHurt(Player player) {
		return L2DamageTrackerConfig.COMMON.savePlayerHurt.get();
	}

	public static boolean savePlayerAttack(Player player) {
		return L2DamageTrackerConfig.COMMON.savePlayerAttack.get();
	}
}
