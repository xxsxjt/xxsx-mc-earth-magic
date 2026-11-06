package com.xxsx.fantasyearth;

import com.xxsx.earthlore.api.LoreApi;
import com.xxsx.earthlore.api.StudyResult;
import com.xxsx.earthlore.data.LoreText;
import com.xxsx.earthlore.registry.LoreContent;
import com.xxsx.earthlore.study.LiteratureItem;
import com.xxsx.earthlore.study.StudyService;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
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

import java.util.List;
import java.util.function.Consumer;

public class ArcaneAdaptationNotesItem extends Item {
    public enum Type {
        BODY_WARD("arcane_body_ward_notes", FantasyLoreIntegration.K_MAG_07_BODY_WARD),
        BREATH_WARD("arcane_breath_ward_notes", FantasyLoreIntegration.K_MAG_08_BREATH_WARD),
        EVOCATION("evocation_notes", FantasyLoreIntegration.K_MAG_16_EVOCATION),
        ILLUSION("illusion_notes", FantasyLoreIntegration.K_MAG_17_ILLUSION),
        TRANSLOCATION("translocation_notes", FantasyLoreIntegration.K_MAG_18_TRANSLOCATION);

        private final String id;
        private final Identifier node;

        Type(String id, Identifier node) {
            this.id = id;
            this.node = node;
        }

        public Identifier node() {
            return node;
        }

        public LoreText defaultLoreText() {
            return FantasyLoreIntegration.notesLoreText(List.of(node));
        }
    }

    private final Type type;

    public ArcaneAdaptationNotesItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.has(LoreContent.LORE_TEXT.get())) {
            stack.set(LoreContent.LORE_TEXT.get(), type.defaultLoreText());
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
        lines.accept(Component.translatable("tooltip.fantasy_earth." + type.id).withStyle(ChatFormatting.LIGHT_PURPLE));
        lines.accept(Component.translatable("tooltip.fantasy_earth." + type.id + ".use").withStyle(ChatFormatting.GRAY));
    }

    static void sendAdaptationStatus(Player player) {
        player.sendSystemMessage(Component.translatable(
                "message.fantasy_earth.arcane_adaptation.body_status",
                Math.round(ArcanaPower.getExhaustionReduction(player) * 100.0D),
                Math.round(ArcanaPower.getCombatDamageReduction(player) * 100.0D),
                Math.round((ArcanaPower.getBreathMultiplier(player) - 1.0D) * 100.0D),
                ArcanaPower.format(ArcanaPower.getMaxMana(player)))
                .withStyle(ChatFormatting.DARK_PURPLE));
    }
}
