package com.xxsx.fantasyearth;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class ArcaneSeatEntity extends Entity {
    private BlockPos sourcePos = BlockPos.ZERO;

    public ArcaneSeatEntity(EntityType<? extends ArcaneSeatEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
        this.blocksBuilding = false;
    }

    public void bindTo(BlockPos pos) {
        this.sourcePos = pos.immutable();
        this.setPos(pos.getX() + 0.5D, pos.getY() + 0.04D, pos.getZ() + 0.5D);
    }

    public boolean isBoundTo(BlockPos pos) {
        return this.sourcePos.equals(pos);
    }

    public BlockPos sourcePos() {
        return this.sourcePos;
    }

    public void emitFocusChange(ArcaneFocus focus) {
        if (!(this.level() instanceof ServerLevel level)) {
            return;
        }
        ArcanePractice.emitFocusChange(level, sourcePos, focus, true);
    }

    @Override
    public void tick() {
        super.tick();
        this.noPhysics = true;
        this.setDeltaMovement(Vec3.ZERO);
        if (this.level().isClientSide()) {
            return;
        }

        if (!(this.level() instanceof ServerLevel serverLevel)) {
            this.discard();
            return;
        }

        BlockState matState = serverLevel.getBlockState(this.sourcePos);
        if (!(matState.getBlock() instanceof ArcaneFocusMatBlock)) {
            this.discard();
            return;
        }

        if (!(this.getFirstPassenger() instanceof ServerPlayer player)) {
            this.discard();
            return;
        }

        Direction facing = matState.getValue(ArcaneFocusMatBlock.FACING);
        player.setYRot(facing.toYRot());
        player.setYHeadRot(facing.toYRot());
        player.setYBodyRot(facing.toYRot());

        double x = this.sourcePos.getX() + 0.5D;
        double y = this.sourcePos.getY() + 0.24D;
        double z = this.sourcePos.getZ() + 0.5D;
        if (this.tickCount % 8 == 0) {
            ArcaneVfx.moteBurst(serverLevel, x, y + 0.08D, z, 3, 0.30D);
        }
        if (this.tickCount % 32 == 0) {
            ArcaneVfx.sparkBurst(serverLevel, x, y + 0.32D, z, 2, 0.18D);
        }
        if (this.tickCount % 20 == 0
                && ArcaneFocusMatBlock.performSeatedFocus(serverLevel, this.sourcePos, player, true)) {
            ArcaneVfx.runeRing(serverLevel, new net.minecraft.world.phys.Vec3(x, y + 0.05D, z), 0.85D, 8);
            ArcaneVfx.moteBurst(serverLevel, x, y + 0.35D, z, 8, 0.40D);
            serverLevel.playSound(null, this.sourcePos, SoundEvents.ENCHANTMENT_TABLE_USE,
                    SoundSource.PLAYERS, 0.7F, 1.1F);
        }
        if (this.tickCount % 20 == 0) {
            ArcaneNetwork.sync(player, this.sourcePos, false);
        }
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity passenger) {
        return new Vec3(this.getX(), this.getY() + 0.13D, this.getZ());
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.sourcePos = new BlockPos(
                input.getIntOr("SourceX", 0),
                input.getIntOr("SourceY", 0),
                input.getIntOr("SourceZ", 0));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("SourceX", this.sourcePos.getX());
        output.putInt("SourceY", this.sourcePos.getY());
        output.putInt("SourceZ", this.sourcePos.getZ());
    }
}
