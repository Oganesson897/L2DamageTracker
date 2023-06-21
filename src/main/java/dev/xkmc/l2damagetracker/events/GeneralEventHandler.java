package dev.xkmc.l2damagetracker.events;

import dev.xkmc.l2damagetracker.contents.materials.generic.GenericArmorItem;
import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = L2DamageTracker.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class GeneralEventHandler {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void onPotionTest(MobEffectEvent.Applicable event) {
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			if (slot.getType() != EquipmentSlot.Type.ARMOR) continue;
			ItemStack stack = event.getEntity().getItemBySlot(slot);
			if (!stack.isEmpty()) {
				if (stack.getItem() instanceof GenericArmorItem armor) {
					if (armor.getConfig().immuneToEffect(stack, armor, event.getEffectInstance())) {
						event.setResult(Event.Result.DENY);
						return;
					}
				}
			}
		}
	}

}
