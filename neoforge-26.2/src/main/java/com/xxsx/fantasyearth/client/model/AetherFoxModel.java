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

public final class AetherFoxModel extends EntityModel<FamiliarRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(FantasyEarth.id("aether_fox"), "main");

    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart leftEarFin;
    private final ModelPart rightEarFin;
    private final ModelPart body;
    private final ModelPart leftWingFin;
    private final ModelPart rightWingFin;
    private final ModelPart[] legs;
    private final ModelPart centerTail;
    private final ModelPart centerTailMid;
    private final ModelPart centerTailTip;
    private final ModelPart leftTail;
    private final ModelPart rightTail;
    private final ModelPart leftTailMid;
    private final ModelPart rightTailMid;
    private final ModelPart leftTailTip;
    private final ModelPart rightTailTip;
    private final ModelPart outerLeftTail;
    private final ModelPart outerRightTail;

    public AetherFoxModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.head = root.getChild("head");
        this.jaw = this.head.getChild("jaw");
        this.leftEarFin = this.head.getChild("left_ear_fin");
        this.rightEarFin = this.head.getChild("right_ear_fin");
        this.body = root.getChild("body");
        this.leftWingFin = this.body.getChild("left_wing_fin");
        this.rightWingFin = this.body.getChild("right_wing_fin");
        this.legs = new ModelPart[]{
                root.getChild("front_right_leg"),
                root.getChild("front_left_leg"),
                root.getChild("back_right_leg"),
                root.getChild("back_left_leg")
        };
        this.centerTail = root.getChild("center_tail");
        this.centerTailMid = this.centerTail.getChild("mid");
        this.centerTailTip = this.centerTailMid.getChild("tip");
        this.leftTail = root.getChild("left_tail");
        this.rightTail = root.getChild("right_tail");
        this.leftTailMid = this.leftTail.getChild("mid");
        this.rightTailMid = this.rightTail.getChild("mid");
        this.leftTailTip = this.leftTailMid.getChild("tip");
        this.rightTailTip = this.rightTailMid.getChild("tip");
        this.outerLeftTail = root.getChild("outer_left_tail");
        this.outerRightTail = root.getChild("outer_right_tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_body_1 = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.5F, -3.5F, -5.5F, 7.0F, 7.0F, 6.0F)
                        .texOffs(27, 0).addBox(-3.0F, -3.0F, 0.5F, 6.0F, 6.0F, 6.0F)
                        .texOffs(52, 0).addBox(-4.0F, -2.5F, -6.0F, 8.0F, 5.0F, 3.0F, new CubeDeformation(0.06F)),
                PartPose.offsetAndRotation(0.0F, 14.5F, 0.0F, -0.05F, 0.0F, 0.0F));
        p_body_1.addOrReplaceChild("neck", CubeListBuilder.create()
                        .texOffs(75, 0).addBox(-2.5F, -2.5F, -2.0F, 5.0F, 5.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.5F, -5.0F, 0.38F, 0.0F, 0.0F));
        p_body_1.addOrReplaceChild("left_wing_fin", CubeListBuilder.create()
                        .texOffs(94, 0).addBox(0.0F, -4.0F, -0.5F, 1.0F, 4.0F, 5.0F)
                        .texOffs(107, 0).addBox(0.0F, -6.5F, 1.5F, 1.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(3.0F, -2.5F, -2.0F, -0.42F, 0.32F, 0.38F));
        p_body_1.addOrReplaceChild("right_wing_fin", CubeListBuilder.create()
                        .texOffs(0, 14).addBox(-1.0F, -4.0F, -0.5F, 1.0F, 4.0F, 5.0F)
                        .texOffs(13, 14).addBox(-1.0F, -6.5F, 1.5F, 1.0F, 3.0F, 4.0F),
                PartPose.offsetAndRotation(-3.0F, -2.5F, -2.0F, -0.42F, -0.32F, -0.38F));
        PartDefinition p_head_5 = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(24, 14).addBox(-3.5F, -3.0F, -3.5F, 7.0F, 6.0F, 5.0F)
                        .texOffs(49, 14).addBox(2.5F, -0.5F, -2.5F, 2.0F, 3.0F, 3.0F)
                        .texOffs(60, 14).addBox(-4.5F, -0.5F, -2.5F, 2.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 11.5F, -6.0F, 0.06F, 0.0F, 0.0F));
        p_head_5.addOrReplaceChild("muzzle", CubeListBuilder.create()
                        .texOffs(71, 14).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 2.0F, 2.0F)
                        .texOffs(84, 14).addBox(-1.0F, -0.5F, -4.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 0.5F, -3.5F, 0.08F, 0.0F, 0.0F));
        p_head_5.addOrReplaceChild("jaw", CubeListBuilder.create()
                        .texOffs(93, 14).addBox(-1.5F, 0.0F, -3.0F, 3.0F, 1.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, -3.0F, 0.06F, 0.0F, 0.0F));
        p_head_5.addOrReplaceChild("left_ear_fin", CubeListBuilder.create()
                        .texOffs(106, 14).addBox(-1.0F, -5.0F, -0.5F, 3.0F, 5.0F, 1.0F)
                        .texOffs(115, 14).addBox(-0.5F, -7.0F, -0.5F, 2.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(2.0F, -2.5F, -1.0F, -0.2F, -0.22F, 0.44F));
        p_head_5.addOrReplaceChild("right_ear_fin", CubeListBuilder.create()
                        .texOffs(0, 26).addBox(-2.0F, -5.0F, -0.5F, 3.0F, 5.0F, 1.0F)
                        .texOffs(9, 26).addBox(-1.5F, -7.0F, -0.5F, 2.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(-2.0F, -2.5F, -1.0F, -0.2F, 0.22F, -0.44F));
        p_head_5.addOrReplaceChild("forehead_gem", CubeListBuilder.create()
                        .texOffs(16, 26).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, -2.5F, -3.5F, -0.3F, 0.0F, 0.0F));
        PartDefinition p_front_right_leg_11 = root.addOrReplaceChild("front_right_leg", CubeListBuilder.create()
                        .texOffs(21, 26).addBox(-1.0F, -0.5F, -1.5F, 2.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(-2.2F, 15.5F, -3.8F, -0.14F, 0.0F, 0.0F));
        PartDefinition p_shin_12 = p_front_right_leg_11.addOrReplaceChild("shin", CubeListBuilder.create()
                        .texOffs(32, 26).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.26F, 0.0F, 0.0F));
        p_shin_12.addOrReplaceChild("paw", CubeListBuilder.create()
                        .texOffs(41, 26).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, -0.12F, 0.0F, 0.0F));
        PartDefinition p_front_left_leg_14 = root.addOrReplaceChild("front_left_leg", CubeListBuilder.create()
                        .texOffs(52, 26).addBox(-1.0F, -0.5F, -1.5F, 2.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(2.2F, 15.5F, -3.8F, -0.14F, 0.0F, 0.0F));
        PartDefinition p_shin_15 = p_front_left_leg_14.addOrReplaceChild("shin", CubeListBuilder.create()
                        .texOffs(63, 26).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.26F, 0.0F, 0.0F));
        p_shin_15.addOrReplaceChild("paw", CubeListBuilder.create()
                        .texOffs(72, 26).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, -0.12F, 0.0F, 0.0F));
        PartDefinition p_back_right_leg_17 = root.addOrReplaceChild("back_right_leg", CubeListBuilder.create()
                        .texOffs(83, 26).addBox(-1.0F, -0.5F, -1.5F, 2.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(-2.2F, 15.5F, 4.2F, 0.22F, 0.0F, 0.0F));
        PartDefinition p_shin_18 = p_back_right_leg_17.addOrReplaceChild("shin", CubeListBuilder.create()
                        .texOffs(94, 26).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.42F, 0.0F, 0.0F));
        p_shin_18.addOrReplaceChild("paw", CubeListBuilder.create()
                        .texOffs(103, 26).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, 0.2F, 0.0F, 0.0F));
        PartDefinition p_back_left_leg_20 = root.addOrReplaceChild("back_left_leg", CubeListBuilder.create()
                        .texOffs(114, 26).addBox(-1.0F, -0.5F, -1.5F, 2.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(2.2F, 15.5F, 4.2F, 0.22F, 0.0F, 0.0F));
        PartDefinition p_shin_21 = p_back_left_leg_20.addOrReplaceChild("shin", CubeListBuilder.create()
                        .texOffs(0, 35).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, -0.42F, 0.0F, 0.0F));
        p_shin_21.addOrReplaceChild("paw", CubeListBuilder.create()
                        .texOffs(9, 35).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 3.5F, 0.0F, 0.2F, 0.0F, 0.0F));
        PartDefinition p_center_tail_23 = root.addOrReplaceChild("center_tail", CubeListBuilder.create()
                        .texOffs(20, 35).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 13.5F, 6.0F, 0.42F, 0.0F, 0.0F));
        PartDefinition p_mid_24 = p_center_tail_23.addOrReplaceChild("mid", CubeListBuilder.create()
                        .texOffs(39, 35).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 5.5F, 0.24F, 0.0F, 0.0F));
        p_mid_24.addOrReplaceChild("tip", CubeListBuilder.create()
                        .texOffs(60, 35).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 5.5F, 0.18F, 0.0F, 0.0F));
        PartDefinition p_left_tail_26 = root.addOrReplaceChild("left_tail", CubeListBuilder.create()
                        .texOffs(77, 35).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(1.5F, 13.5F, 6.0F, 0.28F, 0.42F, 0.14F));
        PartDefinition p_mid_27 = p_left_tail_26.addOrReplaceChild("mid", CubeListBuilder.create()
                        .texOffs(96, 35).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 5.5F, 0.2F, 0.18F, 0.0F));
        p_mid_27.addOrReplaceChild("tip", CubeListBuilder.create()
                        .texOffs(0, 46).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 5.5F, 0.18F, 0.108F, 0.0F));
        PartDefinition p_right_tail_29 = root.addOrReplaceChild("right_tail", CubeListBuilder.create()
                        .texOffs(17, 46).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(-1.5F, 13.5F, 6.0F, 0.28F, -0.42F, -0.14F));
        PartDefinition p_mid_30 = p_right_tail_29.addOrReplaceChild("mid", CubeListBuilder.create()
                        .texOffs(36, 46).addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 5.5F, 0.2F, -0.18F, 0.0F));
        p_mid_30.addOrReplaceChild("tip", CubeListBuilder.create()
                        .texOffs(57, 46).addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 5.5F, 0.18F, -0.108F, 0.0F));
        root.addOrReplaceChild("outer_left_tail", CubeListBuilder.create()
                        .texOffs(74, 46).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 7.0F),
                PartPose.offsetAndRotation(2.0F, 14.0F, 5.5F, 0.14F, 0.78F, 0.26F));
        root.addOrReplaceChild("outer_right_tail", CubeListBuilder.create()
                        .texOffs(93, 46).addBox(-1.0F, -1.5F, 0.0F, 2.0F, 3.0F, 7.0F),
                PartPose.offsetAndRotation(-2.0F, 14.0F, 5.5F, 0.14F, -0.78F, -0.26F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(FamiliarRenderState state) {
        super.setupAnim(state);
        float age = state.ageInTicks;
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float idle = CreatureMotion.idleBlend(speed);
        float breathe = CreatureMotion.breathe(age, 0.11F);
        float alert = state.aggressive ? 1.0F : 0.0F;
        float wind = CreatureMotion.windUp(state.attackProgress);
        float strike = CreatureMotion.strike(state.attackProgress);

        this.head.yRot += CreatureMotion.clampLook(state.yRot * Mth.DEG_TO_RAD, 0.85F);
        this.head.xRot += CreatureMotion.clampLook(state.xRot * Mth.DEG_TO_RAD, 0.5F)
                + breathe * 0.02F - wind * 0.12F - strike * 0.42F;
        this.head.zRot += CreatureMotion.wave(age, 0.045F, 0.0F) * 0.03F + alert * 0.04F;
        this.jaw.xRot += alert * 0.08F + CreatureMotion.wave(age, 0.09F, 0.9F) * 0.025F + strike * 0.36F;

        float earFlickLeft = CreatureMotion.twitch(age, 0.13F, 1.7F, 0.18F);
        float earFlickRight = CreatureMotion.twitch(age, 0.10F, 4.3F, 0.18F);
        this.leftEarFin.zRot += earFlickLeft + alert * 0.06F;
        this.rightEarFin.zRot -= earFlickRight + alert * 0.06F;
        this.leftEarFin.xRot += -0.10F * alert - CreatureMotion.twitch(age, 0.07F, 0.6F, 0.10F);
        this.rightEarFin.xRot += -0.10F * alert - CreatureMotion.twitch(age, 0.08F, 2.2F, 0.10F);

        float wingFlutter = CreatureMotion.wave(age, 0.12F, 0.5F) * 0.12F + alert * 0.14F;
        this.leftWingFin.zRot -= wingFlutter;
        this.rightWingFin.zRot += wingFlutter;

        float gait0 = CreatureMotion.gait(walk, 0.0F, 0.95F, speed);
        float gait1 = CreatureMotion.gait(walk, Mth.PI, 0.95F, speed);
        this.legs[0].xRot += gait0 - strike * 0.34F;
        this.legs[1].xRot += gait1 - strike * 0.34F;
        this.legs[2].xRot += gait1;
        this.legs[3].xRot += gait0;

        this.body.y += breathe * 0.06F + Mth.cos(walk * 1.33F) * 0.05F * speed;
        this.body.xRot += -alert * 0.02F + Mth.sin(walk * 0.67F) * 0.015F * speed - strike * 0.10F;
        this.body.zRot += CreatureMotion.wave(age, 0.05F, 1.1F) * 0.012F * idle;
        this.body.z -= strike * 0.55F;

        float tailWave = CreatureMotion.wave(age, 0.10F, 0.0F) * (0.18F + state.stability * 0.14F);
        this.centerTail.yRot += tailWave;
        this.centerTailMid.yRot += tailWave * 0.65F;
        this.centerTailTip.yRot += tailWave * 0.85F;
        this.centerTail.xRot += breathe * 0.03F + wind * 0.18F;

        this.leftTail.yRot += tailWave * 0.9F;
        this.rightTail.yRot += tailWave * 0.9F;
        this.leftTailMid.yRot += tailWave * 0.6F;
        this.rightTailMid.yRot += tailWave * 0.6F;
        this.leftTailTip.yRot += tailWave * 0.75F;
        this.rightTailTip.yRot += tailWave * 0.75F;

        this.outerLeftTail.yRot += tailWave * 0.7F;
        this.outerRightTail.yRot += tailWave * 0.7F;

        if (state.sitting) {
            this.body.xRot += 0.24F;
            this.body.y += 1.2F;
            this.legs[0].xRot += -0.08F;
            this.legs[1].xRot += -0.08F;
            this.legs[2].xRot += -1.15F;
            this.legs[3].xRot += -1.15F;
            this.centerTail.xRot += 0.36F;
            this.leftTail.xRot += 0.34F;
            this.rightTail.xRot += 0.34F;
        }
    }
}
