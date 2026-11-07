package com.xxsx.fantasyearth.client.model;

import com.xxsx.fantasyearth.FantasyEarth;
import com.xxsx.fantasyearth.client.CreatureMotion;
import com.xxsx.fantasyearth.client.renderer.ArcaneSettlerRenderState;
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

public final class ArcaneSettlerModel extends EntityModel<ArcaneSettlerRenderState> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            FantasyEarth.id("arcane_settler"), "main");

    private final ModelPart head;
    private final ModelPart witchHat;
    private final ModelPart witchHatTip;
    private final ModelPart goblinEars;
    private final ModelPart lens;
    private final ModelPart body;
    private final ModelPart grimoire;
    private final ModelPart waistVial;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart witchPack;
    private final ModelPart goblinPack;
    private final ModelPart researcherPack;
    private final ModelPart coatSkirt;

    public ArcaneSettlerModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        this.head = root.getChild("head");
        this.witchHat = this.head.getChild("witch_hat");
        this.witchHatTip = this.witchHat.getChild("crown").getChild("tip");
        this.goblinEars = this.head.getChild("goblin_ears");
        this.lens = this.head.getChild("lens");
        this.body = root.getChild("body");
        this.grimoire = this.body.getChild("grimoire");
        this.waistVial = this.body.getChild("waist_vial");
        this.rightArm = root.getChild("right_arm");
        this.leftArm = root.getChild("left_arm");
        this.rightLeg = root.getChild("right_leg");
        this.leftLeg = root.getChild("left_leg");
        this.witchPack = root.getChild("witch_pack");
        this.goblinPack = root.getChild("goblin_pack");
        this.researcherPack = root.getChild("researcher_pack");
        this.coatSkirt = this.body.getChild("coat_skirt");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_head_1 = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F),
                PartPose.ZERO);
        p_head_1.addOrReplaceChild("hair_cap", CubeListBuilder.create()
                        .texOffs(33, 0).addBox(-4.5F, -8.5F, -4.5F, 9.0F, 3.0F, 9.0F, new CubeDeformation(0.05F))
                        .texOffs(70, 0).addBox(-4.0F, -6.0F, 3.2F, 8.0F, 7.0F, 2.0F),
                PartPose.ZERO);
        PartDefinition p_witch_hat_3 = p_head_1.addOrReplaceChild("witch_hat", CubeListBuilder.create()
                        .texOffs(0, 17).addBox(-6.0F, -1.0F, -6.0F, 12.0F, 1.0F, 12.0F)
                        .texOffs(49, 17).addBox(-4.5F, -2.0F, -4.5F, 9.0F, 1.0F, 9.0F, new CubeDeformation(0.05F)),
                PartPose.offsetAndRotation(0.0F, -8.0F, 0.0F, -0.08F, 0.0F, 0.04F));
        PartDefinition p_crown_4 = p_witch_hat_3.addOrReplaceChild("crown", CubeListBuilder.create()
                        .texOffs(86, 17).addBox(-3.5F, -4.0F, -3.5F, 7.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, -2.0F, 0.0F, -0.18F, 0.0F, 0.08F));
        p_crown_4.addOrReplaceChild("tip", CubeListBuilder.create()
                        .texOffs(0, 31).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 4.0F, 4.0F)
                        .texOffs(17, 31).addBox(-0.5F, -5.0F, -1.5F, 1.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, -3.8F, 0.0F, -0.38F, 0.0F, 0.22F));
        PartDefinition p_goblin_ears_6 = p_head_1.addOrReplaceChild("goblin_ears", CubeListBuilder.create(),
                PartPose.offset(0.0F, -4.5F, -0.5F));
        p_goblin_ears_6.addOrReplaceChild("ear_l", CubeListBuilder.create()
                        .texOffs(22, 31).addBox(0.0F, -1.5F, -0.5F, 4.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(4.0F, 0.0F, 0.0F, 0.0F, -0.22F, -0.28F));
        p_goblin_ears_6.addOrReplaceChild("ear_r", CubeListBuilder.create()
                        .texOffs(33, 31).addBox(-4.0F, -1.5F, -0.5F, 4.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(-4.0F, 0.0F, 0.0F, 0.0F, 0.22F, 0.28F));
        p_head_1.addOrReplaceChild("lens", CubeListBuilder.create()
                        .texOffs(44, 31).addBox(-1.5F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(2.2F, -4.2F, -4.2F, 0.0F, -0.08F, 0.0F));
        PartDefinition p_body_10 = root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(53, 31).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F),
                PartPose.ZERO);
        p_body_10.addOrReplaceChild("coat", CubeListBuilder.create()
                        .texOffs(78, 31).addBox(-4.5F, -0.2F, -2.5F, 9.0F, 12.0F, 5.0F, new CubeDeformation(0.04F)),
                PartPose.ZERO);
        p_body_10.addOrReplaceChild("mantle_left", CubeListBuilder.create()
                        .texOffs(0, 49).addBox(-0.5F, -1.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.08F)),
                PartPose.offsetAndRotation(3.8F, 0.5F, 0.0F, 0.06F, 0.0F, 0.26F));
        p_body_10.addOrReplaceChild("mantle_right", CubeListBuilder.create()
                        .texOffs(23, 49).addBox(-4.5F, -1.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.08F)),
                PartPose.offsetAndRotation(-3.8F, 0.5F, 0.0F, 0.06F, 0.0F, -0.26F));
        p_body_10.addOrReplaceChild("grimoire", CubeListBuilder.create()
                        .texOffs(46, 49).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 5.0F, 4.0F)
                        .texOffs(59, 49).addBox(-1.2F, 1.5F, -0.5F, 1.0F, 2.0F, 1.0F),
                PartPose.offsetAndRotation(-4.6F, 9.2F, -0.5F, 0.14F, 0.12F, 0.24F));
        p_body_10.addOrReplaceChild("waist_vial", CubeListBuilder.create()
                        .texOffs(64, 49).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(4.5F, 9.2F, -1.2F, -0.16F, -0.14F, -0.22F));
        PartDefinition p_coat_skirt_16 = p_body_10.addOrReplaceChild("coat_skirt", CubeListBuilder.create()
                        .texOffs(73, 49).addBox(-3.5F, 0.0F, -2.6F, 7.0F, 8.0F, 1.0F)
                        .texOffs(90, 49).addBox(-4.5F, 0.0F, 1.6F, 9.0F, 9.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 10.5F, 0.0F, 0.04F, 0.0F, 0.0F));
        p_coat_skirt_16.addOrReplaceChild("skirt_left", CubeListBuilder.create()
                        .texOffs(111, 49).addBox(-1.0F, 0.0F, -2.5F, 2.0F, 9.0F, 5.0F),
                PartPose.offsetAndRotation(4.2F, 0.0F, 0.0F, 0.0F, 0.0F, -0.18F));
        p_coat_skirt_16.addOrReplaceChild("skirt_right", CubeListBuilder.create()
                        .texOffs(0, 64).addBox(-1.0F, 0.0F, -2.5F, 2.0F, 9.0F, 5.0F),
                PartPose.offsetAndRotation(-4.2F, 0.0F, 0.0F, 0.0F, 0.0F, 0.18F));
        PartDefinition p_right_arm_19 = root.addOrReplaceChild("right_arm", CubeListBuilder.create()
                        .texOffs(15, 64).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offsetAndRotation(-5.0F, 2.0F, 0.0F, -0.06F, 0.0F, 0.08F));
        p_right_arm_19.addOrReplaceChild("right_sleeve", CubeListBuilder.create()
                        .texOffs(32, 64).addBox(-3.5F, -2.0F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.04F)),
                PartPose.ZERO);
        p_right_arm_19.addOrReplaceChild("right_cuff", CubeListBuilder.create()
                        .texOffs(53, 64).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 5.0F, 6.0F),
                PartPose.offsetAndRotation(-1.5F, 4.8F, 0.2F, 0.14F, 0.0F, 0.12F));
        PartDefinition p_left_arm_22 = root.addOrReplaceChild("left_arm", CubeListBuilder.create()
                        .texOffs(76, 64).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offsetAndRotation(5.0F, 2.0F, 0.0F, -0.06F, 0.0F, -0.08F));
        p_left_arm_22.addOrReplaceChild("left_sleeve", CubeListBuilder.create()
                        .texOffs(93, 64).addBox(-1.5F, -2.0F, -2.5F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.04F)),
                PartPose.ZERO);
        p_left_arm_22.addOrReplaceChild("left_cuff", CubeListBuilder.create()
                        .texOffs(0, 81).addBox(-2.5F, 0.0F, -3.0F, 5.0F, 5.0F, 6.0F),
                PartPose.offsetAndRotation(1.5F, 4.8F, 0.2F, 0.14F, 0.0F, -0.12F));
        PartDefinition p_right_leg_25 = root.addOrReplaceChild("right_leg", CubeListBuilder.create()
                        .texOffs(23, 81).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(-2.0F, 12.0F, 0.0F));
        p_right_leg_25.addOrReplaceChild("right_boot", CubeListBuilder.create()
                        .texOffs(40, 81).addBox(-2.5F, 0.0F, -2.8F, 5.0F, 5.0F, 5.0F),
                PartPose.offset(0.0F, 7.0F, 0.0F));
        PartDefinition p_left_leg_27 = root.addOrReplaceChild("left_leg", CubeListBuilder.create()
                        .texOffs(61, 81).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F),
                PartPose.offset(2.0F, 12.0F, 0.0F));
        p_left_leg_27.addOrReplaceChild("left_boot", CubeListBuilder.create()
                        .texOffs(78, 81).addBox(-2.5F, 0.0F, -2.8F, 5.0F, 5.0F, 5.0F),
                PartPose.offset(0.0F, 7.0F, 0.0F));
        PartDefinition p_witch_pack_29 = root.addOrReplaceChild("witch_pack", CubeListBuilder.create()
                        .texOffs(99, 81).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 10.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, 2.5F, 0.06F, 0.0F, 0.0F));
        p_witch_pack_29.addOrReplaceChild("herb_bottle", CubeListBuilder.create()
                        .texOffs(0, 98).addBox(-1.5F, -2.0F, -1.5F, 3.0F, 5.0F, 3.0F),
                PartPose.offsetAndRotation(4.2F, 4.5F, 2.0F, 0.0F, 0.0F, -0.22F));
        PartDefinition p_goblin_pack_31 = root.addOrReplaceChild("goblin_pack", CubeListBuilder.create()
                        .texOffs(13, 98).addBox(-4.5F, 0.0F, 0.0F, 9.0F, 10.0F, 5.0F),
                PartPose.offsetAndRotation(0.0F, 2.5F, 2.5F, 0.08F, 0.0F, 0.0F));
        p_goblin_pack_31.addOrReplaceChild("tool_roll", CubeListBuilder.create()
                        .texOffs(42, 98).addBox(-5.0F, -1.5F, -1.5F, 10.0F, 3.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -1.5F, 2.5F, 0.0F, 0.0F, 0.06F));
        PartDefinition p_researcher_pack_33 = root.addOrReplaceChild("researcher_pack", CubeListBuilder.create()
                        .texOffs(69, 98).addBox(-4.0F, 0.0F, 0.0F, 8.0F, 11.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, 2.5F, 0.05F, 0.0F, 0.0F));
        p_researcher_pack_33.addOrReplaceChild("scroll_tube", CubeListBuilder.create()
                        .texOffs(94, 98).addBox(-1.5F, -6.0F, -1.5F, 3.0F, 12.0F, 3.0F),
                PartPose.offsetAndRotation(4.0F, 4.0F, 2.0F, 0.0F, 0.0F, -0.26F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(ArcaneSettlerRenderState state) {
        super.setupAnim(state);
        float age = state.ageInTicks;
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float idle = CreatureMotion.idleBlend(speed);
        float breathe = CreatureMotion.breathe(age, 0.08F);

        this.head.yRot += CreatureMotion.clampLook(state.yRot * Mth.DEG_TO_RAD, 0.9F);
        this.head.xRot += CreatureMotion.clampLook(state.xRot * Mth.DEG_TO_RAD, 0.6F) + breathe * 0.015F;
        this.head.zRot += CreatureMotion.wave(age, 0.035F, 0.8F) * 0.02F;

        this.rightLeg.xRot += CreatureMotion.gait(walk, 0.0F, 1.0F, speed);
        this.leftLeg.xRot += CreatureMotion.gait(walk, Mth.PI, 1.0F, speed);
        this.rightLeg.zRot += -0.02F - Math.max(0.0F, this.rightLeg.xRot) * 0.03F;
        this.leftLeg.zRot += 0.02F + Math.max(0.0F, this.leftLeg.xRot) * 0.03F;

        this.body.y += breathe * 0.04F;
        this.body.zRot += CreatureMotion.wave(age, 0.03F, 1.3F) * 0.012F * idle;
        this.body.yScale = 1.0F + breathe * 0.008F;
        this.body.xRot += Mth.sin(walk * 0.67F) * 0.01F * speed;

        this.coatSkirt.xRot += Mth.sin(walk * 0.67F) * 0.05F * speed
                + CreatureMotion.wave(age, 0.05F, 2.0F) * 0.012F * idle;
        this.coatSkirt.zRot += CreatureMotion.wave(age, 0.04F, 0.5F) * 0.014F * idle;

        this.grimoire.zRot += CreatureMotion.wave(age, 0.06F, 0.3F) * 0.04F;
        this.waistVial.zRot -= CreatureMotion.wave(age, 0.07F, 1.4F) * 0.05F;

        float wind = CreatureMotion.windUp(state.attackProgress);
        float strike = CreatureMotion.strike(state.attackProgress);
        if (state.attackProgress > 0.001F || state.casting) {
            float castLift = state.casting ? -0.58F : -0.25F;
            float chantWave = CreatureMotion.wave(age, 0.22F, 0.0F) * (state.casting ? 0.12F : 0.0F);
            this.rightArm.xRot += castLift - wind * 1.18F + strike * 0.52F + chantWave;
            this.leftArm.xRot += castLift * 0.85F - wind * 0.92F + strike * 0.42F - chantWave;
            this.rightArm.yRot += -0.22F + strike * 0.18F;
            this.leftArm.yRot += 0.22F - strike * 0.18F;
            this.rightArm.zRot += 0.18F + strike * 0.22F;
            this.leftArm.zRot += -0.18F - strike * 0.22F;
            this.body.xRot -= strike * 0.08F;
            this.head.xRot -= wind * 0.15F - strike * 0.08F;
        } else if (state.trading) {
            float offerWave = CreatureMotion.wave(age, 0.09F, 0.0F) * 0.08F;
            this.rightArm.xRot += -0.66F + offerWave;
            this.leftArm.xRot += -0.66F - offerWave;
            this.rightArm.yRot += -0.28F;
            this.leftArm.yRot += 0.28F;
            this.head.xRot += 0.08F;
        } else {
            this.rightArm.xRot += this.leftLeg.xRot * 0.65F;
            this.leftArm.xRot += this.rightLeg.xRot * 0.65F;
            applyRoleIdle(state, age, idle);
        }

        this.witchHat.visible = state.role == 0;
        this.witchPack.visible = state.role == 0;
        this.goblinEars.visible = state.role == 1;
        this.goblinPack.visible = state.role == 1;
        this.lens.visible = state.role == 2;
        this.researcherPack.visible = state.role == 2;
        float lensScale = 1.0F + CreatureMotion.breathe(age, 0.18F) * 0.08F;
        this.lens.xScale = lensScale;
        this.lens.yScale = lensScale;
        this.lens.zScale = lensScale;
        this.witchHat.zRot += CreatureMotion.wave(age, 0.04F, 0.2F) * 0.025F * idle;
        this.witchHatTip.zRot += CreatureMotion.wave(age, 0.075F, 0.8F) * 0.06F;
        this.witchHatTip.xRot += CreatureMotion.wave(age, 0.06F, 1.4F) * 0.05F;
    }

    private void applyRoleIdle(ArcaneSettlerRenderState state, float age, float idle) {
        if (idle <= 0.01F) {
            return;
        }
        switch (state.role) {
            case 0 -> {
                float stir = CreatureMotion.wave(age, 0.075F, 0.0F);
                float circle = CreatureMotion.wave(age, 0.075F, Mth.HALF_PI);
                this.leftArm.xRot += (-0.72F + stir * 0.10F) * idle;
                this.leftArm.zRot += (0.18F + circle * 0.10F) * idle;
                this.rightArm.xRot += -0.24F * idle;
                this.head.xRot += 0.10F * idle;
            }
            case 1 -> {
                float rub = CreatureMotion.wave(age, 0.32F, 0.0F);
                this.rightArm.xRot += (-0.64F + rub * 0.05F) * idle;
                this.leftArm.xRot += (-0.64F - rub * 0.05F) * idle;
                this.rightArm.yRot += -0.24F * idle;
                this.leftArm.yRot += 0.24F * idle;
            }
            case 2 -> {
                this.rightArm.xRot += -1.05F * idle;
                this.rightArm.yRot += CreatureMotion.wave(age, 0.045F, 0.0F) * 0.34F * idle;
                this.rightArm.zRot += -0.18F * idle;
                this.head.yRot += CreatureMotion.wave(age, 0.045F, 0.6F) * 0.22F * idle;
            }
            default -> {
            }
        }
    }
}
