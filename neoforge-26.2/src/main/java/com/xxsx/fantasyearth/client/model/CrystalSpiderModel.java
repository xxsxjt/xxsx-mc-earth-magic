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
        head = root.getChild("head");
        leftFang = head.getChild("left_fang");
        rightFang = head.getChild("right_fang");
        abdomen = root.getChild("abdomen");
        crystals = abdomen.getChild("crystals");
        for (int i = 0; i < legs.length; i++) {
            legs[i] = root.getChild("leg_" + i);
            lowerLegs[i] = legs[i].getChild("lower");
        }
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition head = root.addOrReplaceChild("head", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-4.0F, -3.0F, -5.0F, 8.0F, 6.0F, 6.0F)
                        .texOffs(28, 0).addBox(-3.0F, -1.0F, -7.0F, 6.0F, 3.0F, 3.0F),
                PartPose.offset(0.0F, 16.0F, -4.0F));
        head.addOrReplaceChild("left_fang", CubeListBuilder.create()
                        .texOffs(28, 0).addBox(-0.6F, -0.5F, -2.5F, 1.4F, 2.2F, 2.6F),
                PartPose.offsetAndRotation(1.6F, 1.0F, -6.2F, 0.16F, -0.18F, 0.0F));
        head.addOrReplaceChild("right_fang", CubeListBuilder.create()
                        .texOffs(28, 0).addBox(-0.8F, -0.5F, -2.5F, 1.4F, 2.2F, 2.6F),
                PartPose.offsetAndRotation(-1.6F, 1.0F, -6.2F, 0.16F, 0.18F, 0.0F));
        PartDefinition abdomen = root.addOrReplaceChild("abdomen", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-6.0F, -4.2F, -4.0F, 12.0F, 8.4F, 12.0F)
                        .texOffs(48, 16).addBox(-6.2F, -4.5F, -3.5F, 12.4F, 5.0F, 10.0F, new CubeDeformation(0.22F)),
                PartPose.offset(0.0F, 15.0F, 2.0F));
        PartDefinition crystals = abdomen.addOrReplaceChild("crystals", CubeListBuilder.create(),
                PartPose.offset(0.0F, -3.0F, 1.0F));
        crystals.addOrReplaceChild("main", CubeListBuilder.create()
                        .texOffs(0, 40).addBox(-1.5F, -7.5F, -1.5F, 3.0F, 7.5F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.12F, 0.0F, 0.06F));
        crystals.addOrReplaceChild("left", CubeListBuilder.create()
                        .texOffs(14, 40).addBox(-1.0F, -5.5F, -1.0F, 2.0F, 5.5F, 2.0F),
                PartPose.offsetAndRotation(-3.0F, 0.4F, 1.2F, -0.15F, 0.0F, -0.44F));
        crystals.addOrReplaceChild("right", CubeListBuilder.create()
                        .texOffs(14, 40).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F),
                PartPose.offsetAndRotation(3.0F, 0.4F, -0.8F, -0.10F, 0.0F, 0.42F));
        crystals.addOrReplaceChild("rear", CubeListBuilder.create()
                        .texOffs(26, 40).addBox(-1.0F, -4.5F, -1.0F, 2.0F, 4.5F, 2.0F),
                PartPose.offsetAndRotation(2.2F, 0.7F, 3.0F, 0.24F, 0.0F, 0.34F));
        crystals.addOrReplaceChild("crown_shard", CubeListBuilder.create()
                        .texOffs(26, 40).addBox(-0.8F, -4.2F, -0.8F, 1.6F, 4.2F, 1.6F),
                PartPose.offsetAndRotation(-1.8F, 0.6F, -2.2F, -0.26F, 0.0F, -0.28F));
        for (int i = 0; i < 8; i++) {
            boolean left = i >= 4;
            int row = i % 4;
            float z = -3.0F + row * 2.2F;
            float yaw = (0.85F - row * 0.18F) * (left ? -1.0F : 1.0F);
            float roll = left ? 0.42F : -0.42F;
            PartDefinition leg = root.addOrReplaceChild("leg_" + i,
                    CubeListBuilder.create().texOffs(row * 20, 52)
                            .addBox(left ? 0.0F : -7.0F, -1.0F, -1.0F, 7.0F, 2.0F, 2.0F),
                    PartPose.offsetAndRotation(left ? 4.0F : -4.0F, 17.0F, z, 0.0F, yaw, roll));
            PartDefinition lower = leg.addOrReplaceChild("lower", CubeListBuilder.create().texOffs(row * 20, 60)
                            .addBox(left ? 0.0F : -8.0F, -0.9F, -0.9F, 8.0F, 1.8F, 1.8F),
                    PartPose.offsetAndRotation(left ? 6.7F : -6.7F, 0.0F, 0.0F,
                            0.0F, 0.0F, left ? 0.52F : -0.52F));
            lower.addOrReplaceChild("claw", CubeListBuilder.create().texOffs(row * 20, 60)
                            .addBox(left ? 0.0F : -2.2F, -0.6F, -0.6F, 2.2F, 1.2F, 1.2F),
                    PartPose.offsetAndRotation(left ? 7.6F : -7.6F, 0.0F, 0.0F,
                            0.0F, 0.0F, left ? 0.28F : -0.28F));
        }
        return LayerDefinition.create(mesh, 96, 80);
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

        head.yRot = CreatureMotion.clampLook(state.yRot * Mth.DEG_TO_RAD * 0.55F, 0.45F);
        head.xRot = CreatureMotion.clampLook(state.xRot * Mth.DEG_TO_RAD * 0.45F, 0.35F)
                + breathe * 0.02F - wind * 0.12F;
        head.zRot = CreatureMotion.wave(age, 0.05F, 0.9F) * 0.02F;
        float fangSwing = CreatureMotion.wave(age, 0.14F, 0.0F) * 0.08F + alert * 0.14F + strike * 0.44F;
        leftFang.yRot = -0.18F + fangSwing;
        rightFang.yRot = 0.18F - fangSwing;

        for (int i = 0; i < legs.length; i++) {
            boolean left = i >= 4;
            int row = i % 4;
            float phase = walk * 0.78F + row * 0.95F + (left ? Mth.PI : 0.0F);
            float baseYaw = (0.95F - row * 0.20F) * (left ? -1.0F : 1.0F);
            float reach = 0.22F + 0.06F * Mth.cos(age * 0.05F + row);
            legs[i].zRot = (left ? 0.40F : -0.40F) + Mth.cos(phase) * reach * speed;
            legs[i].yRot = baseYaw + Mth.sin(phase) * 0.18F * speed;
            lowerLegs[i].zRot = (left ? 0.54F : -0.54F) + Mth.sin(phase + 0.5F) * 0.14F * speed
                    + (left ? 1.0F : -1.0F) * strike * 0.18F;
        }

        float pulse = 1.0F + CreatureMotion.breathe(age, 0.18F) * 0.05F;
        crystals.xScale = pulse;
        crystals.yScale = pulse;
        crystals.zScale = pulse;
        crystals.yRot = CreatureMotion.wave(age, 0.03F, 0.0F) * 0.12F;

        abdomen.y = 15.0F + breathe * 0.05F + Mth.cos(walk * 0.9F) * 0.06F * speed - strike * 0.25F;
        abdomen.xRot = -alert * 0.04F + Mth.sin(walk * 0.9F) * 0.02F * speed - wind * 0.10F;
        abdomen.zRot = CreatureMotion.wave(age, 0.04F, 2.2F) * 0.015F * idle;

        head.z -= strike * 1.2F;
        head.xRot -= strike * 0.28F;
        for (int i = 0; i < legs.length; i++) {
            legs[i].zRot += (i >= 4 ? 1.0F : -1.0F) * wind * 0.12F;
        }
    }
}
