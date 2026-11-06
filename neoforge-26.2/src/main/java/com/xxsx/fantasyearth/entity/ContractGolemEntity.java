package com.xxsx.fantasyearth.entity;

import com.xxsx.fantasyearth.FantasyEarth;
import com.xxsx.fantasyearth.FantasyEarthParticles;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.EnumSet;
import java.util.UUID;

/** FB-09 Contract Golem: summoned by F-037 construct_core, 50 HP, speed 0.22, attack 7, armor 10, lifetime 24000, absorbs 20% owner damage. */
public final class ContractGolemEntity extends PathfinderMob {
    public static final int DEFAULT_LIFETIME_TICKS = 24000;
    private static final EntityDataAccessor<Integer> DATA_LIFETIME = SynchedEntityData.defineId(
            ContractGolemEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_OWNER = SynchedEntityData.defineId(
            ContractGolemEntity.class, EntityDataSerializers.STRING);

    private boolean droppedCore = false;

    public ContractGolemEntity(EntityType<? extends ContractGolemEntity> entityType, Level level) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.22D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75D)
                .add(Attributes.FOLLOW_RANGE, 28.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_LIFETIME, DEFAULT_LIFETIME_TICKS);
        builder.define(DATA_OWNER, "");
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("LifetimeTicks", getLifetime());
        output.putString("OwnerUuid", entityData.get(DATA_OWNER));
        output.putBoolean("DroppedCore", droppedCore);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setLifetime(input.getIntOr("LifetimeTicks", DEFAULT_LIFETIME_TICKS));
        entityData.set(DATA_OWNER, input.getStringOr("OwnerUuid", ""));
        droppedCore = input.getBooleanOr("DroppedCore", false);
    }

    public int getLifetime() {
        return entityData.get(DATA_LIFETIME);
    }

    public void setLifetime(int ticks) {
        entityData.set(DATA_LIFETIME, Math.max(0, Math.min(DEFAULT_LIFETIME_TICKS, ticks)));
    }

    public @Nullable UUID getOwnerUuid() {
        String raw = entityData.get(DATA_OWNER);
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setOwnerUuid(@Nullable UUID uuid) {
        entityData.set(DATA_OWNER, uuid == null ? "" : uuid.toString());
    }

    public @Nullable Player getOwnerPlayer() {
        UUID id = getOwnerUuid();
        if (id == null) {
            return null;
        }
        return level().getPlayerByUUID(id);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.05D, true));
        this.goalSelector.addGoal(3, new FollowSummonerGoal(this, 1.1D, 8.0F, 2.5F));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.85D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            int rem = getLifetime();
            if (rem > 0) {
                rem--;
                setLifetime(rem);
                if (rem == 0) {
                    dropDormantCoreOnce(serverLevel);
                    discard();
                    return;
                }
            }
            Player owner = getOwnerPlayer();
            if (owner != null) {
                LivingEntity threat = owner.getLastHurtByMob();
                if (threat != null && threat.isAlive() && threat != this && getTarget() == null) {
                    setTarget(threat);
                }
            }
        }
        if (level().isClientSide() && tickCount % 12 == 0) {
            level().addParticle(FantasyEarthParticles.RUNE_SPARK.get(),
                    getRandomX(0.65D), getY(0.55D), getRandomZ(0.65D), 0.0D, 0.015D, 0.0D);
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide() && level() instanceof ServerLevel serverLevel) {
            dropDormantCoreOnce(serverLevel);
        }
        super.die(source);
    }

    private void dropDormantCoreOnce(ServerLevel serverLevel) {
        if (!droppedCore) {
            droppedCore = true;
            spawnAtLocation(serverLevel, new ItemStack(FantasyEarth.DORMANT_RITUAL_CORE.get()));
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.AMETHYST_BLOCK_CHIME;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.IRON_GOLEM_DEATH;
    }

    private static final class FollowSummonerGoal extends Goal {
        private final ContractGolemEntity golem;
        private final double speed;
        private final float startDist;
        private final float stopDist;
        private @Nullable Player owner;

        FollowSummonerGoal(ContractGolemEntity golem, double speed, float startDist, float stopDist) {
            this.golem = golem;
            this.speed = speed;
            this.startDist = startDist;
            this.stopDist = stopDist;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            Player p = golem.getOwnerPlayer();
            if (p == null || p.isSpectator() || golem.distanceToSqr(p) < (double) (startDist * startDist)) {
                return false;
            }
            this.owner = p;
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return owner != null && !golem.getNavigation().isDone()
                    && golem.distanceToSqr(owner) > (double) (stopDist * stopDist);
        }

        @Override
        public void tick() {
            if (owner != null) {
                golem.getLookControl().setLookAt(owner, 10.0F, (float) golem.getMaxHeadXRot());
                golem.getNavigation().moveTo(owner, speed);
            }
        }
    }
}
