package dev.xkmc.l2damagetracker.init.data;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2library.serial.config.ConfigDataProvider;
import dev.xkmc.l2tabs.init.L2Tabs;
import dev.xkmc.l2tabs.init.data.AttributeDisplayConfig;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;

public class DTAttributeConfigGen extends ConfigDataProvider {

	public DTAttributeConfigGen(DataGenerator generator) {
		super(generator, "L2DamageTracker Attribute Config Gen");
	}

	@Override
	public void add(Collector collector) {
		collector.add(L2Tabs.ATTRIBUTE_ENTRY,
				new ResourceLocation(L2DamageTracker.MODID, "l2extra"),
				new AttributeDisplayConfig()
						.add(L2DamageTracker.CRIT_RATE.get(), true, 11000, L2DamageTracker.CRIT_RATE.get().getIntrinsic())
						.add(L2DamageTracker.CRIT_DMG.get(), true, 12000, L2DamageTracker.CRIT_DMG.get().getIntrinsic())
						.add(L2DamageTracker.BOW_STRENGTH.get(), true, 13000, L2DamageTracker.BOW_STRENGTH.get().getIntrinsic())
						.add(L2DamageTracker.EXPLOSION_FACTOR.get(), true, 16000, L2DamageTracker.EXPLOSION_FACTOR.get().getIntrinsic())
						.add(L2DamageTracker.FIRE_FACTOR.get(), true, 17000, L2DamageTracker.FIRE_FACTOR.get().getIntrinsic())
						.add(L2DamageTracker.MAGIC_FACTOR.get(), true, 18000, L2DamageTracker.MAGIC_FACTOR.get().getIntrinsic())
						.add(L2DamageTracker.REDUCTION.get(), true, 23000, 0)
						.add(L2DamageTracker.ABSORB.get(), 24000)
		);
	}
}
