package com.xxsx.fantasyearth.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.xxsx.fantasyearth.entity.ContractableFamiliarEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;
import net.minecraft.resources.Identifier;

import java.util.function.Function;

public final class FamiliarRenderer<T extends ContractableFamiliarEntity,
        M extends EntityModel<FamiliarRenderState>> extends MobRenderer<T, FamiliarRenderState, M> {
    private final Identifier texture;
    private final float scale;

    public FamiliarRenderer(EntityRendererProvider.Context context, ModelLayerLocation layer,
                            Function<ModelPart, M> modelFactory, Identifier texture,
                            float shadowRadius, float scale) {
        super(context, modelFactory.apply(context.bakeLayer(layer)), shadowRadius);
        this.texture = texture;
        this.scale = scale;
        this.addLayer(new LivingEntityEmissiveLayer<>(this, state -> texture,
                (state, age) -> 0.05F + state.stability * 0.10F + (state.aggressive ? 0.06F : 0.0F)
                        + Mth.sin(age * 0.12F) * 0.02F,
                this.getModel(), RenderTypes::entityTranslucentEmissive, false));
    }

    @Override
    public FamiliarRenderState createRenderState() {
        return new FamiliarRenderState();
    }

    @Override
    public void extractRenderState(T entity, FamiliarRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.stability = entity.getStability() / (float) ContractableFamiliarEntity.MAX_STABILITY;
        state.familiarMode = entity.getFamiliarMode().id();
        state.aggressive = entity.getTarget() != null;
        state.attackProgress = entity.getAttackAnim(partialTick);
        state.sitting = entity.isInSittingPose();
    }

    @Override
    public Identifier getTextureLocation(FamiliarRenderState state) {
        return texture;
    }

    @Override
    protected void scale(FamiliarRenderState state, PoseStack poseStack) {
        float resolved = state.isBaby ? scale * 0.62F : scale;
        poseStack.scale(resolved, resolved, resolved);
    }
}
