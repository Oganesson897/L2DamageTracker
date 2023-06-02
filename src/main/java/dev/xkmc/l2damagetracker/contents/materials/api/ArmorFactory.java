package dev.xkmc.l2damagetracker.contents.materials.api;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

@FunctionalInterface
public interface ArmorFactory {

	ArmorItem get(IMatArmorType mat, ArmorItem.Type slot, Item.Properties props);

}
