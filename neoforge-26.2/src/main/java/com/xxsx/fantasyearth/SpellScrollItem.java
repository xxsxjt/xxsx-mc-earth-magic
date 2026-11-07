package com.xxsx.fantasyearth;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** F-048 Spell Scroll: stores a spell graph_id and casts it without requiring an arcane focus or rune tier. */
public class SpellScrollItem extends MagicMaterialItem {
    public SpellScrollItem(Properties properties, String hintKey) {
        super(properties, hintKey);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
            return InteractionResult.SUCCESS_SERVER;
        }
        SpellScrollComponent comp = SpellScrollComponent.read(stack);
        if (player.isShiftKeyDown()) {
            ArcaneFocus current = ArcanaPower.getArcaneFocus(serverPlayer);
            String nextGraph = SpellCatalog.forFocus(current).id();
            stack.set(FantasyEarth.SPELL_SCROLL_COMPONENT.get(), comp.withGraph(nextGraph));
            serverPlayer.sendSystemMessage(Component.translatable("message.fantasy_earth.spell_scroll.inscribed", nextGraph)
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
            return InteractionResult.SUCCESS_SERVER;
        }
        if (comp.graphId().isBlank()) {
            ArcaneFocus defaultFocus = ArcanaPower.getArcaneFocus(serverPlayer);
            String autoGraph = SpellCatalog.forFocus(defaultFocus).id();
            comp = comp.withGraph(autoGraph);
            stack.set(FantasyEarth.SPELL_SCROLL_COMPONENT.get(), comp);
        }
        ArcaneFocus focus = SpellCatalog.focusForGraphId(comp.graphId());
        if (ArcaneSkill.activateWithScroll(serverLevel, serverPlayer, focus)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            return InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, lines, flag);
        SpellScrollComponent comp = SpellScrollComponent.read(stack);
        if (!comp.graphId().isBlank()) {
            lines.accept(Component.translatable("tooltip.fantasy_earth.spell_scroll.graph", comp.graphId())
                    .withStyle(ChatFormatting.AQUA));
        }
    }
}
