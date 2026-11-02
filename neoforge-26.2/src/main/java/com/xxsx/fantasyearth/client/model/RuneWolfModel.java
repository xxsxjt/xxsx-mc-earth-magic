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

public final class RuneWolfModel extends EntityModel<FamiliarRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            FantasyEarth.id("rune_wolf"), "main");

    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart body;
    private final ModelPart armor;
    private final ModelPart mane;
    private final ModelPart lens;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart[] legs;
    private final ModelPart tail;
    private final ModelPart tailMid;
    private final ModelPart tailTip;

    public RuneWolfModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.head = root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.body = root.getChild("body");
        this.armor = this.body.getChild("armor");
        this.mane = this.body.getChild("mane");
        this.lens = this.head.getChild("lens");
        this.leftEar = this.head.getChild("left_ear");
        this.rightEar = this.head.getChild("right_ear");
        this.legs = new ModelPart[]{
                root.getChild("front_right_leg"),
                root.getChild("front_left_leg"),
                root.getChild("back_right_leg"),
                root.getChild("back_left_leg")
        };
        this.tail = root.getChild("tail");
        this.tailMid = this.tail.getChild("tail_mid");
        this.tailTip = this.tailMid.getChild("tail_tip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body_1 = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 7.0F)
                        .texOffs(31, 0).addBox(-3.0F, -3.2F, 1.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 13.5F, 0.0F, -0.06F, 0.0F, 0.0F));
        PartDefinition p_armor_2 = p_body_1.addOrReplaceChild("armor", CubeListBuilder.create()
                        .texOffs(56, 0).addBox(-4.5F, -4.5F, -5.0F, 9.0F, 4.0F, 9.0F, new CubeDeformation(0.1F)),
                PartPose.ZERO);
        p_armor_2.addOrReplaceChild("pauldron_left", CubeListBuilder.create()
                        .texOffs(93, 0).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 5.0F, 6.0F)
                        .texOffs(110, 0).addBox(0.2F, -2.5F, -2.0F, 1.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(3.6F, -2.2F, -3.5F, 0.08F, 0.14F, 0.34F));
        p_armor_2.addOrReplaceChild("pauldron_right", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 5.0F, 6.0F)
                        .texOffs(17, 16).addBox(-1.2F, -2.5F, -2.0F, 1.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(-3.6F, -2.2F, -3.5F, 0.08F, -0.14F, -0.34F));
        p_armor_2.addOrReplaceChild("back_rune", CubeListBuilder.create()
                        .texOffs(28, 16).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 2.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, -4.8F, -0.5F, -0.18F, 0.0F, 0.0F));
        PartDefinition p_mane_6 = p_body_1.addOrReplaceChild("mane", CubeListBuilder.create()
                        .texOffs(45, 16).addBox(-4.5F, -3.5F, -3.0F, 9.0F, 7.0F, 5.0F, new CubeDeformation(0.05F)),
                PartPose.offsetAndRotation(0.0F, -2.0F, -5.0F, 0.18F, 0.0F, 0.0F));
        p_mane_6.addOrReplaceChild("mane_crest_top", CubeListBuilder.create()
                        .texOffs(74, 16).addBox(-2.0F, -1.5F, 0.0F, 4.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -3.5F, -1.5F, 0.28F, 0.0F, 0.0F));
        p_mane_6.addOrReplaceChild("mane_crest_left", CubeListBuilder.create()
                        .texOffs(95, 16).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(3.8F, -1.5F, -1.0F, 0.18F, 0.34F, 0.24F));
        p_mane_6.addOrReplaceChild("mane_crest_right", CubeListBuilder.create()
                        .texOffs(0, 29).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(-3.8F, -1.5F, -1.0F, 0.18F, -0.34F, -0.24F));
        PartDefinition p_head_10 = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(17, 29).addBox(-3.5F, -3.5F, -4.0F, 7.0F, 7.0F, 6.0F)
                        .texOffs(44, 29).addBox(-3.5F, -3.8F, -4.2F, 7.0F, 2.0F, 4.0F, new CubeDeformation(0.06F)),
                PartPose.offsetAndRotation(0.0F, 10.5F, -7.5F, 0.05F, 0.0F, 0.0F));
        p_head_10.addOrReplaceChild("snout", CubeListBuilder.create()
                        .texOffs(67, 29).addBox(-2.0F, -1.0F, -4.0F, 4.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, -4.0F, 0.06F, 0.0F, 0.0F));
        p_head_10.addOrReplaceChild("jaw", CubeListBuilder.create()
                        .texOffs(84, 29).addBox(-1.5F, 0.0F, -3.5F, 3.0F, 1.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 2.2F, -4.0F, 0.08F, 0.0F, 0.0F));
        p_head_10.addOrReplaceChild("lens", CubeListBuilder.create()
                        .texOffs(99, 29).addBox(-0.5F, -1.0F, -0.2F, 1.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, -2.5F, -4.1F, -0.06F, 0.0F, 0.0F));
        p_head_10.addOrReplaceChild("left_ear", CubeListBuilder.create()
                        .texOffs(104, 29).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(2.4F, -3.0F, -1.0F, -0.26F, -0.18F, 0.34F));
        p_head_10.addOrReplaceChild("right_ear", CubeListBuilder.create()
                        .texOffs(113, 29).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(-2.4F, -3.0F, -1.0F, -0.26F, 0.18F, -0.34F));
        PartDefinition p_front_right_leg_16 = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create()
                        .texOffs(0, 43).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 5.0F, 3.0F)
                        .texOffs(13, 43).addBox(-1.5F, 0.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.08F)),
                PartPose.offsetAndRotation(-2.6F, 14.5F, -4.5F, -0.18F, 0.0F, 0.0F));
        PartDefinition p_shin_17 = p_front_right_leg_16.addOrReplaceChild("shin", CubeListBuilder.create()
                        .texOffs(26, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, 0.2F, 0.34F, 0.0F, 0.0F));
        p_shin_17.addOrReplaceChild("paw", CubeListBuilder.create()
                        .texOffs(35, 43).addBox(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.5F, -0.2F, -0.16F, 0.0F, 0.0F));
        PartDefinition p_front_left_leg_19 = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create()
                        .texOffs(50, 43).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 5.0F, 3.0F)
                        .texOffs(63, 43).addBox(-1.5F, 0.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.08F)),
                PartPose.offsetAndRotation(2.6F, 14.5F, -4.5F, -0.18F, 0.0F, 0.0F));
        PartDefinition p_shin_20 = p_front_left_leg_19.addOrReplaceChild("shin", CubeListBuilder.create()
                        .texOffs(76, 43).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, 0.2F, 0.34F, 0.0F, 0.0F));
        p_shin_20.addOrReplaceChild("paw", CubeListBuilder.create()
                        .texOffs(85, 43).addBox(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.5F, -0.2F, -0.16F, 0.0F, 0.0F));
        PartDefinition p_back_right_leg_22 = root.addOrReplaceChild("back_right_leg", CubeListBuilder.create()
                        .texOffs(100, 43).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 5.0F, 3.0F)
                        .texOffs(113, 43).addBox(-1.5F, 0.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.08F)),
                PartPose.offsetAndRotation(-2.5F, 14.5F, 4.8F, 0.34F, 0.0F, 0.0F));
        PartDefinition p_shin_23 = p_back_right_leg_22.addOrReplaceChild("shin", CubeListBuilder.create()
                        .texOffs(0, 52).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, 0.2F, -0.62F, 0.0F, 0.0F));
        p_shin_23.addOrReplaceChild("paw", CubeListBuilder.create()
                        .texOffs(9, 52).addBox(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.5F, -0.2F, 0.28F, 0.0F, 0.0F));
        PartDefinition p_back_left_leg_25 = root.addOrReplaceChild("back_left_leg", CubeListBuilder.create()
                        .texOffs(24, 52).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 5.0F, 3.0F)
                        .texOffs(37, 52).addBox(-1.5F, 0.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.08F)),
                PartPose.offsetAndRotation(2.5F, 14.5F, 4.8F, 0.34F, 0.0F, 0.0F));
        PartDefinition p_shin_26 = p_back_left_leg_25.addOrReplaceChild("shin", CubeListBuilder.create()
                        .texOffs(50, 52).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.2F, 0.2F, -0.62F, 0.0F, 0.0F));
        p_shin_26.addOrReplaceChild("paw", CubeListBuilder.create()
                        .texOffs(59, 52).addBox(-1.5F, 0.0F, -2.5F, 3.0F, 2.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 4.5F, -0.2F, 0.28F, 0.0F, 0.0F));
        PartDefinition p_tail_28 = root.addOrReplaceChild("tail", CubeListBuilder.create()
                        .texOffs(74, 52).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 12.5F, 6.5F, -0.32F, 0.0F, 0.0F));
        PartDefinition p_tail_mid_29 = p_tail_28.addOrReplaceChild("tail_mid", CubeListBuilder.create()
                        .texOffs(93, 52).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 5.5F, 0.38F, 0.0F, 0.0F));
        p_tail_mid_29.addOrReplaceChild("tail_tip", CubeListBuilder.create()
                        .texOffs(0, 63).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 5.5F, 0.36F, 0.0F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(FamiliarRenderState state) {
        super.setupAnim(state);
        float age = state.ageInTicks;
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float idle = CreatureMotion.idleBlend(speed);
        float breathe = CreatureMotion.breathe(age, 0.09F);
        float alert = state.aggressive ? 1.0F : 0.0F;
        float wind = CreatureMotion.windUp(state.attackProgress);
        float strike = CreatureMotion.strike(state.attackProgress);

        this.head.yRot += CreatureMotion.clampLook(state.yRot * Mth.DEG_TO_RAD, 0.8F);
        this.head.xRot += CreatureMotion.clampLook(state.xRot * Mth.DEG_TO_RAD, 0.55F)
                - alert * 0.10F + breathe * 0.02F - wind * 0.18F - strike * 0.55F;
        this.head.zRot += CreatureMotion.wave(age, 0.035F, 0.4F) * 0.025F;

        this.jaw.xRot += alert * 0.10F + CreatureMotion.wave(age, 0.08F, 1.1F) * 0.03F + strike * 0.42F;

        this.leftEar.xRot += CreatureMotion.twitch(age, 0.09F, 0.8F, 0.16F) - alert * 0.12F;
        this.rightEar.xRot += CreatureMotion.twitch(age, 0.11F, 3.4F, 0.16F) - alert * 0.12F;
        this.leftEar.zRot += CreatureMotion.twitch(age, 0.07F, 1.9F, 0.08F);
        this.rightEar.zRot -= CreatureMotion.twitch(age, 0.08F, 5.0F, 0.08F);

        float gait0 = CreatureMotion.gait(walk, 0.0F, 0.98F, speed);
        float gait1 = CreatureMotion.gait(walk, Mth.PI, 0.98F, speed);
        this.legs[0].xRot += gait0 - strike * 0.40F;
        this.legs[1].xRot += gait1 - strike * 0.40F;
        this.legs[2].xRot += gait1;
        this.legs[3].xRot += gait0;

        this.body.y += breathe * 0.07F + Mth.cos(walk * 1.33F) * 0.06F * speed;
        this.body.xRot += -alert * 0.03F + Mth.sin(walk * 0.67F) * 0.02F * speed - strike * 0.08F;
        this.body.zRot += CreatureMotion.wave(age, 0.04F, 1.3F) * 0.015F * idle;
        this.body.z -= strike * 0.65F;

        this.mane.xRot += breathe * 0.03F + alert * 0.06F;
        this.armor.yScale = 1.0F + CreatureMotion.breathe(age, 0.12F) * 0.018F;
        float pulse = 1.0F + CreatureMotion.breathe(age, 0.22F) * (0.05F + state.stability * 0.05F);
        this.lens.xScale = pulse;
        this.lens.yScale = pulse;
        this.lens.zScale = pulse;

        this.tail.yRot += CreatureMotion.wave(age, 0.10F, 0.0F) * 0.20F;
        this.tail.xRot += breathe * 0.02F + alert * 0.12F + wind * 0.18F;
        this.tailMid.yRot += CreatureMotion.wave(age, 0.12F, 0.7F) * 0.24F;
        this.tailMid.xRot += CreatureMotion.wave(age, 0.14F, 1.2F) * 0.07F;
        this.tailTip.yRot += CreatureMotion.wave(age, 0.15F, 1.5F) * 0.28F;
        this.tailTip.xRot += CreatureMotion.wave(age, 0.17F, 2.1F) * 0.08F;

        if (state.sitting) {
            this.body.xRot += 0.22F;
            this.body.y += 1.4F;
            this.legs[0].xRot += -0.10F;
            this.legs[1].xRot += -0.10F;
            this.legs[2].xRot += -1.12F;
            this.legs[3].xRot += -1.12F;
            this.tail.xRot += 0.45F;
            this.tailMid.xRot += 0.16F;
        }
    }
}
