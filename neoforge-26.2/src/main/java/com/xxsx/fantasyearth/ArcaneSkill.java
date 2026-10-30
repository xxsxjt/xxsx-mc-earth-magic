package com.xxsx.fantasyearth;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class ArcaneSkill {
    private ArcaneSkill() {
    }

    public static boolean activate(ServerLevel level, ServerPlayer player) {
        ArcaneFocus focus = ArcanaPower.getArcaneFocus(player);
        SpellExecutor.Plan plan = SpellExecutor.plan(player, level, focus);
        if (!plan.accepted()) {
            String key = switch (plan.errorCode()) {
                case "requires_research" -> "message.fantasy_earth.skill.requires_research";
                case "cooldown" -> "message.fantasy_earth.skill.cooldown";
                case "mana_low" -> "message.fantasy_earth.skill.no_mana";
                default -> "message.fantasy_earth.skill.graph_invalid";
            };
            long remaining = ArcanaPower.getSkillCooldownTicks(player, level);
            if ("cooldown".equals(plan.errorCode())) {
                player.sendSystemMessage(Component.translatable(key, (remaining + 19L) / 20L)
                        .withStyle(ChatFormatting.YELLOW));
            } else if ("mana_low".equals(plan.errorCode())) {
                player.sendSystemMessage(Component.translatable(key, ArcanaPower.format(plan.manaCost()))
                        .withStyle(ChatFormatting.RED));
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
        }

        player.swing(InteractionHand.MAIN_HAND, true);
        ArcanaPower.startSkillCooldown(player, level);
        ArcaneNetwork.broadcastVisual(player, ArcaneVisualAction.forFocus(focus));
        level.sendParticles(switch (focus) {
                    case ATTUNEMENT -> ParticleTypes.REVERSE_PORTAL;
                    case BODY_WARD -> ParticleTypes.WITCH;
                    case BREATH_WARD -> ParticleTypes.BUBBLE_POP;
                }, player.getX(), player.getY() + 1.0D, player.getZ(),
                24, 0.75D, 0.9D, 0.75D, 0.03D);
        level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                SoundSource.PLAYERS, 0.85F, 0.92F + focus.id() * 0.12F);
        player.sendSystemMessage(Component.translatable(
                "message.fantasy_earth.skill.activated." + focus.path(),
                ArcanaPower.format(cost)).withStyle(ChatFormatting.LIGHT_PURPLE));
        MagicJourney.complete(player, MagicJourney.Milestone.ACTIVE_SPELL);
        return true;
    }
}
