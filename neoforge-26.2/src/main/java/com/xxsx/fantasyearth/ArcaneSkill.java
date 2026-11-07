package com.xxsx.fantasyearth;

import com.xxsx.fantasyearth.entity.ContractableFamiliarEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
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
                castAetherMiningRay(level, player, levelValue);
            }
            case BODY_WARD -> {
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 140 + levelValue * 12,
                        levelValue >= 8 ? 1 : 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 160 + levelValue * 12,
                        levelValue >= 6 ? 1 : 0, true, false, true));
                EarthHumanCompat.treatFractureAndCleanseAir(player, 0.55D, 3.0D, 25.0D, 0.0D, 10.0D);
                castFrostRingNova(level, player, levelValue);
            }
            case BREATH_WARD -> {
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 200 + levelValue * 22,
                        0, true, false, true));
                player.setAirSupply(player.getMaxAirSupply()
                        + (int) Math.round(ArcanaPower.getBreathCapacityBonus(player)));
                EarthHumanCompat.treatFractureAndCleanseAir(player, 0.0D, 1.5D, 100.0D, 20.0D, 12.0D);
            }
            case EVOCATION -> {
                castArcaneMissiles(level, player, levelValue);
            }
            case ILLUSION -> {
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 120, 0, true, false, true));
                player.addEffect(new MobEffectInstance(MobEffects.SPEED, 140, 1, true, false, true));
                AABB blindBox = player.getBoundingBox().inflate(7.0D);
                for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, blindBox,
                        e -> e != player && e instanceof Enemy && !(e instanceof ContractableFamiliarEntity))) {
                    target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 100, 2, false, true, true));
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1, false, true, true));
                }
            }
            case TRANSLOCATION -> {
                Vec3 look = player.getLookAngle().normalize();
                Vec3 dest = player.position();
                for (int step = 1; step <= 10; step++) {
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
                player.resetFallDistance();
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

    /**
     * 以太采掘射线：远程解构准星前方 8 格内的矿石或岩石方块，并自动吸入玩家背包。
     */
    public static void castAetherMiningRay(ServerLevel level, ServerPlayer player, int levelValue) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        for (int step = 1; step <= 8; step++) {
            Vec3 p = eye.add(look.scale(step));
            level.sendParticles(ParticleTypes.END_ROD, p.x, p.y, p.z, 2, 0.05D, 0.05D, 0.05D, 0.01D);
            BlockPos hitPos = BlockPos.containing(p);
            if (tryDeconstructBlock(level, player, hitPos)) {
                for (BlockPos adj : BlockPos.betweenClosed(hitPos.offset(-1, -1, -1), hitPos.offset(1, 1, 1))) {
                    if (!adj.equals(hitPos) && level.getRandom().nextBoolean()) {
                        tryDeconstructBlock(level, player, adj.immutable());
                    }
                }
                break;
            }
        }
    }

    /**
     * 霜环护盾：冻结周围水面为冰，熄灭自身火焰，并冰封定身周围敌对生物。
     */
    public static void castFrostRingNova(ServerLevel level, ServerPlayer player, int levelValue) {
        player.clearFire();
        BlockPos center = player.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-3, -1, -3), center.offset(3, 0, 3))) {
            BlockState state = level.getBlockState(pos);
            if (state.is(Blocks.WATER) && level.getBlockState(pos.above()).isAir()) {
                level.setBlockAndUpdate(pos.immutable(), Blocks.FROSTED_ICE.defaultBlockState());
            }
        }
        AABB box = player.getBoundingBox().inflate(5.5D);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, box,
                e -> e != player && e.isAlive() && !e.isAlliedTo(player) && !(e instanceof ContractableFamiliarEntity))) {
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 160, 3, false, true, true));
            target.setTicksFrozen(Math.min(target.getTicksRequiredToFreeze() + 80, target.getTicksFrozen() + 120));
            target.hurtServer(level, player.damageSources().magic(), 4.5F + levelValue * 0.4F);
            Vec3 knock = target.position().subtract(player.position());
            if (knock.lengthSqr() > 0.001D) {
                Vec3 push = knock.normalize().scale(0.65D);
                target.push(push.x, 0.25D, push.z);
            }
        }
        level.sendParticles(ParticleTypes.SNOWFLAKE, player.getX(), player.getY() + 0.8D, player.getZ(),
                24, 1.5D, 0.4D, 1.5D, 0.04D);
    }

    /**
     * 奥术飞弹：锁定前方视野内的最多 3 个敌人，发射必中追踪奥术能量束。
     */
    public static void castArcaneMissiles(ServerLevel level, ServerPlayer player, int levelValue) {
        float damage = 6.5F + levelValue * 0.9F;
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle().normalize();
        AABB hitBox = player.getBoundingBox().expandTowards(look.scale(14.0D)).inflate(3.5D);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, hitBox,
                        e -> e != player && e.isAlive() && !e.isAlliedTo(player) && !(e instanceof ContractableFamiliarEntity))
                .stream()
                .sorted(Comparator.comparingDouble(player::distanceToSqr))
                .limit(3)
                .toList();
        if (targets.isEmpty()) {
            castAetherMiningRay(level, player, levelValue);
            return;
        }
        for (LivingEntity target : targets) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            Vec3 delta = targetCenter.subtract(eye);
            int steps = Math.max(4, (int) (delta.length() * 3));
            for (int i = 1; i <= steps; i++) {
                Vec3 pt = eye.add(delta.scale((double) i / steps));
                level.sendParticles(ParticleTypes.WITCH, pt.x, pt.y, pt.z, 1, 0.02D, 0.02D, 0.02D, 0.0D);
            }
            target.hurtServer(level, player.damageSources().magic(), damage);
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 0, false, false, true));
        }
    }

    private static boolean tryDeconstructBlock(ServerLevel level, ServerPlayer player, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.getDestroySpeed(level, pos) < 0.0F || state.getDestroySpeed(level, pos) > 30.0F) {
            return false;
        }
        if (!state.is(BlockTags.MINEABLE_WITH_PICKAXE) && !state.is(BlockTags.BASE_STONE_OVERWORLD)) {
            return false;
        }
        if (level.getBlockEntity(pos) != null) {
            return false;
        }
        List<ItemStack> drops = Block.getDrops(state, level, pos, null, player, player.getMainHandItem());
        level.destroyBlock(pos, false, player);
        for (ItemStack drop : drops) {
            ItemStack copy = drop.copy();
            if (!player.getInventory().add(copy) && !copy.isEmpty()) {
                Block.popResource(level, pos, copy);
            }
        }
        return true;
    }
}
