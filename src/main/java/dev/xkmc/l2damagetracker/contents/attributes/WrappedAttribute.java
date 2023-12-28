package dev.xkmc.l2damagetracker.contents.attributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class WrappedAttribute extends RangedAttribute {

	private final double intrinsicBase;
	private final double defaultValue;

	public WrappedAttribute(String name, double ins, double def, double min, double max) {
		super(name, def, min - ins, max - ins);
		defaultValue = def;
		intrinsicBase = ins;
	}

	public double getIntrinsic() {
		return intrinsicBase;
	}

	public double getWrappedValue(LivingEntity le) {
		var ins = le.getAttribute(this);
		if (ins == null) return getMinValue() + getIntrinsic();
		if (ins.getBaseValue() != defaultValue) {
			ins.setBaseValue(defaultValue);
		}
		return ins.getValue() + getIntrinsic();
	}

	@Override
	public WrappedAttribute setSyncable(boolean sync) {
		super.setSyncable(sync);
		return this;
	}
}
