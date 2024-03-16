package dev.xkmc.l2damagetracker.contents.attributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class WrappedAttribute extends RangedAttribute {


	public WrappedAttribute(String name, double def, double min, double max) {
		super(name, def, min, max);
	}

	public double getWrappedValue(LivingEntity le) {
		var ins = le.getAttribute(this);
		if (ins == null) return getMinValue();
		if (ins.getBaseValue() != 0) {
			ins.setBaseValue(0);
		}
		return ins.getValue();
	}

	@Override
	public WrappedAttribute setSyncable(boolean sync) {
		super.setSyncable(sync);
		return this;
	}
}
