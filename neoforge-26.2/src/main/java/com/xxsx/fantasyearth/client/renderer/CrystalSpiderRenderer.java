package com.xxsx.fantasyearth.client.renderer;

import com.xxsx.fantasyearth.FantasyEarth;
import com.xxsx.fantasyearth.client.model.CrystalSpiderModel;
import com.xxsx.fantasyearth.entity.CrystalArmoredSpiderEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.Mth;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.layers.LivingEntityEmissiveLayer;
import net.minecraft.resources.Identifier;

public final class CrystalSpiderRenderer extends MobRenderer<CrystalArmoredSpiderEntity,
        CrystalSpiderRenderState, CrystalSpiderModel> {
    private static final Identifier TEXTURE = FantasyEarth.id("textures/entity/crystal_armored_spider.png");

    public CrystalSpiderRenderer(EntityRendererProvider.Context context) {
        super(context, new CrystalSpiderModel(context.bakeLayer(CrystalSpiderModel.LAYER_LOCATION)), 0.75F);
        this.addLayer(new LivingEntityEmissiveLayer<>(this, state -> TEXTURE,
                (state, age) -> 0.04F + (state.aggressive ? 0.08F : 0.0F) + Mth.sin(age * 0.16F) * 0.02F,
                this.getModel(), RenderTypes::entityTranslucentEmissive, false));
    }

    @Override
    public CrystalSpiderRenderState createRenderState() {
        return new CrystalSpiderRenderState();
    }

    @Override
    public void extractRenderState(CrystalArmoredSpiderEntity entity, CrystalSpiderRenderState state,
                                   float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.attackProgress = entity.getAttackAnim(partialTick);
        state.aggressive = entity.getTarget() != null;
    }

    @Override
    public Identifier getTextureLocation(CrystalSpiderRenderState state) {
        return TEXTURE;
    }
}
