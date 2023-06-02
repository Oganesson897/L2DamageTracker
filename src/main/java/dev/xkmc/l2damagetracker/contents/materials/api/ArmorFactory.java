package dev.xkmc.l2damagetracker.contents.materials.api;

import dev.xkmc.l2library.init.materials.api.IMatArmorType;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

@FunctionalInterface
public interface ArmorFactory {

	ArmorItem get(IMatArmorType mat, ArmorItem.Type slot, Item.Properties props);

}
