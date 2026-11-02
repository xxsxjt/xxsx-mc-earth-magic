package com.xxsx.fantasyearth.client.model;

import com.xxsx.fantasyearth.FantasyEarth;
import com.xxsx.fantasyearth.client.CreatureMotion;
import com.xxsx.fantasyearth.client.renderer.RunicWatcherRenderState;
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

public final class RunicWatcherModel extends EntityModel<RunicWatcherRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            FantasyEarth.id("runic_watcher"), "main");

    private final ModelPart head;
    private final ModelPart halo;
    private final ModelPart lens;
    private final ModelPart torso;
    private final ModelPart chestRune;
    private final ModelPart shoulderWingL;
    private final ModelPart shoulderWingR;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightPylon;
    private final ModelPart leftPylon;

    public RunicWatcherModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.head = root.getChild("head");
        this.halo = this.head.getChild("halo");
        this.lens = this.head.getChild("lens");
        this.torso = root.getChild("torso");
        this.chestRune = this.torso.getChild("chest_rune");
        this.shoulderWingL = this.torso.getChild("shoulder_wing_l");
        this.shoulderWingR = this.torso.getChild("shoulder_wing_r");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.rightPylon = root.getChild("right_pylon");
        this.leftPylon = root.getChild("left_pylon");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_head_1 = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F)
                        .texOffs(29, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.08F)),
                PartPose.offset(0.0F, 4.5F, 0.0F));
        p_head_1.addOrReplaceChild("halo", CubeListBuilder.create()
                        .texOffs(62, 0).addBox(-5.0F, -0.5F, -5.0F, 10.0F, 1.0F, 2.0F)
                        .texOffs(87, 0).addBox(-5.0F, -0.5F, 3.0F, 10.0F, 1.0F, 2.0F)
                        .texOffs(0, 15).addBox(-5.0F, -0.5F, -3.0F, 2.0F, 1.0F, 6.0F)
                        .texOffs(17, 15).addBox(3.0F, -0.5F, -3.0F, 2.0F, 1.0F, 6.0F)
                        .texOffs(34, 15).addBox(-1.0F, -3.5F, -5.0F, 2.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, -8.8F, 0.0F, 0.26F, 0.0F, -0.22F));
        p_head_1.addOrReplaceChild("lens", CubeListBuilder.create()
                        .texOffs(41, 15).addBox(-2.0F, -1.5F, -0.5F, 4.0F, 3.0F, 1.0F),
                PartPose.offset(0.0F, -3.5F, -3.8F));
        p_head_1.addOrReplaceChild("crest_left", CubeListBuilder.create()
                        .texOffs(52, 15).addBox(0.0F, -4.0F, -1.0F, 1.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(3.8F, -5.5F, -1.0F, -0.22F, 0.14F, 0.42F));
        p_head_1.addOrReplaceChild("crest_right", CubeListBuilder.create()
                        .texOffs(61, 15).addBox(-1.0F, -4.0F, -1.0F, 1.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(-3.8F, -5.5F, -1.0F, -0.22F, -0.14F, -0.42F));
        PartDefinition p_torso_6 = root.addOrReplaceChild("torso", CubeListBuilder.create()
                        .texOffs(70, 15).addBox(-5.5F, 0.0F, -3.0F, 11.0F, 7.0F, 6.0F)
                        .texOffs(0, 29).addBox(-5.0F, 0.5F, -3.5F, 10.0F, 4.0F, 7.0F, new CubeDeformation(0.06F))
                        .texOffs(35, 29).addBox(-3.0F, 7.0F, -2.0F, 6.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 5.0F, 0.0F));
        p_torso_6.addOrReplaceChild("chest_rune", CubeListBuilder.create()
                        .texOffs(56, 29).addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 1.0F)
                        .texOffs(69, 29).addBox(-1.5F, -1.5F, -1.0F, 3.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 4.0F, -3.2F, 0.0F, 0.0F, 0.7854F));
        p_torso_6.addOrReplaceChild("shoulder_wing_l", CubeListBuilder.create()
                        .texOffs(80, 29).addBox(0.0F, -2.5F, -3.5F, 6.0F, 3.0F, 7.0F)
                        .texOffs(107, 29).addBox(4.0F, 0.5F, -2.5F, 2.0F, 6.0F, 5.0F),
                PartPose.offsetAndRotation(5.5F, 0.5F, 0.0F, 0.08F, -0.12F, -0.34F));
        p_torso_6.addOrReplaceChild("shoulder_wing_r", CubeListBuilder.create()
                        .texOffs(0, 41).addBox(-6.0F, -2.5F, -3.5F, 6.0F, 3.0F, 7.0F)
                        .texOffs(27, 41).addBox(-6.0F, 0.5F, -2.5F, 2.0F, 6.0F, 5.0F),
                PartPose.offsetAndRotation(-5.5F, 0.5F, 0.0F, 0.08F, 0.12F, 0.34F));
        PartDefinition p_right_arm_10 = root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(42, 41).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F)
                        .texOffs(55, 41).addBox(-1.0F, 4.5F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(-7.5F, 6.5F, 0.0F, -0.12F, 0.0F, 0.14F));
        PartDefinition p_forearm_11 = p_right_arm_10.addOrReplaceChild("forearm", CubeListBuilder.create()
                        .texOffs(64, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F)
                        .texOffs(81, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, -0.26F, 0.0F, -0.084F));
        p_forearm_11.addOrReplaceChild("claw_inner", CubeListBuilder.create()
                        .texOffs(98, 41).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(1.2F, 5.8F, -0.5F, 0.18F, 0.0F, -0.28F));
        p_forearm_11.addOrReplaceChild("claw_outer", CubeListBuilder.create()
                        .texOffs(105, 41).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(-1.2F, 5.8F, -0.5F, 0.18F, 0.0F, 0.24F));
        PartDefinition p_left_arm_14 = root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(112, 41).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 5.0F, 3.0F)
                        .texOffs(0, 53).addBox(-1.0F, 4.5F, -1.0F, 2.0F, 2.0F, 2.0F),
                PartPose.offsetAndRotation(7.5F, 6.5F, 0.0F, -0.12F, 0.0F, -0.14F));
        PartDefinition p_forearm_15 = p_left_arm_14.addOrReplaceChild("forearm", CubeListBuilder.create()
                        .texOffs(9, 53).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F)
                        .texOffs(26, 53).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.1F)),
                PartPose.offsetAndRotation(0.0F, 6.0F, 0.0F, -0.26F, 0.0F, 0.084F));
        p_forearm_15.addOrReplaceChild("claw_inner", CubeListBuilder.create()
                        .texOffs(43, 53).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(-1.2F, 5.8F, -0.5F, 0.18F, 0.0F, 0.28F));
        p_forearm_15.addOrReplaceChild("claw_outer", CubeListBuilder.create()
                        .texOffs(50, 53).addBox(-0.5F, 0.0F, -1.0F, 1.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(1.2F, 5.8F, -0.5F, 0.18F, 0.0F, -0.24F));
        PartDefinition p_right_leg_18 = root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(57, 53).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F)
                        .texOffs(70, 53).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.08F)),
                PartPose.offsetAndRotation(-3.2F, 14.5F, -1.5F, -0.16F, 0.22F, 0.2F));
        p_right_leg_18.addOrReplaceChild("rear_shard", CubeListBuilder.create()
                        .texOffs(83, 53).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
                PartPose.offsetAndRotation(0.5F, 0.5F, 3.5F, 0.32F, -0.25F, 0.14F));
        PartDefinition p_left_leg_20 = root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(92, 53).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 8.0F, 3.0F)
                        .texOffs(105, 53).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.08F)),
                PartPose.offsetAndRotation(3.2F, 14.5F, -1.5F, -0.16F, -0.22F, -0.2F));
        p_left_leg_20.addOrReplaceChild("rear_shard", CubeListBuilder.create()
                        .texOffs(118, 53).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F),
                PartPose.offsetAndRotation(-0.5F, 0.5F, 3.5F, 0.32F, 0.25F, -0.14F));
        PartDefinition p_right_pylon_22 = root.addOrReplaceChild("right_pylon", CubeListBuilder.create()
                        .texOffs(0, 65).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 9.0F, 2.0F),
                PartPose.offsetAndRotation(-5.2F, 5.5F, 3.8F, -0.28F, -0.18F, -0.38F));
        p_right_pylon_22.addOrReplaceChild("tip", CubeListBuilder.create()
                        .texOffs(9, 65).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
        PartDefinition p_left_pylon_24 = root.addOrReplaceChild("left_pylon", CubeListBuilder.create()
                        .texOffs(18, 65).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 9.0F, 2.0F),
                PartPose.offsetAndRotation(5.2F, 5.5F, 3.8F, -0.28F, 0.18F, 0.38F));
        p_left_pylon_24.addOrReplaceChild("tip", CubeListBuilder.create()
                        .texOffs(27, 65).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, -7.0F, 0.0F, 0.0F, 0.7854F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(RunicWatcherRenderState state) {
        super.setupAnim(state);
        float age = state.ageInTicks;
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float idle = CreatureMotion.idleBlend(speed);
        float breathe = CreatureMotion.breathe(age, 0.06F);
        float alert = state.aggressive ? 1.0F : 0.0F;
        float wind = CreatureMotion.windUp(state.attackProgress);
        float strike = CreatureMotion.strike(state.attackProgress);

        float hover = breathe * 0.45F;
        this.head.y += hover;
        this.torso.y += hover;
        this.rightArm.y += hover;
        this.leftArm.y += hover;

        this.head.yRot += CreatureMotion.clampLook(state.yRot * Mth.DEG_TO_RAD, 0.85F);
        this.head.xRot += CreatureMotion.clampLook(state.xRot * Mth.DEG_TO_RAD, 0.5F)
                - alert * 0.06F + breathe * 0.015F - wind * 0.22F;
        this.head.zRot += CreatureMotion.wave(age, 0.03F, 0.3F) * 0.02F;

        this.halo.yRot += age * 0.055F;
        this.halo.xRot += CreatureMotion.wave(age, 0.05F, 0.7F) * 0.12F;
        this.halo.zRot += CreatureMotion.wave(age, 0.04F, 1.8F) * 0.12F;

        this.torso.yRot += CreatureMotion.wave(age, 0.035F, 0.0F) * 0.04F + Mth.sin(walk * 0.67F) * 0.05F * speed;
        this.torso.xRot += -alert * 0.03F - strike * 0.12F;
        this.torso.zRot += CreatureMotion.wave(age, 0.028F, 1.4F) * 0.02F * idle;

        this.shoulderWingL.zRot -= breathe * 0.08F + alert * 0.12F;
        this.shoulderWingR.zRot += breathe * 0.08F + alert * 0.12F;

        // Orbiting/hovering lower monolith pillars
        this.rightLeg.y += CreatureMotion.wave(age, 0.09F, 0.0F) * 0.35F;
        this.leftLeg.y += CreatureMotion.wave(age, 0.09F, Mth.PI) * 0.35F;
        this.rightLeg.xRot += CreatureMotion.gait(walk, 0.0F, 0.65F, speed);
        this.leftLeg.xRot += CreatureMotion.gait(walk, Mth.PI, 0.65F, speed);

        float combatLift = alert * -0.45F;
        this.rightArm.xRot += CreatureMotion.gait(walk, Mth.PI, 0.65F, speed) + combatLift - wind * 1.15F + strike * 0.55F;
        this.leftArm.xRot += CreatureMotion.gait(walk, 0.0F, 0.65F, speed) + combatLift - wind * 0.95F + strike * 0.45F;
        this.rightArm.zRot += -alert * 0.18F + CreatureMotion.wave(age, 0.05F, 0.2F) * 0.03F * idle;
        this.leftArm.zRot += alert * 0.18F - CreatureMotion.wave(age, 0.05F, 1.6F) * 0.03F * idle;

        float pylonSwing = CreatureMotion.wave(age, 0.095F, 0.0F) * 0.12F;
        this.rightPylon.zRot += -pylonSwing - strike * 0.32F;
        this.leftPylon.zRot += pylonSwing + strike * 0.32F;
        this.rightPylon.y += hover * 1.2F;
        this.leftPylon.y += hover * 1.2F;

        float pulse = 1.0F + CreatureMotion.breathe(age, 0.22F) * (0.06F + (1.0F - state.integrity) * 0.05F)
                + strike * 0.28F;
        this.lens.xScale = pulse;
        this.lens.yScale = pulse;
        this.lens.zScale = pulse;
        this.chestRune.zRot += age * 0.045F;
        float runeScale = 1.0F + CreatureMotion.breathe(age, 0.16F) * 0.10F;
        this.chestRune.xScale = runeScale;
        this.chestRune.yScale = runeScale;
    }
}
