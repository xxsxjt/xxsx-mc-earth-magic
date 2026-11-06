package com.xxsx.fantasyearth;

import com.xxsx.earthlore.api.LoreApi;
import com.xxsx.earthlore.api.StudyResult;
import com.xxsx.earthlore.registry.LoreContent;
import com.xxsx.earthlore.study.LiteratureItem;
import com.xxsx.earthlore.study.StudyService;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

/** F-049 Generic Arcane Research Notes produced at the Research Table for magic nodes without a dedicated note item. */
public class ArcaneResearchNotesItem extends Item {
    public ArcaneResearchNotesItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.has(LoreContent.LORE_TEXT.get())) {
            if (player instanceof ServerPlayer serverPlayer) {
                serverPlayer.sendOverlayMessage(Component.translatable("lore.earth_lore.fail.lore_text_empty")
                        .withStyle(ChatFormatting.RED));
            }
            return InteractionResult.FAIL;
        }
        if (level.isClientSide()) {
            if (!LiteratureItem.clientCanStudy.test(player, stack)) {
                return InteractionResult.FAIL;
            }
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        if (player instanceof ServerPlayer serverPlayer) {
            StudyResult result = LoreApi.beginStudy(serverPlayer, stack);
            if (!result.started()) {
                serverPlayer.sendOverlayMessage(StudyService.failMessage(result));
                return InteractionResult.FAIL;
            }
            player.startUsingItem(hand);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS_SERVER;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return LiteratureItem.USE_DURATION;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity entity, int remainingTime) {
        if (entity instanceof ServerPlayer serverPlayer) {
            StudyService.cancel(serverPlayer);
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> lines, TooltipFlag flag) {
        lines.accept(Component.translatable("tooltip.fantasy_earth.arcane_research_notes").withStyle(ChatFormatting.LIGHT_PURPLE));
        lines.accept(Component.translatable("tooltip.fantasy_earth.arcane_research_notes.use").withStyle(ChatFormatting.GRAY));
    }
}
