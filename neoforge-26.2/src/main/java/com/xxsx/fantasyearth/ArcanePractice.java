package com.xxsx.fantasyearth;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class ArcanePractice {
    public enum Support {
        FREE(0, 0.72D, 0.72D, "free"),
        FOCUS_MAT(16, 1.0D, 1.0D, "focus_mat");

        private final int fieldBonus;
        private final double efficiency;
        private final double recoveryScale;
        private final String translationSuffix;

        Support(int fieldBonus, double efficiency, double recoveryScale, String translationSuffix) {
            this.fieldBonus = fieldBonus;
            this.efficiency = efficiency;
            this.recoveryScale = recoveryScale;
            this.translationSuffix = translationSuffix;
        }

        int fieldBonus() {
            return fieldBonus;
        }

        double efficiency() {
            return efficiency;
        }

        double recoveryScale() {
            return recoveryScale;
        }

        int effectDuration(int baseTicks) {
            return Math.max(20, (int) Math.round(baseTicks * recoveryScale));
        }
    }

    private ArcanePractice() {
    }

    public static boolean perform(ServerLevel level, BlockPos pos, ServerPlayer player,
                                  Support support, boolean quietOutput) {
        if (ArcanaPower.getMagicResearchLevel(player) <= 0) {
            if (!quietOutput) {
                player.sendSystemMessage(Component.translatable(
                        "message.fantasy_earth.practice.requires_initiation")
                        .withStyle(ChatFormatting.RED));
            }
            return false;
        }
        long cooldown = ArcanaPower.getMagicFocusCooldownTicks(player, level);
        if (cooldown > 0L) {
            if (!quietOutput && cooldown > 20L) {
                player.sendSystemMessage(Component.translatable("message.fantasy_earth.arcane_notes.cooldown",
                        (cooldown + 19L) / 20L).withStyle(ChatFormatting.YELLOW));
            }
            return false;
        }

        ArcaneFocus focus = ArcanaPower.getArcaneFocus(player);
        AetherChunkField.Reading reading = AetherChunkField.read(level, pos);
        int focusedAether = Math.min(100, reading.value() + support.fieldBonus());
        double focusScale = switch (focus) {
            case ATTUNEMENT -> 1.0D;
            case BODY_WARD -> 0.72D;
            case BREATH_WARD -> 0.82D;
        };
        int usableAether = Math.max(1,
                (int) Math.round(focusedAether * focusScale * support.efficiency()));
        double restored = ArcanaPower.focusAmbientMagic(player, usableAether);
        int gainedXp = Math.max(2, (int) Math.round((3.0D + focusedAether * 0.12D) * support.efficiency()));
        ArcanaPower.ProgressResult progress = ArcanaPower.addArcaneExperience(player, focus, gainedXp);
        AetherChunkField.disturb(level, pos, Math.max(1.0D, restored));
        ArcanaPower.startMagicFocusCooldown(player, level);
        ArcaneNetwork.broadcastVisual(player, ArcaneVisualAction.ATTUNEMENT);

        double recoveryScale = support.recoveryScale();
        EarthHumanCompat.RecoveryReport report;
        switch (focus) {
            case BODY_WARD -> {
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE,
                        support.effectDuration(140), 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION,
                        support.effectDuration(160), 0, true, false, true));
                report = EarthHumanCompat.recoverWard(player,
                        (5.2D + restored * 0.075D) * recoveryScale,
                        (0.48D + restored * 0.016D) * recoveryScale);
            }
            case BREATH_WARD -> {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING,
                        support.effectDuration(150), 0, true, false, true));
                int airCap = player.getMaxAirSupply() + (int) Math.round(ArcanaPower.getBreathCapacityBonus(player));
                player.setAirSupply(Math.min(airCap,
                        player.getAirSupply() + (int) Math.round(120.0D * recoveryScale)));
                report = EarthHumanCompat.recoverBreath(player,
                        (3.8D + restored * 0.055D) * recoveryScale,
                        (0.28D + restored * 0.010D) * recoveryScale);
            }
            default -> {
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                        support.effectDuration(80), 0, true, false, true));
                report = EarthHumanCompat.recoverWard(player,
                        (3.0D + restored * 0.05D) * recoveryScale,
                        (0.22D + restored * 0.010D) * recoveryScale);
            }
        }

        if (!quietOutput) {
            player.sendSystemMessage(Component.translatable(
                    "message.fantasy_earth.practice.used." + support.translationSuffix)
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
            player.sendSystemMessage(Component.translatable("message.fantasy_earth.practice.result",
                    AetherChunkField.gradeName(reading.value()),
                    reading.value(),
                    ArcanaPower.format(restored),
                    progress.gainedXp(),
                    progress.level(),
                    progress.atCap() ? "MAX" : progress.xp() + "/" + progress.xpNeeded(),
                    ArcanaPower.format(report.fatigueReduced()),
                    ArcanaPower.format(report.bodyHealed()),
                    Math.round(support.efficiency() * 100.0D)).withStyle(ChatFormatting.AQUA));
        }
        if (progress.leveledUp()) {
            player.sendSystemMessage(Component.translatable(
                    "message.fantasy_earth.practice.level_up",
                    Component.translatable(focus.titleKey()), progress.level())
                    .withStyle(ChatFormatting.GOLD));
        }
        MagicJourney.complete(player, MagicJourney.Milestone.ATTUNEMENT);
        emitPractice(level, pos, focus, support);
        return true;
    }

    public static void emitFocusChange(ServerLevel level, BlockPos pos, ArcaneFocus focus, boolean supported) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + (supported ? 0.55D : 1.05D);
        double z = pos.getZ() + 0.5D;
        int count = supported ? 16 : 9;
        double spread = supported ? 0.44D : 0.30D;
        switch (focus) {
            case ATTUNEMENT -> ArcaneVfx.moteBurst(level, x, y, z, count, spread);
            case BODY_WARD -> ArcaneVfx.sparkBurst(level, x, y, z, count, spread);
            case BREATH_WARD -> {
                ArcaneVfx.moteBurst(level, x, y, z, count, spread);
                ArcaneVfx.shardBurst(level, x, y, z, Math.max(3, count / 3), spread * 0.7D);
            }
        }
        level.sendParticles(ParticleTypes.ENCHANT,
                x, y, z, supported ? 6 : 3, spread, spread * 0.5D, spread, 0.01D);
    }

    private static void emitPractice(ServerLevel level, BlockPos pos, ArcaneFocus focus, Support support) {
        boolean supported = support == Support.FOCUS_MAT;
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + (supported ? 0.55D : 1.0D);
        double z = pos.getZ() + 0.5D;
        int count = supported ? 14 : 8;
        double spread = supported ? 0.42D : 0.52D;
        switch (focus) {
            case ATTUNEMENT -> {
                ArcaneVfx.moteBurst(level, x, y, z, count, spread);
                ArcaneVfx.runeRingAround(level, new net.minecraft.world.phys.Vec3(x, pos.getY() + 0.1D, z),
                        0.72D, supported ? 8 : 5, 0.0D);
            }
            case BODY_WARD -> {
                ArcaneVfx.sparkBurst(level, x, y, z, count, spread);
                ArcaneVfx.shardBurst(level, x, y, z, Math.max(3, count / 4), spread * 0.6D);
            }
            case BREATH_WARD -> {
                ArcaneVfx.moteBurst(level, x, y, z, count, spread);
                ArcaneVfx.shardBurst(level, x, y, z, Math.max(3, count / 4), spread * 0.6D);
            }
        }
        level.sendParticles(ParticleTypes.ENCHANT, x, y, z, supported ? 5 : 2,
                spread, spread * 0.5D, spread, 0.008D);
        level.playSound(null, pos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS,
                supported ? 0.70F : 0.46F, supported ? 1.10F : 0.92F);
    }
}
