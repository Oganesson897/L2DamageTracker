package dev.xkmc.l2damagetracker.init;

import com.tterrag.registrate.providers.ProviderType;
import com.tterrag.registrate.util.entry.RegistryEntry;
import dev.xkmc.l2damagetracker.contents.damage.DamageTypeRoot;
import dev.xkmc.l2damagetracker.events.GeneralAttackListener;
import dev.xkmc.l2library.base.L2Registrate;
import dev.xkmc.l2library.init.data.L2DamageTypes;
import dev.xkmc.l2library.init.events.attack.AttackEventHandler;
import dev.xkmc.l2library.serial.config.PacketHandlerWithConfig;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(L2DamageTracker.MODID)
@Mod.EventBusSubscriber(modid = L2DamageTracker.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class L2DamageTracker {

	public static final String MODID = "l2damagetracker";

	public static final L2Registrate REGISTRATE = new L2Registrate(MODID);

	public static final PacketHandlerWithConfig PACKET_HANDLER = new PacketHandlerWithConfig(new ResourceLocation(MODID, "main"), 1,
			"l2damagetracker_config"
	);

	public static final RegistryEntry<Attribute> CRIT_RATE = REGISTRATE.simple("crit_rate", ForgeRegistries.ATTRIBUTES.getRegistryKey(), () -> new RangedAttribute("attribute.name.crit_rate", 0, 0, 1).setSyncable(true));
	public static final RegistryEntry<Attribute> CRIT_DMG = REGISTRATE.simple("crit_damage", ForgeRegistries.ATTRIBUTES.getRegistryKey(), () -> new RangedAttribute("attribute.name.crit_damage", 0.5, 0, 1000).setSyncable(true));
	public static final RegistryEntry<Attribute> BOW_STRENGTH = REGISTRATE.simple("bow_strength", ForgeRegistries.ATTRIBUTES.getRegistryKey(), () -> new RangedAttribute("attribute.name.bow_strength", 1, 0, 1000).setSyncable(true));


	public L2DamageTracker() {
		L2DamageTypes.register();
		AttackEventHandler.register(0, new GeneralAttackListener());
		REGISTRATE.addDataGenerator(ProviderType.LANG, L2DTLangData::genLang);
	}

	@SubscribeEvent
	public static void modifyAttributes(EntityAttributeModificationEvent event) {
		event.add(EntityType.PLAYER, CRIT_RATE.get());
		event.add(EntityType.PLAYER, CRIT_DMG.get());
		event.add(EntityType.PLAYER, BOW_STRENGTH.get());
	}

	@SubscribeEvent
	public static void gatherData(GatherDataEvent event) {
		boolean gen = event.includeServer();
		PackOutput output = event.getGenerator().getPackOutput();
		var pvd = event.getLookupProvider();
		var helper = event.getExistingFileHelper();
		new L2DamageTypes(output, pvd, helper).generate(gen, event.getGenerator());
	}

	@SubscribeEvent
	public static void setup(FMLCommonSetupEvent event) {
		DamageTypeRoot.generateAll();
		event.enqueueWork(() -> {
			//TODO
			//AttributeEntry.add(CRIT_RATE, true, 11000);
			//AttributeEntry.add(CRIT_DMG, true, 12000);
			//AttributeEntry.add(BOW_STRENGTH, true, 12000);
		});
	}

}
