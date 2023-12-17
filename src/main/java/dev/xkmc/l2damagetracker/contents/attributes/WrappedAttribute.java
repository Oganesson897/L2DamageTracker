package dev.xkmc.l2damagetracker.contents.attributes;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

public class WrappedAttribute extends RangedAttribute {

	private final double intrinsicBase;

	public WrappedAttribute(String name, double def, double min, double max) {
		super(name, 0, min - def, max - def);
		intrinsicBase = def;
	}

	public double getIntrinsic() {
		return intrinsicBase;
	}

	public double getWrappedValue(LivingEntity le) {
		var ins = le.getAttribute(this);
		if (ins == null) return getMinValue() + getIntrinsic();
		return ins.getValue() + getIntrinsic();
	}

	@Override
	public WrappedAttribute setSyncable(boolean sync) {
		super.setSyncable(sync);
		return this;
	}
}
