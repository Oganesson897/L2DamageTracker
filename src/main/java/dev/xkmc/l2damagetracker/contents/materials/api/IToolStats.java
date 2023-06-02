package dev.xkmc.l2damagetracker.contents.materials.api;

import dev.xkmc.l2library.init.materials.api.ITool;

public interface IToolStats {

	int durability();

	int speed();

	int enchant();

	int getDamage(ITool tool);

	float getSpeed(ITool tool);

}
