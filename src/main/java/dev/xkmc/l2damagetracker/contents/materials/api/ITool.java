package dev.xkmc.l2damagetracker.contents.materials.api;

import dev.xkmc.l2damagetracker.contents.materials.generic.ExtraToolConfig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

public interface ITool {

	int getDamage(int base_damage);

	float getSpeed(float base_speed);

	Item create(Tier tier, int damage, float speed, Item.Properties prop, ExtraToolConfig config);

}
