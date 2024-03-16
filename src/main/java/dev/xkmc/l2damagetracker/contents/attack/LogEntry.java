package dev.xkmc.l2damagetracker.contents.attack;

import dev.xkmc.l2damagetracker.init.L2DamageTracker;
import dev.xkmc.l2damagetracker.init.data.L2DamageTrackerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public class LogEntry {

	public enum Stage {
		ATTACK, HURT_PRE, HURT_L2, HURT_POST, DAMAGE_PRE, DAMAGE_FINAL,
	}

	public static LogEntry of(DamageSource source, LivingEntity target, @Nullable LivingEntity attacker) {
		return new LogEntry(source, target, attacker);
	}

	private static Path path(Player player, @Nullable LivingEntity other, String type, String time) {
		String otherType;
		if (other == null) {
			otherType = "null";
		} else {
			ResourceLocation rl = ForgeRegistries.ENTITY_TYPES.getKey(other.getType());
			assert rl != null;
			otherType = rl.getPath().replaceAll("/", "_");
		}
		return FMLPaths.GAMEDIR.get().resolve("logs/damage_tracker/" +
				player.getScoreboardName() + "-" + type + "/" + otherType + "/" + time + ".txt");
	}

	private final DamageSource source;
	private final LivingEntity target;
	@Nullable
	private final LivingEntity attacker;
	private final String time;
	private final boolean log, info, trace;
	private final List<String> output = new ArrayList<>();
	private final Map<DamageModifier, String> modifiers = new HashMap<>();
	private final Path playerAttack;
	private final Path playerHurt;

	private LogEntry(DamageSource source, LivingEntity target, @Nullable LivingEntity attacker) {
		this.source = source;
		this.target = target;
		this.attacker = attacker;
		this.time = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS").format(new Date());
		info = L2DamageTrackerConfig.COMMON.printDamageTrace.get();
		playerHurt = target instanceof Player player && LogHelper.savePlayerHurt(player) ?
				path(player, attacker, "hurt", time) : null;
		playerAttack = attacker instanceof Player player && LogHelper.savePlayerAttack(player) ?
				path(player, target, "attack", time) : null;
		trace = playerHurt != null || playerAttack != null;
		log = info || trace;
		if (log) {
			output.add("------ Damage Tracker Profile START ------");
			output.add("Attacked Entity: " + target);
			output.add("Attacker Entity: " + attacker);
			output.add("Damage Source: " + source.typeHolder().unwrapKey()
					.map(e -> e.location().toString())
					.orElseGet(() -> source.type().msgId()));
		}
	}

	public void log(Stage stage, float amount) {
		if (!log) return;
		output.add("Stage " + stage.name() + ": val = " + amount);
		if (stage == Stage.DAMAGE_FINAL) {
			output.add("------ Damage Tracker Profile END ------");
			if (info) {
				for (var e : output) {
					L2DamageTracker.LOGGER.info(e);
				}
			}
			if (playerAttack != null) {
				write(playerAttack, e -> output.forEach(e::println));
			}
			if (playerHurt != null) {
				write(playerHurt, e -> output.forEach(e::println));
			}
		}
	}

	@Nullable
	public LogEntry initModifiers() {
		modifiers.clear();
		return trace ? this : null;
	}

	public void recordModifier(DamageModifier mod) {
		if (!trace) return;
		modifiers.put(mod, getStackTrace());
	}

	public void startLayer(DamageModifier.Order key, float val) {
		output.add("| - Layer " + key.name() + " start, val = " + val);
	}

	public void processModifier(DamageModifier e, String info) {
		output.add("| - | " + info + ", source = " + modifiers.get(e));
	}

	public void endLayer(DamageModifier.Order key, float val) {
		output.add("| - Layer " + key.name() + " end, val = " + val);
	}

	private static void write(Path path, Consumer<PrintStream> cons) {
		PrintStream stream = null;
		try {
			stream = getStream(path);
			cons.accept(stream);
		} catch (Exception e) {
			L2DamageTracker.LOGGER.throwing(Level.ERROR, e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception e) {
					L2DamageTracker.LOGGER.throwing(Level.FATAL, e);
				}
			}
		}
	}

	private static PrintStream getStream(Path path) throws IOException {
		File file = path.toFile();
		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				if (!file.getParentFile().mkdirs()) {
					throw new IOException("failed to create directory " + file.getParentFile());
				}
			}
			if (!file.createNewFile()) {
				throw new IOException("failed to create file " + file);
			}
		}
		return new PrintStream(file);
	}

	private static String getStackTrace() {
		var trace = new Throwable().getStackTrace();
		for (var e : trace) {
			if (e.getClassName().startsWith("dev.xkmc.l2damagetracker.contents.attack"))
				continue;
			return e.toString();
		}
		return "unknown";
	}

}
