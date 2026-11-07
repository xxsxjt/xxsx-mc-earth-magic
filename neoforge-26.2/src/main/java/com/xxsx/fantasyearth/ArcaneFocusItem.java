package com.xxsx.fantasyearth;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/** F-047 Arcane Focus: holds 3 rune slots, 3 gem slots, optional jade housing; casts active spell on right-click. */
public class ArcaneFocusItem extends MagicMaterialItem {
    public ArcaneFocusItem(Properties properties, String hintKey) {
        super(properties, hintKey);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack focusStack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS_SERVER;
        }
        ArcaneFocusComponent comp = ArcaneFocusComponent.read(focusStack);
        ItemStack offhand = player.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

        // Sneak + right-click with offhand rune/gem/jade housing installs it into the focus
        if (player.isShiftKeyDown() && !offhand.isEmpty()) {
            if (offhand.getItem() instanceof RuneStoneItem runeItem) {
                RuneItemComponent rc = RuneItemComponent.read(offhand, runeItem.runeKind());
                String spec = runeItem.runeKind() + ":" + rc.tier();
                List<String> slots = comp.runes();
                int emptyIdx = -1;
                for (int i = 0; i < slots.size(); i++) {
                    if (slots.get(i).isEmpty()) {
                        emptyIdx = i;
                        break;
                    }
                }
                int targetIdx = emptyIdx >= 0 ? emptyIdx : 0;
                focusStack.set(FantasyEarth.ARCANE_FOCUS_COMPONENT.get(), comp.withRuneSlot(targetIdx, spec));
                if (!player.getAbilities().instabuild) {
                    offhand.shrink(1);
                }
                serverPlayer.swing(hand, true);
                ArcaneNetwork.broadcastVisual(serverPlayer, ArcaneVisualAction.ATTUNEMENT);
                ArcaneVfx.sparkBurst(serverLevel, serverPlayer.getX(), serverPlayer.getY() + 1.0D, serverPlayer.getZ(), 18, 0.45D);
                ArcaneVfx.runeRingAround(serverLevel, serverPlayer.position(), 0.85D, 8, 0.9D);
                serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE,
                        SoundSource.PLAYERS, 0.85F, 1.25F);
                serverPlayer.sendSystemMessage(Component.translatable("message.fantasy_earth.focus.rune_slotted", spec)
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
                return InteractionResult.SUCCESS_SERVER;
            }
            if (offhand.is(FantasyEarth.JADE_FOCUS_HOUSING.get()) && !comp.jadeHousing()) {
                focusStack.set(FantasyEarth.ARCANE_FOCUS_COMPONENT.get(), comp.withJadeHousing(true));
                if (!player.getAbilities().instabuild) {
                    offhand.shrink(1);
                }
                serverPlayer.swing(hand, true);
                ArcaneNetwork.broadcastVisual(serverPlayer, ArcaneVisualAction.ATTUNEMENT);
                ArcaneVfx.shardBurst(serverLevel, serverPlayer.getX(), serverPlayer.getY() + 1.0D, serverPlayer.getZ(), 16, 0.45D);
                ArcaneVfx.moteBurst(serverLevel, serverPlayer.getX(), serverPlayer.getY() + 1.0D, serverPlayer.getZ(), 14, 0.4D);
                serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                        SoundSource.PLAYERS, 0.9F, 1.15F);
                serverPlayer.sendSystemMessage(Component.translatable("message.fantasy_earth.focus.jade_installed")
                        .withStyle(ChatFormatting.GREEN));
                return InteractionResult.SUCCESS_SERVER;
            }
            String gemSpecies = resolveGemSpecies(offhand);
            if (gemSpecies != null) {
                if (comp.hasDuplicateGemSpecies(gemSpecies)) {
                    serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.AMETHYST_BLOCK_HIT,
                            SoundSource.PLAYERS, 0.7F, 0.7F);
                    serverPlayer.sendSystemMessage(Component.translatable("message.fantasy_earth.focus.duplicate_gem")
                            .withStyle(ChatFormatting.RED));
                    return InteractionResult.FAIL;
                }
                List<String> gems = comp.gems();
                int emptyIdx = -1;
                for (int i = 0; i < gems.size(); i++) {
                    if (gems.get(i).isEmpty()) {
                        emptyIdx = i;
                        break;
                    }
                }
                int targetIdx = emptyIdx >= 0 ? emptyIdx : 0;
                focusStack.set(FantasyEarth.ARCANE_FOCUS_COMPONENT.get(), comp.withGemSlot(targetIdx, gemSpecies + ":common"));
                if (!player.getAbilities().instabuild) {
                    offhand.shrink(1);
                }
                serverPlayer.swing(hand, true);
                ArcaneNetwork.broadcastVisual(serverPlayer, ArcaneVisualAction.ATTUNEMENT);
                ArcaneVfx.shardBurst(serverLevel, serverPlayer.getX(), serverPlayer.getY() + 1.0D, serverPlayer.getZ(), 18, 0.45D);
                ArcaneVfx.sparkBurst(serverLevel, serverPlayer.getX(), serverPlayer.getY() + 1.0D, serverPlayer.getZ(), 12, 0.4D);
                serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME,
                        SoundSource.PLAYERS, 0.95F, 1.3F);
                serverPlayer.sendSystemMessage(Component.translatable("message.fantasy_earth.focus.gem_slotted", gemSpecies)
                        .withStyle(ChatFormatting.AQUA));
                return InteractionResult.SUCCESS_SERVER;
            }
        }

        if (comp.durability() <= 0) {
            serverLevel.playSound(null, serverPlayer.blockPosition(), SoundEvents.ITEM_BREAK.value(),
                    SoundSource.PLAYERS, 0.7F, 0.9F);
            serverPlayer.sendSystemMessage(Component.translatable("message.fantasy_earth.focus.broken")
                    .withStyle(ChatFormatting.RED));
            return InteractionResult.FAIL;
        }
        if (ArcaneSkill.activate(serverLevel, serverPlayer, hand)) {
            focusStack.set(FantasyEarth.ARCANE_FOCUS_COMPONENT.get(), comp.withDurability(comp.durability() - 1));
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    private static String resolveGemSpecies(ItemStack stack) {
        if (stack.is(Items.EMERALD)) {
            return "emerald";
        }
        if (stack.is(Items.AMETHYST_SHARD)) {
            return "amethyst";
        }
        if (stack.is(Items.DIAMOND)) {
            return "diamond";
        }
        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().toLowerCase(Locale.ROOT);
        for (String species : List.of("ruby", "sapphire", "emerald", "amethyst", "topaz", "diamond")) {
            if (path.contains(species)) {
                return species;
            }
        }
        return null;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, lines, flag);
        ArcaneFocusComponent comp = ArcaneFocusComponent.read(stack);
        lines.accept(Component.translatable("tooltip.fantasy_earth.arcane_focus.durability",
                comp.durability(), comp.maxDurability()).withStyle(ChatFormatting.AQUA));
        lines.accept(Component.translatable("tooltip.fantasy_earth.arcane_focus.runes",
                String.join(", ", comp.runes().stream().filter(s -> !s.isEmpty()).toList()))
                .withStyle(ChatFormatting.LIGHT_PURPLE));
    }
}
