package com.xxsx.fantasyearth.client.model;

import com.xxsx.fantasyearth.FantasyEarth;
import com.xxsx.fantasyearth.client.CreatureMotion;
import com.xxsx.fantasyearth.client.renderer.CrystalSpiderRenderState;
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

public final class CrystalSpiderModel extends EntityModel<CrystalSpiderRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            FantasyEarth.id("crystal_armored_spider"), "main");

    private final ModelPart head;
    private final ModelPart leftFang;
    private final ModelPart rightFang;
    private final ModelPart abdomen;
    private final ModelPart crystals;
    private final ModelPart[] legs = new ModelPart[8];
    private final ModelPart[] lowerLegs = new ModelPart[8];

    public CrystalSpiderModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.head = root.getChild("head");
        this.leftFang = this.head.getChild("left_fang");
        this.rightFang = this.head.getChild("right_fang");
        this.abdomen = root.getChild("abdomen");
        this.crystals = this.abdomen.getChild("crystals");
        for (int i = 0; i < this.legs.length; i++) {
            this.legs[i] = root.getChild("leg_" + i);
            this.lowerLegs[i] = this.legs[i].getChild("lower");
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_head_1 = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -3.0F, -5.0F, 8.0F, 6.0F, 6.0F)
                        .texOffs(29, 0).addBox(-3.0F, -4.0F, -4.0F, 6.0F, 2.0F, 5.0F)
                        .texOffs(52, 0).addBox(-3.0F, 0.0F, -6.5F, 6.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 15.0F, -4.0F, 0.06F, 0.0F, 0.0F));
        p_head_1.addOrReplaceChild("left_fang", CubeListBuilder.create()
                        .texOffs(69, 0).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 3.0F, 3.0F)
                        .texOffs(80, 0).addBox(-0.5F, 0.5F, -5.0F, 1.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(2.0F, 1.5F, -5.5F, 0.28F, -0.32F, -0.14F));
        p_head_1.addOrReplaceChild("right_fang", CubeListBuilder.create()
                        .texOffs(87, 0).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 3.0F, 3.0F)
                        .texOffs(98, 0).addBox(-0.5F, 0.5F, -5.0F, 1.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-2.0F, 1.5F, -5.5F, 0.28F, 0.32F, 0.14F));
        p_head_1.addOrReplaceChild("head_horn", CubeListBuilder.create()
                        .texOffs(105, 0).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -3.5F, -2.5F, -0.42F, 0.0F, 0.0F));
        PartDefinition p_abdomen_5 = root.addOrReplaceChild("abdomen", CubeListBuilder.create()
                        .texOffs(0, 13).addBox(-5.0F, -4.0F, -2.0F, 10.0F, 8.0F, 11.0F)
                        .texOffs(43, 13).addBox(-5.5F, -4.5F, -1.0F, 11.0F, 4.0F, 9.0F, new CubeDeformation(0.12F)),
                PartPose.offsetAndRotation(0.0F, 14.5F, 1.5F, -0.08F, 0.0F, 0.0F));
        p_abdomen_5.addOrReplaceChild("plate_left", CubeListBuilder.create()
                        .texOffs(84, 13).addBox(-1.0F, -2.0F, -4.5F, 2.0F, 5.0F, 9.0F),
                PartPose.offsetAndRotation(4.5F, -2.0F, 3.0F, 0.0F, 0.08F, 0.38F));
        p_abdomen_5.addOrReplaceChild("plate_right", CubeListBuilder.create()
                        .texOffs(0, 33).addBox(-1.0F, -2.0F, -4.5F, 2.0F, 5.0F, 9.0F),
                PartPose.offsetAndRotation(-4.5F, -2.0F, 3.0F, 0.0F, -0.08F, -0.38F));
        p_abdomen_5.addOrReplaceChild("tail_stinger", CubeListBuilder.create()
                        .texOffs(23, 33).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, 8.5F, -0.32F, 0.0F, 0.0F));
        PartDefinition p_crystals_9 = p_abdomen_5.addOrReplaceChild("crystals", CubeListBuilder.create(),
                PartPose.offset(0.0F, -4.0F, 3.0F));
        p_crystals_9.addOrReplaceChild("spire_main", CubeListBuilder.create()
                        .texOffs(38, 33).addBox(-1.5F, -8.0F, -1.5F, 3.0F, 8.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.24F, 0.0F, 0.06F));
        p_crystals_9.addOrReplaceChild("spire_front_l", CubeListBuilder.create()
                        .texOffs(51, 33).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(2.5F, 0.5F, -2.0F, 0.34F, 0.18F, 0.46F));
        p_crystals_9.addOrReplaceChild("spire_front_r", CubeListBuilder.create()
                        .texOffs(60, 33).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(-2.5F, 0.5F, -2.0F, 0.34F, -0.18F, -0.46F));
        p_crystals_9.addOrReplaceChild("spire_mid_l", CubeListBuilder.create()
                        .texOffs(69, 33).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(3.5F, 1.0F, 1.5F, -0.12F, -0.14F, 0.62F));
        p_crystals_9.addOrReplaceChild("spire_mid_r", CubeListBuilder.create()
                        .texOffs(78, 33).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 6.0F, 2.0F),
                PartPose.offsetAndRotation(-3.5F, 1.0F, 1.5F, -0.12F, 0.14F, -0.62F));
        p_crystals_9.addOrReplaceChild("spire_rear_l", CubeListBuilder.create()
                        .texOffs(87, 33).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(2.0F, 1.0F, 4.5F, -0.48F, -0.22F, 0.36F));
        p_crystals_9.addOrReplaceChild("spire_rear_r", CubeListBuilder.create()
                        .texOffs(96, 33).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(-2.0F, 1.0F, 4.5F, -0.48F, 0.22F, -0.36F));
        PartDefinition p_leg_0_17 = root.addOrReplaceChild("leg_0", CubeListBuilder.create()
                        .texOffs(105, 33).addBox(-7.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F)
                        .texOffs(0, 48).addBox(-6.0F, -2.0F, -0.5F, 4.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-3.5F, 15.5F, -3.6F, 0.0F, -0.88F, 0.56F));
        PartDefinition p_lower_18 = p_leg_0_17.addOrReplaceChild("lower", CubeListBuilder.create()
                        .texOffs(11, 48).addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-6.5F, 0.0F, 0.0F, 0.0F, 0.0F, -1.14F));
        p_lower_18.addOrReplaceChild("claw", CubeListBuilder.create()
                        .texOffs(32, 48).addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-7.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.28F));
        PartDefinition p_leg_1_20 = root.addOrReplaceChild("leg_1", CubeListBuilder.create()
                        .texOffs(41, 48).addBox(-7.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F)
                        .texOffs(60, 48).addBox(-6.0F, -2.0F, -0.5F, 4.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-3.5F, 15.5F, -1.4F, 0.0F, -0.3F, 0.64F));
        PartDefinition p_lower_21 = p_leg_1_20.addOrReplaceChild("lower", CubeListBuilder.create()
                        .texOffs(71, 48).addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-6.5F, 0.0F, 0.0F, 0.0F, 0.0F, -1.2F));
        p_lower_21.addOrReplaceChild("claw", CubeListBuilder.create()
                        .texOffs(92, 48).addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-7.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.28F));
        PartDefinition p_leg_2_23 = root.addOrReplaceChild("leg_2", CubeListBuilder.create()
                        .texOffs(101, 48).addBox(-7.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F)
                        .texOffs(0, 53).addBox(-6.0F, -2.0F, -0.5F, 4.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-3.5F, 15.5F, 0.8F, 0.0F, 0.3F, 0.64F));
        PartDefinition p_lower_24 = p_leg_2_23.addOrReplaceChild("lower", CubeListBuilder.create()
                        .texOffs(11, 53).addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-6.5F, 0.0F, 0.0F, 0.0F, 0.0F, -1.2F));
        p_lower_24.addOrReplaceChild("claw", CubeListBuilder.create()
                        .texOffs(32, 53).addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-7.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.28F));
        PartDefinition p_leg_3_26 = root.addOrReplaceChild("leg_3", CubeListBuilder.create()
                        .texOffs(41, 53).addBox(-7.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F)
                        .texOffs(60, 53).addBox(-6.0F, -2.0F, -0.5F, 4.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-3.5F, 15.5F, 3.0F, 0.0F, 0.88F, 0.56F));
        PartDefinition p_lower_27 = p_leg_3_26.addOrReplaceChild("lower", CubeListBuilder.create()
                        .texOffs(71, 53).addBox(-8.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-6.5F, 0.0F, 0.0F, 0.0F, 0.0F, -1.14F));
        p_lower_27.addOrReplaceChild("claw", CubeListBuilder.create()
                        .texOffs(92, 53).addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-7.5F, 0.0F, 0.0F, 0.0F, 0.0F, -0.28F));
        PartDefinition p_leg_4_29 = root.addOrReplaceChild("leg_4", CubeListBuilder.create()
                        .texOffs(101, 53).addBox(0.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F)
                        .texOffs(0, 58).addBox(2.0F, -2.0F, -0.5F, 4.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(3.5F, 15.5F, -3.6F, 0.0F, 0.88F, -0.56F));
        PartDefinition p_lower_30 = p_leg_4_29.addOrReplaceChild("lower", CubeListBuilder.create()
                        .texOffs(11, 58).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(6.5F, 0.0F, 0.0F, 0.0F, 0.0F, 1.14F));
        p_lower_30.addOrReplaceChild("claw", CubeListBuilder.create()
                        .texOffs(32, 58).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(7.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.28F));
        PartDefinition p_leg_5_32 = root.addOrReplaceChild("leg_5", CubeListBuilder.create()
                        .texOffs(41, 58).addBox(0.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F)
                        .texOffs(60, 58).addBox(2.0F, -2.0F, -0.5F, 4.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(3.5F, 15.5F, -1.4F, 0.0F, 0.3F, -0.64F));
        PartDefinition p_lower_33 = p_leg_5_32.addOrReplaceChild("lower", CubeListBuilder.create()
                        .texOffs(71, 58).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(6.5F, 0.0F, 0.0F, 0.0F, 0.0F, 1.2F));
        p_lower_33.addOrReplaceChild("claw", CubeListBuilder.create()
                        .texOffs(92, 58).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(7.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.28F));
        PartDefinition p_leg_6_35 = root.addOrReplaceChild("leg_6", CubeListBuilder.create()
                        .texOffs(101, 58).addBox(0.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F)
                        .texOffs(0, 63).addBox(2.0F, -2.0F, -0.5F, 4.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(3.5F, 15.5F, 0.8F, 0.0F, -0.3F, -0.64F));
        PartDefinition p_lower_36 = p_leg_6_35.addOrReplaceChild("lower", CubeListBuilder.create()
                        .texOffs(11, 63).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(6.5F, 0.0F, 0.0F, 0.0F, 0.0F, 1.2F));
        p_lower_36.addOrReplaceChild("claw", CubeListBuilder.create()
                        .texOffs(32, 63).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(7.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.28F));
        PartDefinition p_leg_7_38 = root.addOrReplaceChild("leg_7", CubeListBuilder.create()
                        .texOffs(41, 63).addBox(0.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F)
                        .texOffs(60, 63).addBox(2.0F, -2.0F, -0.5F, 4.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(3.5F, 15.5F, 3.0F, 0.0F, -0.88F, -0.56F));
        PartDefinition p_lower_39 = p_leg_7_38.addOrReplaceChild("lower", CubeListBuilder.create()
                        .texOffs(71, 63).addBox(0.0F, -1.0F, -1.0F, 8.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(6.5F, 0.0F, 0.0F, 0.0F, 0.0F, 1.14F));
        p_lower_39.addOrReplaceChild("claw", CubeListBuilder.create()
                        .texOffs(92, 63).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(7.5F, 0.0F, 0.0F, 0.0F, 0.0F, 0.28F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(CrystalSpiderRenderState state) {
        super.setupAnim(state);
        float age = state.ageInTicks;
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float idle = CreatureMotion.idleBlend(speed);
        float breathe = CreatureMotion.breathe(age, 0.10F);
        float alert = state.aggressive ? 1.0F : 0.0F;
        float wind = CreatureMotion.windUp(state.attackProgress);
        float strike = CreatureMotion.strike(state.attackProgress);

        this.head.yRot += CreatureMotion.clampLook(state.yRot * Mth.DEG_TO_RAD * 0.55F, 0.45F);
        this.head.xRot += CreatureMotion.clampLook(state.xRot * Mth.DEG_TO_RAD * 0.45F, 0.35F)
                + breathe * 0.02F - wind * 0.12F - strike * 0.28F;
        this.head.zRot += CreatureMotion.wave(age, 0.05F, 0.9F) * 0.02F;
        this.head.z -= strike * 1.2F;

        float fangSwing = CreatureMotion.wave(age, 0.14F, 0.0F) * 0.08F + alert * 0.16F + strike * 0.44F;
        this.leftFang.yRot += fangSwing;
        this.rightFang.yRot -= fangSwing;
        this.leftFang.xRot += strike * 0.22F;
        this.rightFang.xRot += strike * 0.22F;

        for (int i = 0; i < this.legs.length; i++) {
            boolean left = i >= 4;
            int row = i % 4;
            float sign = left ? 1.0F : -1.0F;
            float phase = walk * 0.82F + row * 1.05F + (left ? Mth.PI : 0.0F);
            float lift = Mth.cos(phase) * 0.24F * speed;
            float swing = Mth.sin(phase) * 0.22F * speed;
            this.legs[i].zRot += -sign * lift - sign * wind * 0.16F;
            this.legs[i].yRot += swing;
            this.lowerLegs[i].zRot += sign * (Mth.sin(phase + 0.5F) * 0.18F * speed + strike * 0.22F + breathe * 0.03F);
        }

        float pulse = 1.0F + CreatureMotion.breathe(age, 0.18F) * 0.05F;
        this.crystals.xScale = pulse;
        this.crystals.yScale = pulse;
        this.crystals.zScale = pulse;
        this.crystals.yRot += CreatureMotion.wave(age, 0.03F, 0.0F) * 0.06F;

        this.abdomen.y += breathe * 0.08F + Mth.cos(walk * 0.9F) * 0.08F * speed - strike * 0.25F;
        this.abdomen.xRot += -alert * 0.04F + Mth.sin(walk * 0.9F) * 0.025F * speed - wind * 0.10F;
        this.abdomen.zRot += CreatureMotion.wave(age, 0.04F, 2.2F) * 0.02F * idle;
    }
}
