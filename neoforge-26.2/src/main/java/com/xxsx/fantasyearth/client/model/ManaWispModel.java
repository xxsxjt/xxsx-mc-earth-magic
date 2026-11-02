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
    private final ModelPart crown;
    private final ModelPart ring;
    private final ModelPart innerRing;
    private final ModelPart leftFin;
    private final ModelPart rightFin;
    private final ModelPart flame;
    private final ModelPart flameTip;
    private final ModelPart filamentFl;
    private final ModelPart filamentFr;
    private final ModelPart filamentBl;
    private final ModelPart filamentBr;
    private final ModelPart moteA;
    private final ModelPart moteB;
    private final ModelPart moteC;

    public ManaWispModel(ModelPart root) {
        super(root, RenderTypes::entityTranslucent);
        this.core = root.getChild("core");
        this.crown = this.core.getChild("crown");
        this.ring = root.getChild("ring");
        this.innerRing = root.getChild("inner_ring");
        this.leftFin = root.getChild("left_fin");
        this.rightFin = root.getChild("right_fin");
        this.flame = root.getChild("flame");
        this.flameTip = this.flame.getChild("flame_tip");
        this.filamentFl = this.flame.getChild("filament_fl");
        this.filamentFr = this.flame.getChild("filament_fr");
        this.filamentBl = this.flame.getChild("filament_bl");
        this.filamentBr = this.flame.getChild("filament_br");
        this.moteA = root.getChild("mote_a");
        this.moteB = root.getChild("mote_b");
        this.moteC = root.getChild("mote_c");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        PartDefinition p_core_1 = root.addOrReplaceChild("core", CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-2.5F, -2.5F, -2.5F, 5.0F, 5.0F, 5.0F)
                        .texOffs(21, 0).addBox(-4.0F, -3.5F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.12F)),
                PartPose.offset(0.0F, 12.5F, 0.0F));
        PartDefinition p_crown_2 = p_core_1.addOrReplaceChild("crown", CubeListBuilder.create()
                        .texOffs(54, 0).addBox(-4.5F, -3.0F, -4.5F, 9.0F, 3.0F, 9.0F)
                        .texOffs(91, 0).addBox(-3.0F, -5.0F, -3.0F, 6.0F, 2.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -3.5F, 0.0F, -0.08F, 0.0F, 0.04F));
        p_crown_2.addOrReplaceChild("brim_front", CubeListBuilder.create()
                        .texOffs(0, 16).addBox(-5.0F, 0.0F, -2.0F, 10.0F, 1.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, -4.2F, 0.34F, 0.0F, 0.0F));
        p_crown_2.addOrReplaceChild("brim_back", CubeListBuilder.create()
                        .texOffs(27, 16).addBox(-5.0F, 0.0F, -1.0F, 10.0F, 1.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, -0.5F, 4.2F, -0.34F, 0.0F, 0.0F));
        p_crown_2.addOrReplaceChild("brim_left", CubeListBuilder.create()
                        .texOffs(54, 16).addBox(-1.0F, 0.0F, -4.0F, 3.0F, 1.0F, 8.0F),
                PartPose.offsetAndRotation(4.2F, -0.5F, 0.0F, 0.0F, 0.0F, -0.34F));
        p_crown_2.addOrReplaceChild("brim_right", CubeListBuilder.create()
                        .texOffs(77, 16).addBox(-2.0F, 0.0F, -4.0F, 3.0F, 1.0F, 8.0F),
                PartPose.offsetAndRotation(-4.2F, -0.5F, 0.0F, 0.0F, 0.0F, 0.34F));
        p_crown_2.addOrReplaceChild("bud_large", CubeListBuilder.create()
                        .texOffs(100, 16).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F)
                        .texOffs(105, 16).addBox(-1.5F, -3.5F, -1.5F, 3.0F, 2.0F, 3.0F),
                PartPose.offsetAndRotation(1.8F, -4.8F, -0.8F, 0.16F, 0.2F, 0.36F));
        p_crown_2.addOrReplaceChild("bud_small", CubeListBuilder.create()
                        .texOffs(118, 16).addBox(-0.5F, -2.0F, -0.5F, 1.0F, 2.0F, 1.0F)
                        .texOffs(0, 26).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 1.0F, 2.0F),
                PartPose.offsetAndRotation(-2.0F, -4.4F, 1.2F, -0.22F, -0.15F, -0.42F));
        root.addOrReplaceChild("ring", CubeListBuilder.create()
                        .texOffs(9, 26).addBox(-5.5F, -0.5F, -5.5F, 11.0F, 1.0F, 1.0F)
                        .texOffs(34, 26).addBox(-5.5F, -0.5F, 4.5F, 11.0F, 1.0F, 1.0F)
                        .texOffs(59, 26).addBox(-5.5F, -0.5F, -4.5F, 1.0F, 1.0F, 9.0F)
                        .texOffs(80, 26).addBox(4.5F, -0.5F, -4.5F, 1.0F, 1.0F, 9.0F),
                PartPose.offsetAndRotation(0.0F, 14.5F, 0.0F, 0.24F, 0.0F, -0.2F));
        root.addOrReplaceChild("inner_ring", CubeListBuilder.create()
                        .texOffs(101, 26).addBox(4.5F, -1.5F, -1.0F, 1.0F, 3.0F, 2.0F)
                        .texOffs(108, 26).addBox(-5.5F, -1.5F, -1.0F, 1.0F, 3.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 12.5F, 0.0F, -0.34F, 0.35F, 0.28F));
        root.addOrReplaceChild("left_fin", CubeListBuilder.create()
                        .texOffs(115, 26).addBox(0.0F, -3.0F, -0.5F, 5.0F, 4.0F, 1.0F)
                        .texOffs(0, 37).addBox(0.5F, 1.0F, -0.5F, 3.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(3.8F, 12.0F, 0.0F, 0.12F, -0.28F, -0.32F));
        root.addOrReplaceChild("right_fin", CubeListBuilder.create()
                        .texOffs(9, 37).addBox(-5.0F, -3.0F, -0.5F, 5.0F, 4.0F, 1.0F)
                        .texOffs(22, 37).addBox(-3.5F, 1.0F, -0.5F, 3.0F, 3.0F, 1.0F),
                PartPose.offsetAndRotation(-3.8F, 12.0F, 0.0F, 0.12F, 0.28F, 0.32F));
        PartDefinition p_flame_13 = root.addOrReplaceChild("flame", CubeListBuilder.create()
                        .texOffs(31, 37).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F),
                PartPose.offsetAndRotation(0.0F, 15.5F, 0.0F, 0.08F, 0.0F, 0.0F));
        p_flame_13.addOrReplaceChild("flame_tip", CubeListBuilder.create()
                        .texOffs(44, 37).addBox(-1.0F, 0.0F, -1.0F, 2.0F, 4.0F, 2.0F),
                PartPose.offsetAndRotation(0.0F, 3.8F, 0.0F, 0.14F, 0.0F, 0.0F));
        p_flame_13.addOrReplaceChild("filament_fl", CubeListBuilder.create()
                        .texOffs(53, 37).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F),
                PartPose.offsetAndRotation(2.4F, -0.5F, -2.0F, 0.16F, 0.0F, -0.18F));
        p_flame_13.addOrReplaceChild("filament_fr", CubeListBuilder.create()
                        .texOffs(58, 37).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 6.0F, 1.0F),
                PartPose.offsetAndRotation(-2.4F, -0.5F, -2.0F, 0.16F, 0.0F, 0.18F));
        p_flame_13.addOrReplaceChild("filament_bl", CubeListBuilder.create()
                        .texOffs(63, 37).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F),
                PartPose.offsetAndRotation(2.2F, -0.5F, 2.2F, -0.18F, 0.0F, -0.16F));
        p_flame_13.addOrReplaceChild("filament_br", CubeListBuilder.create()
                        .texOffs(68, 37).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 5.0F, 1.0F),
                PartPose.offsetAndRotation(-2.2F, -0.5F, 2.2F, -0.18F, 0.0F, 0.16F));
        PartDefinition p_mote_a_19 = root.addOrReplaceChild("mote_a", CubeListBuilder.create()
                        .texOffs(73, 37).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(5.5F, 11.0F, -2.0F, 0.35F, 0.45F, 0.25F));
        p_mote_a_19.addOrReplaceChild("glow", CubeListBuilder.create()
                        .texOffs(78, 37).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.05F)),
                PartPose.ZERO);
        PartDefinition p_mote_b_21 = root.addOrReplaceChild("mote_b", CubeListBuilder.create()
                        .texOffs(87, 37).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(-5.2F, 13.5F, 2.2F, 0.35F, 0.45F, 0.25F));
        p_mote_b_21.addOrReplaceChild("glow", CubeListBuilder.create()
                        .texOffs(92, 37).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.05F)),
                PartPose.ZERO);
        PartDefinition p_mote_c_23 = root.addOrReplaceChild("mote_c", CubeListBuilder.create()
                        .texOffs(101, 37).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F),
                PartPose.offsetAndRotation(0.0F, 9.5F, 5.2F, 0.35F, 0.45F, 0.25F));
        p_mote_c_23.addOrReplaceChild("glow", CubeListBuilder.create()
                        .texOffs(106, 37).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.05F)),
                PartPose.ZERO);
        return LayerDefinition.create(mesh, 128, 128);
    }

    @Override
    public void setupAnim(FamiliarRenderState state) {
        super.setupAnim(state);
        float age = state.ageInTicks;
        float breathe = CreatureMotion.breathe(age, 0.12F);
        float strike = CreatureMotion.strike(state.attackProgress);

        float bob = breathe * 0.75F + CreatureMotion.breathe(age, 0.31F) * 0.16F;
        this.core.y += bob;
        this.core.yRot += age * 0.035F;
        this.crown.yRot -= age * 0.055F;
        this.crown.zRot += CreatureMotion.wave(age, 0.08F, 0.5F) * 0.06F;

        this.ring.y += breathe * 0.5F;
        this.ring.yRot += age * 0.045F;
        this.ring.xRot += CreatureMotion.wave(age, 0.07F, 0.4F) * 0.22F;
        this.ring.zRot += CreatureMotion.wave(age, 0.05F, 1.7F) * 0.22F;

        this.innerRing.y += breathe * 0.65F;
        this.innerRing.yRot -= age * 0.065F;
        this.innerRing.xRot += CreatureMotion.wave(age, 0.09F, 1.1F) * 0.24F;
        this.innerRing.zRot += CreatureMotion.wave(age, 0.08F, 2.3F) * 0.24F;

        float flap = CreatureMotion.wave(age, 0.18F, 0.0F) * 0.22F;
        this.leftFin.zRot -= flap;
        this.rightFin.zRot += flap;
        this.leftFin.y += bob;
        this.rightFin.y += bob;

        this.flame.y += bob;
        this.flame.yRot += age * 0.05F;
        this.flameTip.xRot += CreatureMotion.wave(age, 0.21F, 0.6F) * 0.18F;
        this.flameTip.zRot += CreatureMotion.wave(age, 0.19F, 1.9F) * 0.18F;
        float sway = CreatureMotion.wave(age, 0.14F, 0.4F) * 0.16F;
        this.filamentFl.zRot -= sway;
        this.filamentFr.zRot += sway;
        this.filamentBl.xRot -= sway;
        this.filamentBr.xRot += sway;

        float pulse = 1.0F + CreatureMotion.breathe(age, 0.24F) * 0.06F;
        this.core.xScale = pulse;
        this.core.zScale = pulse;

        orbit(this.moteA, age, 0.0F, 5.2F, 1.6F, 0.9F);
        orbit(this.moteB, age, Mth.TWO_PI / 3.0F, 5.6F, 2.0F, 1.3F);
        orbit(this.moteC, age, Mth.TWO_PI * 2.0F / 3.0F, 4.8F, 1.2F, 1.7F);

        float attack = Mth.sin(state.attackProgress * Mth.PI);
        this.ring.xScale = 1.0F + attack * 0.38F + strike * 0.20F;
        this.ring.zScale = this.ring.xScale;
        this.core.yScale = 1.0F + attack * 0.16F;

        if (state.sitting) {
            this.core.y += 1.4F;
            this.ring.y += 1.4F;
            this.innerRing.y += 1.4F;
            this.flame.y += 1.4F;
        }
    }

    private static void orbit(ModelPart mote, float age, float offset, float radius, float tilt, float speed) {
        float angle = age * 0.07F * speed + offset;
        mote.x = Mth.cos(angle) * radius;
        mote.z = Mth.sin(angle) * radius;
        mote.y = 12.0F + Mth.sin(angle * 1.6F) * tilt;
        mote.yRot = angle;
    }
}
