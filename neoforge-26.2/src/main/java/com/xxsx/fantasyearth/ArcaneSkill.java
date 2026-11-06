package com.xxsx.fantasyearth;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class ArcaneSkill {
    private ArcaneSkill() {
    }

    public static boolean activate(ServerLevel level, ServerPlayer player) {
        return execute(level, player, ArcanaPower.getArcaneFocus(player), false);
    }

    public static boolean activateWithScroll(ServerLevel level, ServerPlayer player, ArcaneFocus focus) {
        return execute(level, player, focus, true);
    }

    private static boolean execute(ServerLevel level, ServerPlayer player, ArcaneFocus focus, boolean fromScroll) {
        SpellExecutor.Plan plan = SpellExecutor.plan(player, level, focus, fromScroll);
        if (!plan.accepted()) {
            String key = switch (plan.errorCode()) {
                case "lore_locked", "requires_research" -> "message.fantasy_earth.skill.requires_research";
                case "rune_tier_low" -> "message.fantasy_earth.skill.rune_tier_low";
                case "missing_focus" -> "message.fantasy_earth.skill.missing_focus";
                case "cooldown" -> "message.fantasy_earth.skill.cooldown";
                case "mana_low" -> "message.fantasy_earth.skill.no_mana";
                case "field_low" -> "message.fantasy_earth.skill.field_low";
                default -> "message.fantasy_earth.skill.graph_invalid";
            };
            long remaining = ArcanaPower.getSkillCooldownTicks(player, level);
            if ("cooldown".equals(plan.errorCode())) {
                player.sendSystemMessage(Component.translatable(key, (remaining + 19L) / 20L)
                        .withStyle(ChatFormatting.YELLOW));
            } else if ("mana_low".equals(plan.errorCode())) {
                player.sendSystemMessage(Component.translatable(key, ArcanaPower.format(plan.manaCost()))
                        .withStyle(ChatFormatting.RED));
            } else if ("field_low".equals(plan.errorCode())) {
                player.sendSystemMessage(Component.translatable(key, plan.message())
                        .withStyle(ChatFormatting.AQUA));
            } else {
                player.sendSystemMessage(Component.translatable(key, plan.message())
                        .withStyle(ChatFormatting.RED));
            }
            return false;
        }
        double cost = plan.manaCost();
        if (!ArcanaPower.trySpendMana(player, cost)) {
            player.sendSystemMessage(Component.translatable(
                    "message.fantasy_earth.skill.no_mana", ArcanaPower.format(cost))
                    .withStyle(ChatFormatting.RED));
            return false;
        }

        int levelValue = ArcanaPower.getFocusLevel(player, focus);
        switch (focus) {
            case ATTUNEMENT -> {
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 100 + levelValue * 10,
                        levelValue >= 7 ? 1 : 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 180 + levelValue * 20,
                        0, true, false, true));
            }
            case BODY_WARD -> {
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 140 + levelValue * 12,
                        levelValue >= 8 ? 1 : 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 160 + levelValue * 12,
                        levelValue >= 6 ? 1 : 0, true, false, true));
            }
            case BREATH_WARD -> {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200 + levelValue * 22,
                        0, true, false, true));
                player.setAirSupply(player.getMaxAirSupply()
                        + (int) Math.round(ArcanaPower.getBreathCapacityBonus(player)));
            }
            case EVOCATION -> {
                float damage = 4.0F + levelValue;
                Vec3 look = player.getLookAngle();
                AABB hitBox = player.getBoundingBox().expandTowards(look.scale(10.0D)).inflate(1.5D);
                List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, hitBox,
                        e -> e != player && e.isAlive() && !e.isAlliedTo(player));
                if (!targets.isEmpty()) {
                    targets.getFirst().hurtServer(level, player.damageSources().magic(), damage);
                }
            }
            case ILLUSION -> {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 120, 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 120, 0, true, false, true));
            }
            case TRANSLOCATION -> {
                Vec3 look = player.getLookAngle().normalize();
                Vec3 dest = player.position();
                for (int step = 1; step <= 8; step++) {
                    Vec3 candidate = player.position().add(look.scale(step));
                    BlockPos bp = BlockPos.containing(candidate);
                    if (level.getBlockState(bp).getCollisionShape(level, bp).isEmpty()
                            && level.getBlockState(bp.above()).getCollisionShape(level, bp.above()).isEmpty()) {
                        dest = new Vec3(candidate.x, bp.getY(), candidate.z);
                    } else {
                        break;
                    }
                }
                player.teleportTo(dest.x, dest.y, dest.z);
            }
        }

        if (plan.aeuCost() > 0.0D) {
            AetherChunkField.disturbAeu(level, player.blockPosition(), plan.aeuCost());
        }
        player.swing(InteractionHand.MAIN_HAND, true);
        int cdTicks = switch (focus) {
            case EVOCATION -> 200;
            case ILLUSION -> 300;
            case TRANSLOCATION -> 320;
            default -> 240;
        };
        ArcanaPower.startSkillCooldown(player, level, cdTicks);
        ArcaneNetwork.broadcastVisual(player, ArcaneVisualAction.forFocus(focus));
        ArcaneVfx.focusCast(level, player.position(), focus);
        level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS, 0.85F, 0.92F + focus.id() * 0.12F);
        player.sendSystemMessage(Component.translatable(
                "message.fantasy_earth.skill.activated." + focus.path(),
                ArcanaPower.format(cost)).withStyle(ChatFormatting.LIGHT_PURPLE));
        MagicJourney.complete(player, MagicJourney.Milestone.ACTIVE_SPELL);
        return true;
    }
}
