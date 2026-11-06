package com.xxsx.fantasyearth;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** F-024..F-027, F-029 Rune items (power, guard, flow, bind, jade) with tier 1..3 (or 1..4 for jade). */
public class RuneStoneItem extends MagicMaterialItem {
    private final String runeKind;

    public RuneStoneItem(Properties properties, String runeKind, String hintKey) {
        super(properties, hintKey);
        this.runeKind = runeKind;
    }

    public String runeKind() {
        return runeKind;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        RuneItemComponent comp = RuneItemComponent.read(stack, runeKind);
        int maxTier = "jade".equals(runeKind) ? 4 : 3;
        if (player.isShiftKeyDown() && comp.tier() < maxTier) {
            // F-RUNE-01 tier upgrade using 2 rune_ink + 1 aether_shard in inventory
            int inkSlot = findItem(player, FantasyEarth.RUNE_INK.get(), 2);
            int shardSlot = findItem(player, FantasyEarth.AETHER_SHARD.get(), 1);
            if (inkSlot >= 0 && shardSlot >= 0) {
                if (!player.getAbilities().instabuild) {
                    player.getInventory().getItem(inkSlot).shrink(2);
                    player.getInventory().getItem(shardSlot).shrink(1);
                }
                RuneItemComponent upgraded = comp.withTier(comp.tier() + 1);
                stack.set(FantasyEarth.RUNE_ITEM_COMPONENT.get(), upgraded);
                player.sendSystemMessage(Component.translatable("message.fantasy_earth.rune.upgraded", upgraded.tier())
                        .withStyle(ChatFormatting.LIGHT_PURPLE));
                return InteractionResult.SUCCESS_SERVER;
            }
        }
        return InteractionResult.PASS;
    }

    private static int findItem(Player player, net.minecraft.world.item.Item item, int minCount) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(item) && s.getCount() >= minCount) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, lines, flag);
        RuneItemComponent comp = RuneItemComponent.read(stack, runeKind);
        lines.accept(Component.translatable("tooltip.fantasy_earth.rune.tier", comp.tier())
                .withStyle(ChatFormatting.AQUA));
        if ("jade".equals(runeKind)) {
            lines.accept(Component.translatable("tooltip.fantasy_earth.rune.grade", comp.gemGrade())
                    .withStyle(ChatFormatting.GREEN));
        }
    }
}
