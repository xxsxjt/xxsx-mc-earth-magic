package com.xxsx.fantasyearth.client.model;

import com.xxsx.fantasyearth.FantasyEarth;
import com.xxsx.fantasyearth.client.CreatureMotion;
import com.xxsx.fantasyearth.client.renderer.FamiliarRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.Mth;

public final class ManaWispModel extends EntityModel<FamiliarRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(FantasyEarth.id("mana_wisp"), "main");
    private final ModelPart core;
    private final ModelPart ring;
    private final ModelPart leftFin;
    private final ModelPart rightFin;
    private final ModelPart flame;
    private final ModelPart moteA;
    private final ModelPart moteB;
    private final ModelPart moteC;

    public ManaWispModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        core = root.getChild("core");
        ring = root.getChild("ring");
        leftFin = root.getChild("left_fin");
        rightFin = root.getChild("right_fin");
        flame = root.getChild("flame");
        moteA = root.getChild("mote_a");
        moteB = root.getChild("mote_b");
        moteC = root.getChild("mote_c");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("core", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F)
                        .texOffs(24, 0).addBox(-3.5F, -3.5F, -3.5F, 7.0F, 7.0F, 7.0F, new CubeDeformation(0.12F)),
                PartPose.offset(0.0F, 13.0F, 0.0F));
        root.addOrReplaceChild("ring", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-5.0F, -1.0F, -5.0F, 10.0F, 2.0F, 10.0F, new CubeDeformation(-0.15F)),
                PartPose.offset(0.0F, 16.0F, 0.0F));
        root.addOrReplaceChild("left_fin", CubeListBuilder.create().texOffs(0, 30)
                        .addBox(0.0F, -4.0F, -1.0F, 7.0F, 8.0F, 1.0F),
                PartPose.offsetAndRotation(2.5F, 13.0F, 0.0F, 0.0F, 0.22F, -0.16F));
        root.addOrReplaceChild("right_fin", CubeListBuilder.create().texOffs(16, 30)
                        .addBox(-7.0F, -4.0F, -1.0F, 7.0F, 8.0F, 1.0F),
                PartPose.offsetAndRotation(-2.5F, 13.0F, 0.0F, 0.0F, -0.22F, 0.16F));
        root.addOrReplaceChild("flame", CubeListBuilder.create().texOffs(34, 30)
                        .addBox(-2.0F, 0.0F, -2.0F, 4.0F, 8.0F, 4.0F),
                PartPose.offset(0.0F, 16.0F, 0.0F));
        for (int i = 0; i < 3; i++) {
            PartDefinition mote = root.addOrReplaceChild("mote_" + (char) ('a' + i),
                    CubeListBuilder.create().texOffs(0, 0)
                            .addBox(-0.7F, -0.7F, -0.7F, 1.4F, 1.4F, 1.4F),
                    PartPose.offset(0.0F, 13.0F, 0.0F));
            mote.addOrReplaceChild("glow", CubeListBuilder.create().texOffs(0, 0)
                            .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.06F)),
                    PartPose.ZERO);
        }
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
        public void setupAnim(FamiliarRenderState state) {
        super.setupAnim(state);
        float age = state.ageInTicks;
        float breathe = CreatureMotion.breathe(age, 0.12F);
        float strike = CreatureMotion.strike(state.attackProgress);

        core.y = 13.0F + breathe * 0.8F + CreatureMotion.breathe(age, 0.31F) * 0.18F;
        core.yRot = age * 0.06F;
        ring.y = 16.0F + breathe * 0.5F;
        ring.yRot = age * 0.045F;
        ring.xRot = CreatureMotion.wave(age, 0.07F, 0.4F) * 0.32F;
        ring.zRot = CreatureMotion.wave(age, 0.05F, 1.7F) * 0.32F;

        leftFin.zRot = -0.16F - CreatureMotion.wave(age, 0.18F, 0.0F) * 0.24F;
        rightFin.zRot = -leftFin.zRot;
        leftFin.xRot = -0.08F - CreatureMotion.breathe(age, 0.24F) * 0.14F;
        rightFin.xRot = leftFin.xRot;

        flame.yScale = 1.0F + CreatureMotion.wave(age, 0.20F, 0.9F) * (0.10F + state.stability * 0.06F);
        flame.xScale = 1.0F - CreatureMotion.wave(age, 0.20F, 0.9F) * 0.05F;
        flame.zScale = flame.xScale;
        flame.yRot = age * 0.09F;

        float pulse = 1.0F + CreatureMotion.breathe(age, 0.24F) * 0.06F;
        core.xScale = pulse;
        core.zScale = pulse;

        orbit(moteA, age, 0.0F, 2.6F, 1.6F, 0.9F);
        orbit(moteB, age, Mth.TWO_PI / 3.0F, 3.1F, 2.1F, 1.3F);
        orbit(moteC, age, Mth.TWO_PI * 2.0F / 3.0F, 2.2F, 1.1F, 1.8F);

        float attack = Mth.sin(state.attackProgress * Mth.PI);
        ring.xScale = 1.0F + attack * 0.45F + strike * 0.25F;
        ring.zScale = ring.xScale;
        flame.yScale += attack * 0.35F;
        core.yScale = 1.0F + attack * 0.18F;

        if (state.sitting) {
            core.y += 1.4F;
            ring.y += 1.4F;
            leftFin.zRot = -0.08F;
            rightFin.zRot = 0.08F;
        }
    }

    private static void orbit(ModelPart mote, float age, float offset, float radius, float tilt, float speed) {
        float angle = age * 0.08F * speed + offset;
        mote.x = Mth.cos(angle) * radius;
        mote.z = Mth.sin(angle) * radius;
        mote.y = 13.0F + Mth.sin(angle * 1.6F) * tilt + CreatureMotion.breathe(age, 0.15F + speed * 0.05F) * 0.3F;
        mote.yRot = angle;
        mote.zRot = Mth.sin(angle) * 0.5F;
    }
}
