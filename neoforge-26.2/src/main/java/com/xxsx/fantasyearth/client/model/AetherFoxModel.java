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
    private final ModelPart leftEarFin;
    private final ModelPart rightEarFin;
    private final ModelPart body;
    private final ModelPart[] legs;
    private final ModelPart leftTail;
    private final ModelPart rightTail;
    private final ModelPart leftTailMid;
    private final ModelPart rightTailMid;

    public AetherFoxModel(ModelPart root) {
        super(root, RenderTypes::entityCutout);
        head = root.getChild("head");
        leftEarFin = head.getChild("left_ear_fin");
        rightEarFin = head.getChild("right_ear_fin");
        body = root.getChild("body");
        legs = new ModelPart[]{root.getChild("front_right_leg"), root.getChild("front_left_leg"),
                root.getChild("back_right_leg"), root.getChild("back_left_leg")};
        leftTail = root.getChild("left_tail");
        rightTail = root.getChild("right_tail");
        leftTailMid = leftTail.getChild("mid");
        rightTailMid = rightTail.getChild("mid");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("body", CubeListBuilder.create()
                        .texOffs(0, 24).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 12.0F)
                        .texOffs(42, 25).addBox(-3.6F, -4.7F, -5.0F, 7.2F, 1.2F, 10.0F, new CubeDeformation(0.08F)),
                PartPose.offset(0.0F, 15.0F, 0.0F));
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -4.0F, -5.0F, 8.0F, 7.0F, 7.0F)
                        .texOffs(30, 0).addBox(-2.0F, -0.5F, -8.0F, 4.0F, 3.0F, 4.0F),
                PartPose.offset(0.0F, 12.0F, -6.0F));
        head.addOrReplaceChild("left_ear_fin", CubeListBuilder.create().texOffs(48, 0)
                        .addBox(0.0F, -6.0F, -1.0F, 5.0F, 6.0F, 1.0F),
                PartPose.offsetAndRotation(1.0F, -3.0F, -1.0F, 0.0F, -0.18F, -0.15F));
        head.addOrReplaceChild("right_ear_fin", CubeListBuilder.create().texOffs(60, 0)
                        .addBox(-5.0F, -6.0F, -1.0F, 5.0F, 6.0F, 1.0F),
                PartPose.offsetAndRotation(-1.0F, -3.0F, -1.0F, 0.0F, 0.18F, 0.15F));
        leg(root, "front_right_leg", 0, 48, -2.5F, -4.0F);
        leg(root, "front_left_leg", 12, 48, 2.5F, -4.0F);
        leg(root, "back_right_leg", 24, 48, -2.5F, 4.0F);
        leg(root, "back_left_leg", 36, 48, 2.5F, 4.0F);
        PartDefinition leftTail = root.addOrReplaceChild("left_tail", CubeListBuilder.create().texOffs(0, 64)
                        .addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(2.0F, 14.0F, 5.0F, 0.35F, 0.22F, 0.0F));
        PartDefinition leftTailMid = leftTail.addOrReplaceChild("mid", CubeListBuilder.create().texOffs(48, 64)
                        .addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.3F, 0.12F, 0.12F, 0.0F));
        leftTailMid.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(0, 84)
                        .addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.3F, 0.10F, 0.10F, 0.0F));

        PartDefinition rightTail = root.addOrReplaceChild("right_tail", CubeListBuilder.create().texOffs(24, 64)
                        .addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 7.0F),
                PartPose.offsetAndRotation(-2.0F, 14.0F, 5.0F, 0.35F, -0.22F, 0.0F));
        PartDefinition rightTailMid = rightTail.addOrReplaceChild("mid", CubeListBuilder.create().texOffs(74, 64)
                        .addBox(-2.5F, -2.5F, 0.0F, 5.0F, 5.0F, 7.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.3F, 0.12F, -0.12F, 0.0F));
        rightTailMid.addOrReplaceChild("tip", CubeListBuilder.create().texOffs(26, 84)
                        .addBox(-3.0F, -3.0F, 0.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 6.3F, 0.10F, -0.10F, 0.0F));
        return LayerDefinition.create(mesh, 128, 128);
    }

    private static void leg(PartDefinition root, String name, int u, int v, float x, float z) {
        root.addOrReplaceChild(name, CubeListBuilder.create().texOffs(u, v)
                        .addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F),
                PartPose.offset(x, 18.0F, z));
    }

    @Override    public void setupAnim(FamiliarRenderState state) {
        super.setupAnim(state);
        float age = state.ageInTicks;
        float walk = state.walkAnimationPos;
        float speed = Math.min(state.walkAnimationSpeed, 1.0F);
        float idle = CreatureMotion.idleBlend(speed);
        float breathe = CreatureMotion.breathe(age, 0.11F);
        float alert = state.aggressive ? 1.0F : 0.0F;
        float wind = CreatureMotion.windUp(state.attackProgress);
        float strike = CreatureMotion.strike(state.attackProgress);

        head.yRot = CreatureMotion.clampLook(state.yRot * Mth.DEG_TO_RAD, 0.85F);
        head.xRot = CreatureMotion.clampLook(state.xRot * Mth.DEG_TO_RAD, 0.5F) + breathe * 0.02F - wind * 0.12F;
        head.zRot = CreatureMotion.wave(age, 0.045F, 0.0F) * 0.03F + alert * 0.04F;

        float earFlickLeft = CreatureMotion.twitch(age, 0.13F, 1.7F, 0.20F);
        float earFlickRight = CreatureMotion.twitch(age, 0.10F, 4.3F, 0.20F);
        leftEarFin.zRot = -0.15F - earFlickLeft - alert * 0.05F;
        rightEarFin.zRot = 0.15F + earFlickRight + alert * 0.05F;
        leftEarFin.xRot = -0.10F * alert - CreatureMotion.twitch(age, 0.07F, 0.6F, 0.12F);
        rightEarFin.xRot = -0.10F * alert - CreatureMotion.twitch(age, 0.08F, 2.2F, 0.12F);
        leftEarFin.zScale = 1.0F + breathe * 0.05F;
        rightEarFin.zScale = leftEarFin.zScale;

        legs[0].xRot = CreatureMotion.gait(walk, 0.0F, 1.05F, speed);
        legs[1].xRot = CreatureMotion.gait(walk, Mth.PI, 1.05F, speed);
        legs[2].xRot = legs[1].xRot;
        legs[3].xRot = legs[0].xRot;
        legs[0].zRot = -0.03F - Math.max(0.0F, legs[0].xRot) * 0.05F;
        legs[1].zRot = 0.03F + Math.max(0.0F, legs[1].xRot) * 0.05F;
        legs[2].zRot = -legs[1].zRot;
        legs[3].zRot = -legs[0].zRot;

        body.y = 15.0F + breathe * 0.06F + Mth.cos(walk * 1.33F) * 0.05F * speed;
        body.xRot = -alert * 0.02F + Mth.sin(walk * 0.67F) * 0.015F * speed - strike * 0.10F;
        body.zRot = CreatureMotion.wave(age, 0.05F, 1.1F) * 0.012F * idle;
        body.yScale = 1.0F + breathe * 0.015F;
        body.xScale = 1.0F - breathe * 0.008F;

        float tailWave = CreatureMotion.wave(age, 0.10F, 0.0F) * (0.22F + state.stability * 0.18F);
        leftTail.yRot = 0.22F + tailWave;
        rightTail.yRot = -0.22F - tailWave;
        leftTailMid.yRot = 0.12F + tailWave * 0.55F;
        rightTailMid.yRot = -0.12F - tailWave * 0.55F;
        leftTail.xRot = 0.35F + breathe * 0.03F - alert * 0.10F + wind * 0.22F;
        rightTail.xRot = leftTail.xRot;
        leftTailMid.xRot = 0.12F + CreatureMotion.wave(age, 0.14F, 0.9F) * 0.05F;
        rightTailMid.xRot = leftTailMid.xRot;

        head.xRot -= strike * 0.45F;
        legs[0].xRot -= strike * 0.34F;
        legs[1].xRot -= strike * 0.34F;
        body.z -= strike * 0.55F;

        if (state.sitting) {
            body.xRot = 0.18F;
            body.y = 15.0F;
            legs[0].xRot = -0.10F;
            legs[1].xRot = -0.10F;
            legs[2].xRot = -1.08F;
            legs[3].xRot = -1.08F;
            legs[0].zRot = -0.10F;
            legs[1].zRot = 0.10F;
            leftTail.xRot = 0.92F;
            rightTail.xRot = 0.92F;
            leftTail.yRot = 0.34F + tailWave * 0.5F;
            rightTail.yRot = -0.34F - tailWave * 0.5F;
        }
    }
}
