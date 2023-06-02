package dev.xkmc.l2damagetracker.contents.materials.api;

public interface IToolStats {

	int durability();

	int speed();

	int enchant();

	int getDamage(ITool tool);

	float getSpeed(ITool tool);

}
