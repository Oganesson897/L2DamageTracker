package dev.xkmc.l2damagetracker.contents.attack;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
public class AttackCache {

	int recursive = 0;

	private Stage stage = Stage.PREINIT;
	private PlayerAttackCache player;
	private LivingAttackEvent attack;
	private LivingHurtEvent hurt;
	private LivingDamageEvent damage;

	private LivingEntity target;
	private LivingEntity attacker;

	private ItemStack weapon = ItemStack.EMPTY;

	private float damage_pre;

	private final DamageAccumulator hurtDamage = new DamageAccumulator();
	private final DamageAccumulator dealtDamage = new DamageAccumulator();

	void pushAttackPre(LivingAttackEvent event) {
		stage = Stage.HURT_PRE;
		attack = event;
		target = attack.getEntity();
		attacker = attack.getSource().getEntity() instanceof LivingEntity le ? le : null;
		AttackEventHandler.getListeners().forEach(e -> e.setupProfile(this, this::setupAttackerProfile));
		damage_pre = event.getAmount();
		AttackEventHandler.getListeners().forEach(e -> e.onAttack(this, weapon));
	}

	void pushAttackPost(LivingAttackEvent event) {
		stage = Stage.HURT_POST;
		AttackEventHandler.getListeners().forEach(e -> e.postAttack(this, event, weapon));
	}

	void pushHurtPre(LivingHurtEvent event) {
		stage = Stage.ACTUALLY_HURT_PRE;
		hurt = event;
		float damage = hurtDamage.run(event.getAmount(),
				e -> e.onHurt(this, weapon),
				e -> e.onHurtMaximized(this, weapon));
		event.setAmount(damage);
	}

	void pushHurtPost(LivingHurtEvent event) {
		stage = Stage.ACTUALLY_HURT_POST;
		AttackEventHandler.getListeners().forEach(e -> e.postHurt(this, event, weapon));
	}

	void pushDamagePre(LivingDamageEvent event) {
		stage = Stage.DAMAGE;
		damage = event;
		float damage = dealtDamage.run(event.getAmount(),
				e -> e.onDamage(this, weapon),
				e -> e.onDamageFinalized(this, weapon));
		event.setAmount(damage);
	}

	void setupAttackerProfile(@Nullable LivingEntity entity, @Nullable ItemStack stack) {
		if (attacker == null && entity != null) attacker = entity;
		if (weapon.isEmpty() && stack != null) weapon = stack;
	}

	public Stage getStage() {
		return stage;
	}

	@Nullable
	public AttackEntityEvent getPlayerAttackEntityEvent() {
		return player == null ? null : player.getPlayerAttackEntityEvent();
	}

	@Nullable
	public CriticalHitEvent getCriticalHitEvent() {
		return player == null ? null : player.getCriticalHitEvent();
	}

	@Nullable
	public LivingAttackEvent getLivingAttackEvent() {
		return attack;
	}

	@Nullable
	public LivingHurtEvent getLivingHurtEvent() {
		return hurt;
	}

	@Nullable
	public LivingDamageEvent getLivingDamageEvent() {
		return damage;
	}

	public LivingEntity getAttackTarget() {
		return target;
	}

	@Nullable
	public LivingEntity getAttacker() {
		return attacker;
	}

	public ItemStack getWeapon() {
		return weapon;
	}

	public float getStrength() {
		return player == null ? 1 : player.getStrength();
	}

	public float getPreDamageOriginal() {
		if (stage.ordinal() < Stage.HURT_PRE.ordinal())
			throw new IllegalStateException("dealt damage not calculated yet");
		return damage_pre;
	}

	public float correctPreDamageOriginal(float actual) {
		if (stage.ordinal() < Stage.HURT_PRE.ordinal())
			throw new IllegalStateException("dealt damage not calculated yet");
		return damage_pre;
	}

	public float getPreDamage() {
		return hurtDamage.getMaximized();
	}

	public void addHurtModifier(DamageModifier mod) {
		hurtDamage.addHurtModifier(mod);
	}

	public float getDamageDealt() {
		return dealtDamage.getMaximized();
	}

	public void addDealtModifier(DamageModifier mod) {
		dealtDamage.addHurtModifier(mod);
	}

	public void setupPlayer(PlayerAttackCache prev) {
		player = prev;
		attacker = prev.getAttacker();
		if (!prev.getWeapon().isEmpty()) weapon = prev.getWeapon();
	}

}
