package com.xxsx.fantasyearth;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;

import java.util.List;

public class MagicFieldBlock extends Block {
    public MagicFieldBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        return describe(level, pos, player, InteractionHand.MAIN_HAND);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        return describe(level, pos, player, hand);
    }

    @Override
    protected List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (!drops.isEmpty()) {
            return drops;
        }
        return List.of(new ItemStack(FantasyEarth.AETHER_SHARD.get(), 2));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(4) == 0) {
            double x = pos.getX() + 0.2D + random.nextDouble() * 0.6D;
            double y = pos.getY() + 0.2D + random.nextDouble() * 0.8D;
            double z = pos.getZ() + 0.2D + random.nextDouble() * 0.6D;
            level.addParticle(FantasyEarthParticles.AETHER_MOTE.get(), x, y, z, 0.0D, 0.015D, 0.0D);
            if (random.nextInt(3) == 0) {
                level.addParticle(FantasyEarthParticles.CRYSTAL_SHARD.get(), x, y, z, 0.0D, 0.02D, 0.0D);
            }
        }
    }

    private InteractionResult describe(Level level, BlockPos pos, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        var ritualCenter = MagicStructures.findFormalRitualCenter(level, pos);
        if (ritualCenter.isPresent()) {
            return MagicMachineBlock.openMachineAt(level, ritualCenter.get(), player);
        }
        player.swing(hand, true);
        if (level instanceof ServerLevel serverLevel) {
            ArcaneVfx.moteBurst(serverLevel, pos.getX() + 0.5D, pos.getY() + 0.65D, pos.getZ() + 0.5D, 8, 0.35D);
            ArcaneVfx.shardBurst(serverLevel, pos.getX() + 0.5D, pos.getY() + 0.65D, pos.getZ() + 0.5D, 5, 0.3D);
            serverLevel.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME, SoundSource.BLOCKS, 0.75F, 1.15F);
        }
        AetherChunkField.Reading reading = AetherChunkField.read(level, pos);
        player.sendSystemMessage(Component.translatable("message.fantasy_earth.block.field",
                AetherChunkField.gradeName(reading.value()),
                reading.value(),
                reading.mainSource(),
                ArcanaPower.format(reading.disturbance())).withStyle(ChatFormatting.LIGHT_PURPLE));
        return InteractionResult.SUCCESS_SERVER;
    }
}
