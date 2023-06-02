package dev.xkmc.l2damagetracker.contents.materials.api;

import dev.xkmc.l2damagetracker.contents.materials.generic.ExtraToolConfig;
import net.minecraft.world.item.Tier;

public interface IMatToolType {

	Tier getTier();

	IToolStats getToolStats();

	ToolConfig getToolConfig();

	ExtraToolConfig getExtraToolConfig();

}
