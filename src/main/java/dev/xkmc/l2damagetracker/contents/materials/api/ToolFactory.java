package dev.xkmc.l2damagetracker.contents.materials.api;

import dev.xkmc.l2library.init.materials.api.IMatToolType;
import dev.xkmc.l2library.init.materials.api.ITool;
import net.minecraft.world.item.Item;

@FunctionalInterface
public interface ToolFactory {

	Item get(IMatToolType mat, ITool tool, Item.Properties props);

}
