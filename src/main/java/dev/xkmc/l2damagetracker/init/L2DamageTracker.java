package dev.xkmc.l2damagetracker.init;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2damagetracker.contents.attack.AttackEventHandler;
import dev.xkmc.l2damagetracker.contents.attributes.WrappedAttribute;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeRoot;
import dev.xkmc.l2damagetracker.events.ArsEventCompat;
import dev.xkmc.l2damagetracker.events.GeneralAttackListener;
import dev.xkmc.l2damagetracker.init.data.*;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2tabs.init.L2Tabs;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(L2DamageTracker.MODID)
@Mod.EventBusSubscriber(modid = L2DamageTracker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2DamageTracker {

	public static final String MODID = "l2damagetracker";

	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandlerWithConfig PACKET_HANDLER = new PacketHandlerWithConfig(new ResourceLocation(MODID, "main"), 1);

	public static final RegistryEntry<WrappedAttribute> CRIT_RATE = regWrapped("crit_rate", 0, 0, 1, "Weapon Crit Rate");
	public static final RegistryEntry<WrappedAttribute> CRIT_DMG = regWrapped("crit_damage", 0, 0.5, 1000, "Weapon Crit Damage");
	public static final RegistryEntry<WrappedAttribute> BOW_STRENGTH = regWrapped("bow_strength", 0, 1, 1000, "Projectile Strength");
	public static final RegistryEntry<WrappedAttribute> EXPLOSION_FACTOR = regWrapped("explosion_damage", 1, 0, 1000, "Explosion Damage");
	public static final RegistryEntry<WrappedAttribute> FIRE_FACTOR = regWrapped("fire_damage", 0, 1, 1000, "Fire Damage");
	public static final RegistryEntry<WrappedAttribute> MAGIC_FACTOR = regWrapped("magic_damage", 0, 1, 1000, "Magic Damage");

	public static final ConfigTypeEntry<ArmorEffectConfig> ARMOR =
			new ConfigTypeEntry<>(PACKET_HANDLER, "armor", ArmorEffectConfig.class);

	public L2DamageTracker() {
		L2DamageTrackerConfig.init();
		L2DamageTypes.register();
		AttackEventHandler.register(1000, new GeneralAttackListener());
		REGISTRATE.addDataGenerator(ProviderType.LANG, L2DTLangData::genLang);
		if (ModList.get().isLoaded(ArsNouveau.MODID)) {
			MinecraftForge.EVENT_BUS.register(ArsEventCompat.class);
		}
	}

	@SubscribeEvent
	public static void modifyAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, CRIT_RATE.get());
		event.add(EntityType.PLAYER, CRIT_DMG.get());
		for (var e : event.getTypes()) {
			event.add(e, BOW_STRENGTH.get());
			event.add(e, EXPLOSION_FACTOR.get());
			event.add(e, FIRE_FACTOR.get());
			event.add(e, MAGIC_FACTOR.get());
		}
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		boolean gen = event.includeServer();
		PackOutput output = event.getGenerator().getPackOutput();
		var pvd = event.getLookupProvider();
		var helper = event.getExistingFileHelper();
		new L2DamageTypes(output, pvd, helper).generate(gen, event.getGenerator());
		if (ModList.get().isLoaded(L2Tabs.MODID)) {
			event.getGenerator().addProvider(event.includeServer(), new DTAttributeConfigGen(event.getGenerator()));
		}
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
		DamageTypeRoot.generateAll();
	}

	private static RegistryEntry<Attribute> reg(String id, double def, double max, String name) {
		REGISTRATE.addRawLang("attribute.name." + id, name);
		return REGISTRATE.simple(id, ForgeRegistries.ATTRIBUTES.getRegistryKey(),
				() -> new RangedAttribute("attribute.name." + id, def, 0, max)
						.setSyncable(true));
	}

	private static RegistryEntry<WrappedAttribute> regWrapped(String id, double ins, double def, double max, String name) {
		REGISTRATE.addRawLang("attribute.name." + id, name);
		return REGISTRATE.simple(id, ForgeRegistries.ATTRIBUTES.getRegistryKey(),
				() -> new WrappedAttribute("attribute.name." + id, ins, def, 0, max)
						.setSyncable(true));
	}

}
