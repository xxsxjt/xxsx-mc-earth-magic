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
    private final ModelPart lens;
    private final ModelPart torso;
    private final ModelPart chestRune;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightPylon;
    private final ModelPart leftPylon;

    public RunicWatcherModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.head = root.getChild("head");
        this.lens = this.head.getChild("lens");
        this.torso = root.getChild("torso");
        this.chestRune = this.torso.getChild("chest_rune");
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
        CubeDeformation rim = new CubeDeformation(0.18F);

        PartDefinition head = root.addOrReplaceChild("head",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.5F, -8.0F, -4.5F, 9.0F, 8.0F, 9.0F)
                        .texOffs(32, 0).addBox(-4.5F, -8.0F, -4.5F, 9.0F, 8.0F, 9.0F, rim),
                PartPose.offset(0.0F, 5.0F, 0.0F));
        head.addOrReplaceChild("lens",
                CubeListBuilder.create().texOffs(0, 52).addBox(-2.0F, -2.0F, -0.8F, 4.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, -3.8F, -4.0F));

        PartDefinition torso = root.addOrReplaceChild("torso",
                CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-5.5F, 0.0F, -3.0F, 11.0F, 12.0F, 6.0F)
                        .texOffs(32, 16).addBox(-5.0F, 1.0F, -3.5F, 10.0F, 4.0F, 1.0F, rim),
                PartPose.offset(0.0F, 5.0F, 0.0F));
        torso.addOrReplaceChild("chest_rune",
                CubeListBuilder.create().texOffs(0, 52).addBox(-2.0F, -2.0F, -0.6F, 4.0F, 4.0F, 1.0F),
                PartPose.offset(0.0F, 6.0F, -3.1F));

        root.addOrReplaceChild("right_arm",
                CubeListBuilder.create().texOffs(0, 34).addBox(-4.0F, -2.0F, -2.5F, 5.0F, 13.0F, 5.0F),
                PartPose.offset(-6.0F, 7.0F, 0.0F));
        root.addOrReplaceChild("left_arm",
                CubeListBuilder.create().texOffs(20, 34).addBox(-1.0F, -2.0F, -2.5F, 5.0F, 13.0F, 5.0F),
                PartPose.offset(6.0F, 7.0F, 0.0F));
        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(40, 34).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 9.0F, 5.0F),
                PartPose.offset(-2.7F, 15.0F, 0.0F));
        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(40, 48).addBox(-2.5F, 0.0F, -2.5F, 5.0F, 9.0F, 5.0F),
                PartPose.offset(2.7F, 15.0F, 0.0F));

        root.addOrReplaceChild("right_pylon",
                CubeListBuilder.create().texOffs(10, 54).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 8.0F, 2.0F),
                PartPose.offsetAndRotation(-4.2F, 6.5F, 3.4F, -0.18F, 0.0F, -0.28F));
        root.addOrReplaceChild("left_pylon",
                CubeListBuilder.create().texOffs(18, 54).addBox(-1.0F, -6.0F, -1.0F, 2.0F, 8.0F, 2.0F),
                PartPose.offsetAndRotation(4.2F, 6.5F, 3.4F, -0.18F, 0.0F, 0.28F));

        return LayerDefinition.create(mesh, 64, 64);
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

        this.head.yRot = CreatureMotion.clampLook(state.yRot * Mth.DEG_TO_RAD, 0.85F);
        this.head.xRot = CreatureMotion.clampLook(state.xRot * Mth.DEG_TO_RAD, 0.5F)
                - alert * 0.06F + breathe * 0.012F - wind * 0.22F;
        this.head.zRot = CreatureMotion.wave(age, 0.03F, 0.3F) * 0.02F;

        this.torso.yRot = CreatureMotion.wave(age, 0.035F, 0.0F) * 0.04F
                + Mth.sin(walk * 0.67F) * 0.05F * speed;
        this.torso.xRot = -alert * 0.03F - strike * 0.12F;
        this.torso.zRot = CreatureMotion.wave(age, 0.028F, 1.4F) * 0.02F * idle;

        this.rightLeg.xRot = CreatureMotion.gait(walk, 0.0F, 1.05F, speed);
        this.leftLeg.xRot = CreatureMotion.gait(walk, Mth.PI, 1.05F, speed);
        this.rightLeg.zRot = -0.02F - Math.max(0.0F, this.rightLeg.xRot) * 0.03F;
        this.leftLeg.zRot = 0.02F + Math.max(0.0F, this.leftLeg.xRot) * 0.03F;

        float combatLift = alert * -0.48F;
        this.rightArm.xRot = CreatureMotion.gait(walk, Mth.PI, 0.7F, speed) + combatLift
                - wind * 1.25F + strike * 0.55F;
        this.leftArm.xRot = CreatureMotion.gait(walk, 0.0F, 0.7F, speed) + combatLift
                - wind * 1.05F + strike * 0.45F;
        this.rightArm.zRot = -0.08F - alert * 0.22F + CreatureMotion.wave(age, 0.05F, 0.2F) * 0.02F * idle;
        this.leftArm.zRot = 0.08F + alert * 0.22F - CreatureMotion.wave(age, 0.05F, 1.6F) * 0.02F * idle;

        float pylonSwing = CreatureMotion.wave(age, 0.095F, 0.0F) * 0.12F;
        this.rightPylon.zRot = -0.28F - pylonSwing - strike * 0.35F;
        this.leftPylon.zRot = 0.28F + pylonSwing + strike * 0.35F;
        this.rightPylon.yRot = CreatureMotion.wave(age, 0.06F, 0.8F) * 0.18F;
        this.leftPylon.yRot = -this.rightPylon.yRot;

        float pulse = 1.0F + CreatureMotion.breathe(age, 0.22F) * (0.06F + (1.0F - state.integrity) * 0.05F)
                + strike * 0.30F;
        this.lens.xScale = pulse;
        this.lens.yScale = pulse;
        this.lens.zScale = pulse;
        this.chestRune.xScale = 1.0F + CreatureMotion.breathe(age, 0.16F) * 0.10F;
        this.chestRune.yScale = this.chestRune.xScale;
        this.chestRune.zScale = this.chestRune.xScale;
        this.chestRune.yRot = age * 0.05F;
    }
}
