package com.xxsx.fantasyearth.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xxsx.fantasyearth.FantasyEarth;
import com.xxsx.fantasyearth.client.model.RunicWatcherModel;
import com.xxsx.fantasyearth.entity.RunicWatcherEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.resources.Identifier;

public final class RunicWatcherRenderer extends MobRenderer<RunicWatcherEntity, RunicWatcherRenderState, RunicWatcherModel> {
    private static final Identifier TEXTURE = FantasyEarth.id("textures/entity/runic_watcher.png");

    public RunicWatcherRenderer(EntityRendererProvider.Context context) {
        super(context, new RunicWatcherModel(context.bakeLayer(RunicWatcherModel.LAYER_LOCATION)), 0.52F);
        this.addLayer(new LivingEntityEmissiveLayer<>(this, state -> TEXTURE,
                (state, age) -> 0.05F + (1.0F - state.integrity) * 0.16F + state.attackProgress * 0.12F,
                this.getModel(), RenderTypes::entityTranslucentEmissive, false));
    }

    @Override
    public RunicWatcherRenderState createRenderState() {
        return new RunicWatcherRenderState();
    }

    @Override
    public void extractRenderState(RunicWatcherEntity entity, RunicWatcherRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.aggressive = entity.getTarget() != null;
        state.integrity = entity.getHealth() / entity.getMaxHealth();
        state.attackProgress = entity.getAttackAnim(partialTick);
    }

    @Override
    public Identifier getTextureLocation(RunicWatcherRenderState state) {
        return TEXTURE;
    }

    @Override
    protected void scale(RunicWatcherRenderState state, PoseStack poseStack) {
        poseStack.scale(1.04F, 1.04F, 1.04F);
    }
}
