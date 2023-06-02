package dev.xkmc.l2damagetracker.contents.damage;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;

public interface DamageWrapperTagProvider {

	TagsProvider.TagAppender<DamageType> tag(TagKey<DamageType> tag);

}
