package com.xxsx.fantasyearth.entity;

import com.xxsx.earthlore.api.LoreApi;
import com.xxsx.fantasyearth.ArcaneVfx;
import com.xxsx.fantasyearth.ArcaneFocus;
import com.xxsx.fantasyearth.FantasyEarth;
import com.xxsx.fantasyearth.SpellCatalog;
import com.xxsx.fantasyearth.SpellScrollComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.LookAtTradingPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TradeWithPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public final class ArcaneSettlerEntity extends AbstractVillager {
    private static final EntityDataAccessor<Integer> DATA_ROLE = SynchedEntityData.defineId(
            ArcaneSettlerEntity.class, EntityDataSerializers.INT);
    private int counterAttackCooldown;

    public ArcaneSettlerEntity(EntityType<? extends ArcaneSettlerEntity> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 28.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.43D)
                .add(Attributes.FOLLOW_RANGE, 24.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.5D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.counterAttackCooldown > 0) {
            this.counterAttackCooldown--;
        }
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean hurt = super.hurtServer(level, source, amount);
        if (hurt && this.isAlive()) {
            Entity attackerEntity = source.getEntity();
            if (attackerEntity instanceof LivingEntity attacker && attacker != this && this.counterAttackCooldown <= 0) {
                this.counterAttackCooldown = 25;
                this.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 80, 0, true, false, true));
                this.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 100, 0, true, false, true));
                float damage = 4.0F + getRole().id() * 1.5F;
                attacker.hurtServer(level, level.damageSources().magic(), damage);
                attacker.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 70, 1, false, true, true));
                Vec3 knock = attacker.position().subtract(this.position());
                if (knock.lengthSqr() > 0.001D) {
                    Vec3 push = knock.normalize().scale(0.65D);
                    attacker.push(push.x, 0.25D, push.z);
                }
                ArcaneVfx.focusCast(level, this.position(), ArcaneFocus.EVOCATION);
                this.playSound(SoundEvents.ENCHANTMENT_TABLE_USE, 0.9F, 1.2F);
            }
        }
        return hurt;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource damageSource, boolean recentlyHit) {
        super.dropCustomDeathLoot(level, damageSource, recentlyHit);
        this.spawnAtLocation(level, new ItemStack(FantasyEarth.ARCANE_DUST.get(), 1 + this.random.nextInt(3)));
        this.spawnAtLocation(level, new ItemStack(FantasyEarth.AETHER_SHARD.get(), 1 + this.random.nextInt(2)));
        if (this.random.nextBoolean()) {
            ItemStack scroll = new ItemStack(FantasyEarth.SPELL_SCROLL.get());
            scroll.set(FantasyEarth.SPELL_SCROLL_COMPONENT.get(),
                    new SpellScrollComponent(SpellCatalog.EVOCATION.id(), 1));
            this.spawnAtLocation(level, scroll);
        } else {
            this.spawnAtLocation(level, new ItemStack(FantasyEarth.RUNE_BLANK.get(), 1 + this.random.nextInt(2)));
        }
        if (getRole() == Role.ACADEMY_RESEARCHER) {
            this.spawnAtLocation(level, new ItemStack(FantasyEarth.CRYSTALLIZED_MANA_SALT.get(), 1));
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_ROLE, Role.HEDGE_WITCH.id());
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("ArcaneRole", getRole().id());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setRole(Role.byId(input.getIntOr("ArcaneRole", 0)));
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new TradeWithPlayerGoal(this));
        goalSelector.addGoal(1, new LookAtTradingPlayerGoal(this));
        goalSelector.addGoal(2, new AvoidEntityGoal<>(this, Monster.class, 9.0F, 0.7D, 0.8D));
        goalSelector.addGoal(3, new PanicGoal(this, 0.8D));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.62D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!isAlive() || isTrading() || isBaby()) {
            return super.mobInteract(player, hand);
        }
        if (hand == InteractionHand.MAIN_HAND) {
            player.awardStat(Stats.TALKED_TO_VILLAGER);
        }
        if (!level().isClientSide()) {
            if (getOffers().isEmpty()) {
                return InteractionResult.CONSUME;
            }
            setTradingPlayer(player);
            openTradingScreen(player, Component.translatable(getRole().translationKey()), 1);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void updateTrades(ServerLevel level) {
        MerchantOffers offers = getOffers();
        switch (getRole()) {
            case HEDGE_WITCH -> {
                add(offers, Items.EMERALD, 2, new ItemStack(FantasyEarth.ARCANE_DUST.get(), 3), 12, 2);
                add(offers, Items.EMERALD, 3, new ItemStack(FantasyEarth.RUNE_INK.get(), 2), 10, 3);
                add(offers, Items.GLOWSTONE_DUST, 6, new ItemStack(FantasyEarth.CRYSTALLIZED_MANA_SALT.get()), 8, 5);
            }
            case GOBLIN_APPRAISER -> {
                add(offers, Items.COPPER_INGOT, 5, new ItemStack(FantasyEarth.RUNE_COPPER_PLATE.get()), 12, 3);
                add(offers, Items.EMERALD, 4, new ItemStack(FantasyEarth.AETHER_GLASS.get(), 2), 8, 4);
                add(offers, Items.GOLD_INGOT, 3, new ItemStack(FantasyEarth.FAMILIAR_CONTRACT.get()), 6, 6);
            }
            case ACADEMY_RESEARCHER -> {
                add(offers, Items.EMERALD, 8, new ItemStack(FantasyEarth.ARCANE_INITIATION_NOTES.get()), 4, 8);
                add(offers, Items.EMERALD, 7, new ItemStack(FantasyEarth.ARCANE_BODY_WARD_NOTES.get()), 4, 8);
                add(offers, Items.AMETHYST_SHARD, 8, new ItemStack(FantasyEarth.AETHER_CRYSTAL.get()), 6, 6);
            }
        }
        offers.addAll(LoreApi.teachingOffers(level, getType()));
    }

    private static void add(MerchantOffers offers, net.minecraft.world.level.ItemLike cost, int count,
                            ItemStack result, int maxUses, int xp) {
        offers.add(new MerchantOffer(new ItemCost(cost, count), result, maxUses, xp, 0.05F));
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer) {
        if (offer.shouldRewardExp()) {
            level().addFreshEntity(new ExperienceOrb(level(), getX(), getY() + 0.5D, getZ(),
                    3 + random.nextInt(4)));
        }
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        ArcaneSettlerEntity child = FantasyEarth.ARCANE_SETTLER.get().create(level, EntitySpawnReason.BREEDING);
        if (child != null) {
            child.setRole(random.nextBoolean() ? getRole()
                    : partner instanceof ArcaneSettlerEntity settler ? settler.getRole() : getRole());
        }
        return child;
    }

    @Override
    protected Component getTypeName() {
        return Component.translatable(getRole().translationKey());
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return isTrading() ? SoundEvents.WANDERING_TRADER_TRADE : SoundEvents.WANDERING_TRADER_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WANDERING_TRADER_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WANDERING_TRADER_DEATH;
    }

    public Role getRole() {
        return Role.byId(entityData.get(DATA_ROLE));
    }

    public void setRole(Role role) {
        entityData.set(DATA_ROLE, role.id());
        offers = null;
    }

    public enum Role {
        HEDGE_WITCH(0, "entity.fantasy_earth.arcane_settler.hedge_witch"),
        GOBLIN_APPRAISER(1, "entity.fantasy_earth.arcane_settler.goblin_appraiser"),
        ACADEMY_RESEARCHER(2, "entity.fantasy_earth.arcane_settler.academy_researcher");

        private final int id;
        private final String translationKey;

        Role(int id, String translationKey) {
            this.id = id;
            this.translationKey = translationKey;
        }

        public int id() {
            return id;
        }

        public String translationKey() {
            return translationKey;
        }

        public static Role byId(int id) {
            for (Role role : values()) {
                if (role.id == id) {
                    return role;
                }
            }
            return HEDGE_WITCH;
        }
    }
}
