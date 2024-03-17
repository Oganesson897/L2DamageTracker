package dev.xkmc.l2damagetracker.init;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.providers.RegistrateTagsProvider;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2damagetracker.contents.attack.AttackEventHandler;
import dev.xkmc.l2damagetracker.contents.attributes.WrappedAttribute;
import dev.xkmc.l2damagetracker.contents.curios.TotemUseToClient;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeRoot;
import dev.xkmc.l2damagetracker.events.ArsEventCompat;
import dev.xkmc.l2damagetracker.events.GeneralAttackListener;
import dev.xkmc.l2damagetracker.init.data.*;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.serial.config.ConfigTypeEntry;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import dev.xkmc.l2tabs.init.L2Tabs;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT;

@Mod(L2DamageTracker.MODID)
@SuppressWarnings("unchecked")
@Mod.EventBusSubscriber(modid = L2DamageTracker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2DamageTracker {

	public static final String MODID = "l2damagetracker";
	public static final Logger LOGGER = LogManager.getLogger();
	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandlerWithConfig PACKET_HANDLER = new PacketHandlerWithConfig(
			new ResourceLocation(MODID, "main"), 2,
			e -> e.create(TotemUseToClient.class, PLAY_TO_CLIENT));

	public static final ProviderType<RegistrateTagsProvider.IntrinsicImpl<Attribute>> ATTR_TAGS =
			ProviderType.register("tags/attribute", type -> (p, e) ->
					new RegistrateTagsProvider.IntrinsicImpl<>(p, type, "attributes",
							e.getGenerator().getPackOutput(), Registries.ATTRIBUTE, e.getLookupProvider(),
							reg -> ForgeRegistries.ATTRIBUTES.getResourceKey(reg).orElseThrow(),
							e.getExistingFileHelper()));

	public static final TagKey<Attribute> PERCENTAGE = key("percentage");
	public static final TagKey<Attribute> NEGATIVE = key("negative");

	public static final RegistryEntry<WrappedAttribute> CRIT_RATE = regWrapped(REGISTRATE, "crit_rate", 0, 0, 1, "Weapon Crit Rate", PERCENTAGE);
	public static final RegistryEntry<WrappedAttribute> CRIT_DMG = regWrapped(REGISTRATE, "crit_damage", 0.5, 0, 1000, "Weapon Crit Damage", PERCENTAGE);
	public static final RegistryEntry<WrappedAttribute> BOW_STRENGTH = regWrapped(REGISTRATE, "bow_strength", 1, 0, 1000, "Projectile Strength", PERCENTAGE);
	public static final RegistryEntry<WrappedAttribute> EXPLOSION_FACTOR = regWrapped(REGISTRATE, "explosion_damage", 1, 0, 1000, "Explosion Damage", PERCENTAGE);
	public static final RegistryEntry<WrappedAttribute> FIRE_FACTOR = regWrapped(REGISTRATE, "fire_damage", 1, 0, 1000, "Fire Damage", PERCENTAGE);
	public static final RegistryEntry<WrappedAttribute> MAGIC_FACTOR = regWrapped(REGISTRATE, "magic_damage", 1, 0, 1000, "Magic Damage", PERCENTAGE);
	public static final RegistryEntry<WrappedAttribute> ABSORB = regWrapped(REGISTRATE, "damage_absorption", 0, 0, 10000, "Damage Absorption");
	public static final RegistryEntry<WrappedAttribute> REDUCTION = regWrapped(REGISTRATE, "damage_reduction", 1, -10000, 10000, "Damage after Reduction", PERCENTAGE, NEGATIVE);

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
			event.add(e, REDUCTION.get());
			event.add(e, ABSORB.get());
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

	@SuppressWarnings({"unchecked"})
	public static RegistryEntry<WrappedAttribute> regWrapped(L2Registrate reg, String id, double def, double min, double max, String name, TagKey<Attribute>... keys) {
		reg.addRawLang("attribute." + reg.getModid() + "." + id, name);
		return reg.generic(reg, id, ForgeRegistries.ATTRIBUTES.getRegistryKey(),
				() -> new WrappedAttribute("attribute." + reg.getModid() + "." + id, def, min, max)
						.setSyncable(true)).tag(ATTR_TAGS, keys).register();
	}

	public static TagKey<Attribute> key(String id) {
		return TagKey.create(Registries.ATTRIBUTE, new ResourceLocation(MODID, id));
	}

}
