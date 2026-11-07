package com.xxsx.fantasyearth.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.xxsx.fantasyearth.ArcanaPower;
import com.xxsx.fantasyearth.ArcaneFocus;
import com.xxsx.fantasyearth.ArcaneStatusPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ArcaneAttunementScreen extends Screen {
    private static final int BACKDROP = 0xAE090812;
    private static final int PANEL = 0xF0131120;
    private static final int PANEL_ALT = 0xFF211E35;
    private static final int EDGE = 0xFF8E7CFF;
    private static final int ACCENT = 0xFFAA9CFF;
    private static final int INK = 0xFFF8F4FF;
    private static final int MUTED = 0xFFC8C0E2;
    private static final int LOCKED = 0xFF927B8D;

    private final Map<ArcaneFocus, Button> focusButtons = new EnumMap<>(ArcaneFocus.class);
    private Button primaryActionButton;
    private Button spellButton;
    private Button hubNavButton;
    private Button loreTreeNavButton;
    private int refreshTicks;

    public ArcaneAttunementScreen() {
        super(Component.translatable("screen.fantasy_earth.attunement.title"));
    }

    @Override
    protected void init() {
        focusButtons.clear();
        LayoutMetrics layout = computeLayout(width, height);
        int left = layout.left();
        int top = layout.top();
        int w = layout.panelWidth();
        int h = layout.panelHeight();
        int buttonAreaWidth = layout.navButtonAreaWidth();
        int cellWidth = layout.focusCellWidth();

        ArcaneFocus[] values = ArcaneFocus.values();
        for (int idx = 0; idx < values.length; idx++) {
            ArcaneFocus focus = values[idx];
            int col = idx % 2;
            int row = idx / 2;
            int bx = left + 8 + col * (cellWidth + 4);
            int by = top + 38 + row * 20;
            Component label = fitted(Component.translatable(focus.titleKey()), Math.max(24, cellWidth - 6));
            Button button = addRenderableWidget(Button.builder(label,
                            ignored -> FantasyEarthClient.requestArcaneFocus(focus))
                    .bounds(bx, by, cellWidth, 18)
                    .build());
            button.setTooltip(Tooltip.create(Component.translatable(focus.descriptionKey())));
            focusButtons.put(focus, button);
        }
        spellButton = addRenderableWidget(Button.builder(
                        Component.translatable("screen.fantasy_earth.attunement.skill"),
                        ignored -> FantasyEarthClient.requestActivateArcaneSkill())
                .bounds(left + 8, layout.spellButtonY(), buttonAreaWidth, 18)
                .build());
        primaryActionButton = addRenderableWidget(Button.builder(
                        Component.translatable("screen.fantasy_earth.attunement.practice"),
                        ignored -> performPrimaryAction())
                .bounds(left + 8, layout.primaryActionButtonY(), buttonAreaWidth, 18)
                .build());
        hubNavButton = addRenderableWidget(Button.builder(
                        Component.translatable("screen.fantasy_earth.attunement.nav.hub"),
                        ignored -> FantasyEarthClient.openCultivationHub(this))
                .bounds(layout.hubButtonX(), top + 8, layout.hubButtonWidth(), 18)
                .build());
        hubNavButton.setTooltip(Tooltip.create(
                Component.translatable("screen.fantasy_earth.attunement.nav.hub.tooltip")));
        loreTreeNavButton = addRenderableWidget(Button.builder(
                        Component.translatable("screen.fantasy_earth.attunement.nav.lore_tree"),
                        ignored -> FantasyEarthClient.openLoreTree(this))
                .bounds(layout.loreTreeButtonX(), top + 8, layout.loreTreeButtonWidth(), 18)
                .build());
        loreTreeNavButton.setTooltip(Tooltip.create(
                Component.translatable("screen.fantasy_earth.attunement.nav.lore_tree.tooltip")));
        addRenderableWidget(Button.builder(Component.literal("X"), ignored -> onClose())
                .bounds(layout.closeButtonX(), top + 8, 18, 18)
                .build());
        updateButtons();
    }

    @Override
    public void tick() {
        updateButtons();
        if (minecraft == null || minecraft.player == null || minecraft.getConnection() == null) {
            onClose();
            return;
        }
        if (++refreshTicks >= 20) {
            refreshTicks = 0;
            FantasyEarthClient.requestAttunementRefresh();
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        ArcaneStatusPayload status = FantasyEarthClient.arcaneStatus();
        ArcaneFocus focus = ArcaneFocus.byId(status.focusId());
        int left = panelLeft();
        int top = panelTop();
        int w = panelWidth();
        int h = panelHeight();
        int navW = navigationWidth();
        int contentX = left + navW + 8;
        int contentW = left + w - 10 - contentX;

        g.fill(0, 0, width, height, BACKDROP);
        g.fill(left, top, left + w, top + h, PANEL);
        g.outline(left, top, w, h, EDGE);
        g.fill(left + 1, top + 1, left + w - 1, top + 34, PANEL_ALT);
        g.fill(left + navW, top + 35, left + navW + 1, top + h - 8, 0xFF4D456D);
        drawAmbientMotif(g, left, top, w);

        g.text(font, title, left + 12, top + 10, INK, false);
        Component support = Component.translatable(status.seated()
                ? "screen.fantasy_earth.attunement.support.focus_mat"
                : "screen.fantasy_earth.attunement.support.free");
        int supportMaxWidth = (left + w - 144) - (left + navW + 8);
        if (supportMaxWidth >= 48) {
            g.text(font, fitted(support, supportMaxWidth), left + navW + 8, top + 11,
                    status.seated() ? ACCENT : MUTED, false);
        }

        List<FormattedCharSequence> description = font.split(Component.translatable(focus.descriptionKey()),
                Math.max(80, contentW));
        if (!description.isEmpty()) {
            g.text(font, description.getFirst(), contentX, top + 39, MUTED, false);
        }

        int circuitWidth = Math.min(68, Math.max(54, contentW / 3));
        int meterX = contentX + circuitWidth + 6;
        int meterWidth = Math.max(78, contentW - circuitWidth - 6);
        int y = top + 52;
        drawCompactBar(g, meterX, y, meterWidth,
                Component.translatable("screen.fantasy_earth.attunement.mana"),
                status.maxMana() <= 0.0D ? 0.0D : status.currentMana() / status.maxMana(),
                Math.round(status.currentMana()) + " / " + Math.round(status.maxMana()), ACCENT);
        y += 17;
        drawCompactBar(g, meterX, y, meterWidth,
                Component.translatable("screen.fantasy_earth.attunement.field"),
                status.fieldValue() / 100.0D, status.fieldValue() + " / 100", fieldColor(status.fieldValue()));
        y += 17;
        drawCompactBar(g, meterX, y, meterWidth,
                Component.translatable("screen.fantasy_earth.attunement.disturbance"),
                status.disturbance() / 45.0D, Math.round(status.disturbance()) + " / 45", 0xFFD575B6);
        y += 17;
        boolean focusUnlocked = status.isUnlocked(focus);
        drawCompactBar(g, meterX, y, meterWidth,
                Component.translatable("screen.fantasy_earth.attunement.growth"),
                !focusUnlocked ? 0.0D : status.focusXpNeeded() <= 0
                        ? 1.0D : status.focusXp() / (double) status.focusXpNeeded(),
                !focusUnlocked
                        ? Component.translatable("screen.fantasy_earth.attunement.growth.locked").getString()
                        : status.focusXpNeeded() <= 0
                        ? "Lv." + status.focusLevel() + " · MAX"
                        : "Lv." + status.focusLevel() + " · " + status.focusXp() + "/" + status.focusXpNeeded(),
                0xFF75C7E8);
        drawCircuit(g, contentX, top + 52, circuitWidth, 73, status);

        int stageY = top + h - 39;
        drawStageTrack(g, contentX, stageY, contentW, status);

        if (h >= 194 && contentW >= 188) {
            Component source = Component.translatable("screen.fantasy_earth.attunement.source",
                    Component.translatable(status.sourceKey()));
            g.text(font, fitted(source, contentW), contentX, stageY - 12, MUTED, false);
        }
        super.extractRenderState(g, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if ((event.key() == InputConstants.KEY_LSHIFT || event.key() == InputConstants.KEY_RSHIFT)
                && FantasyEarthClient.arcaneStatus().seated()) {
            leaveFocusMat();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(null);
    }

    private void performPrimaryAction() {
        if (FantasyEarthClient.arcaneStatus().seated()) {
            leaveFocusMat();
        } else {
            FantasyEarthClient.requestPractice();
        }
    }

    private void leaveFocusMat() {
        FantasyEarthClient.requestStopAttuning();
        onClose();
    }

    private void updateButtons() {
        ArcaneStatusPayload status = FantasyEarthClient.arcaneStatus();
        ArcaneFocus selected = ArcaneFocus.byId(status.focusId());
        for (Map.Entry<ArcaneFocus, Button> entry : focusButtons.entrySet()) {
            boolean unlocked = status.isUnlocked(entry.getKey());
            entry.getValue().active = unlocked && entry.getKey() != selected;
            entry.getValue().setTooltip(Tooltip.create(Component.translatable(unlocked
                    ? entry.getKey().descriptionKey()
                    : "screen.fantasy_earth.attunement.focus.locked")));
        }
        if (primaryActionButton != null) {
            boolean seated = status.seated();
            primaryActionButton.setMessage(Component.translatable(seated
                    ? "screen.fantasy_earth.attunement.leave_focus_mat"
                    : "screen.fantasy_earth.attunement.practice"));
            primaryActionButton.active = seated
                    || status.isUnlocked(ArcaneFocus.ATTUNEMENT) && status.remainingTicks() <= 0;
            primaryActionButton.setTooltip(Tooltip.create(Component.translatable(seated
                    ? "screen.fantasy_earth.attunement.leave_focus_mat.tooltip"
                    : "screen.fantasy_earth.attunement.practice.tooltip")));
        }
        if (spellButton != null) {
            int seconds = Math.max(0, (status.skillRemainingTicks() + 19) / 20);
            spellButton.setMessage(seconds > 0
                    ? Component.translatable("screen.fantasy_earth.attunement.skill.cooldown", seconds)
                    : Component.translatable("screen.fantasy_earth.attunement.skill"));
            spellButton.active = status.isUnlocked(selected) && status.skillRemainingTicks() <= 0;
            spellButton.setTooltip(Tooltip.create(Component.translatable(
                    "screen.fantasy_earth.attunement.skill." + selected.path() + ".tooltip")));
        }
    }

    private void drawCompactBar(GuiGraphicsExtractor g, int x, int y, int width, Component label,
                                double ratio, String value, int color) {
        double clamped = Math.max(0.0D, Math.min(1.0D, ratio));
        g.text(font, label, x, y, MUTED, false);
        g.text(font, value, x + width - font.width(value), y, INK, false);
        g.fill(x, y + 11, x + width, y + 16, 0xFF373249);
        g.fill(x + 1, y + 12, x + 1 + (int) Math.round((width - 2) * clamped), y + 15, color);
    }

    private void drawCircuit(GuiGraphicsExtractor g, int x, int y, int width, int height,
                             ArcaneStatusPayload status) {
        int cx = x + width / 2;
        int cy = y + height / 2;
        int radius = Math.min(width, height) / 2 - 4;
        float progress = 1.0F - Math.min(1.0F,
                status.remainingTicks() / (float) ArcanaPower.MAGIC_FOCUS_COOLDOWN_TICKS);
        g.outline(cx - radius, cy - radius, radius * 2, radius * 2, 0xFF4D456D);
        g.outline(cx - radius + 6, cy - radius + 6, radius * 2 - 12, radius * 2 - 12, 0xFF62597B);
        g.fill(cx - radius, cy - 1, cx + radius, cy + 1, 0xFF39334E);
        g.fill(cx - 1, cy - radius, cx + 1, cy + radius, 0xFF39334E);
        int[][] nodes = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
        int activeNodes = Math.min(4, Math.max(0, (int) Math.ceil(progress * 4.0F)));
        for (int i = 0; i < nodes.length; i++) {
            int nx = cx + nodes[i][0] * radius;
            int ny = cy + nodes[i][1] * radius;
            int color = i < activeNodes ? ACCENT : 0xFF62597B;
            g.fill(nx - 3, ny - 3, nx + 4, ny + 4, 0xFF171421);
            g.fill(nx - 1, ny - 1, nx + 2, ny + 2, color);
        }
        int pulse = 2 + Math.round(2.0F * progress);
        g.fill(cx - pulse, cy - pulse, cx + pulse + 1, cy + pulse + 1, 0xFFE4DEFF);
    }

    private void drawStageTrack(GuiGraphicsExtractor g, int x, int y, int width, ArcaneStatusPayload status) {
        float progress = 1.0F - Math.min(1.0F, status.remainingTicks() / (float) ArcanaPower.MAGIC_FOCUS_COOLDOWN_TICKS);
        int current = Math.min(3, Math.max(0, (int) (progress * 4.0F)));
        int lineY = y + 2;
        int gap = 3;
        int segmentWidth = Math.max(8, (width - gap * 3) / 4);
        for (int i = 0; i < 4; i++) {
            int sx = x + i * (segmentWidth + gap);
            int color = i <= current ? ACCENT : 0xFF62597B;
            g.fill(sx, lineY, Math.min(x + width, sx + segmentWidth), lineY + 5, 0xFF312B43);
            g.fill(sx + 1, lineY + 1, Math.min(x + width, sx + segmentWidth - 1), lineY + 4, color);
        }
        Component stage = Component.translatable(ArcaneFocusHud.stageKey(current));
        Component remaining = Component.translatable("screen.fantasy_earth.attunement.remaining",
                Math.max(0, (status.remainingTicks() + 19) / 20));
        g.text(font, stage, x, y + 10, ACCENT, false);
        g.text(font, remaining, x + width - font.width(remaining), y + 10, MUTED, false);
    }

    private void drawAmbientMotif(GuiGraphicsExtractor g, int left, int top, int width) {
        long time = Minecraft.getInstance().level == null ? 0L : Minecraft.getInstance().level.getGameTime();
        int startX = left + Math.max(76, navigationWidth() - 36);
        for (int i = 0; i < 4; i++) {
            int px = startX + i * 8;
            int py = top + 8 + (int) Math.round(Math.cos((time + i * 9) * 0.11D) * 3.0D);
            g.fill(px, py, px + 2, py + 2, i % 2 == 0 ? ACCENT : 0xFFE4DEFF);
        }
    }

    private static int fieldColor(int value) {
        if (value < 25) return LOCKED;
        if (value < 50) return 0xFFD6A55B;
        return ACCENT;
    }

    private Component fitted(Component component, int width) {
        String text = component.getString();
        if (font.width(text) <= width) {
            return component;
        }
        return Component.literal(font.plainSubstrByWidth(text, Math.max(0, width - font.width("..."))) + "...");
    }

    private int panelWidth() {
        return computePanelWidth(width);
    }

    private int panelHeight() {
        return computePanelHeight(height);
    }

    private int navigationWidth() {
        return computeNavigationWidth(panelWidth());
    }

    private int panelLeft() {
        return (width - panelWidth()) / 2;
    }

    private int panelTop() {
        return (height - panelHeight()) / 2;
    }

    public static int computePanelWidth(int screenWidth) {
        return Math.min(430, Math.max(304, screenWidth - 16));
    }

    public static int computePanelHeight(int screenHeight) {
        return Math.min(238, Math.max(188, screenHeight - 16));
    }

    public static int computeNavigationWidth(int panelWidth) {
        return Math.min(134, Math.max(118, panelWidth / 3));
    }

    public static LayoutMetrics computeLayout(int screenWidth, int screenHeight) {
        int panelW = computePanelWidth(screenWidth);
        int panelH = computePanelHeight(screenHeight);
        int left = (screenWidth - panelW) / 2;
        int top = (screenHeight - panelH) / 2;
        int navW = computeNavigationWidth(panelW);
        int navAreaW = navW - 16;
        int cellW = (navAreaW - 4) / 2;
        int rows = (ArcaneFocus.values().length + 1) / 2;
        int focusStartY = top + 38;
        int focusBottomY = focusStartY + (rows - 1) * 20 + 18;
        int spellY = top + panelH - 48;
        int practiceY = top + panelH - 26;
        int closeX = left + panelW - 26;
        int loreW = 54;
        int loreX = closeX - 4 - loreW;
        int hubW = 50;
        int hubX = loreX - 4 - hubW;
        return new LayoutMetrics(
                left, top, panelW, panelH, navW, navAreaW, cellW,
                focusStartY, focusBottomY, spellY, practiceY,
                hubX, hubW, loreX, loreW, closeX
        );
    }

    public record LayoutMetrics(
            int left,
            int top,
            int panelWidth,
            int panelHeight,
            int navWidth,
            int navButtonAreaWidth,
            int focusCellWidth,
            int focusStartY,
            int focusBottomY,
            int spellButtonY,
            int primaryActionButtonY,
            int hubButtonX,
            int hubButtonWidth,
            int loreTreeButtonX,
            int loreTreeButtonWidth,
            int closeButtonX
    ) {
        public boolean hasNoVerticalOverlap() {
            return focusBottomY + 4 <= spellButtonY
                    && spellButtonY + 18 + 2 <= primaryActionButtonY
                    && primaryActionButtonY + 18 <= top + panelHeight - 4;
        }

        public boolean hasNoHeaderOverlap() {
            return hubButtonX > left + 64
                    && hubButtonX + hubButtonWidth + 2 <= loreTreeButtonX
                    && loreTreeButtonX + loreTreeButtonWidth + 2 <= closeButtonX;
        }
    }
}
