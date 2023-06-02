package dev.xkmc.l2damagetracker.contents.materials.api;

import dev.xkmc.l2damagetracker.contents.materials.generic.ExtraArmorConfig;
import net.minecraft.world.item.ArmorMaterial;

public interface IMatArmorType {

	ArmorMaterial getArmorMaterial();

	ArmorConfig getArmorConfig();

	ExtraArmorConfig getExtraArmorConfig();

}
