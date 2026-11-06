package com.xxsx.fantasyearth;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/** F-001 Aether Crystal component (charge_AEU 0..60, capacity_AEU 60, signature, schema_version 1). */
public record AetherCrystalComponent(double chargeAeu, double capacityAeu, String signature, int schemaVersion) {
    public static final double CAPACITY_AEU = 60.0D;
    public static final double CHARGE_BATCH_AEU = 20.0D;
    public static final double MANA_BATCH_MP = 6.0D;
    public static final int CURRENT_SCHEMA = 1;

    public static final AetherCrystalComponent EMPTY = new AetherCrystalComponent(0.0D, CAPACITY_AEU, "", CURRENT_SCHEMA);
    public static final AetherCrystalComponent FULL = new AetherCrystalComponent(CAPACITY_AEU, CAPACITY_AEU, "", CURRENT_SCHEMA);

    public static final Codec<AetherCrystalComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.DOUBLE.optionalFieldOf("charge_AEU", 0.0D).forGetter(AetherCrystalComponent::chargeAeu),
            Codec.DOUBLE.optionalFieldOf("capacity_AEU", CAPACITY_AEU).forGetter(AetherCrystalComponent::capacityAeu),
            Codec.string(0, 32).optionalFieldOf("signature", "").forGetter(AetherCrystalComponent::signature),
            Codec.INT.optionalFieldOf("schema_version", CURRENT_SCHEMA).forGetter(AetherCrystalComponent::schemaVersion)
    ).apply(instance, AetherCrystalComponent::new));

    public AetherCrystalComponent {
        capacityAeu = CAPACITY_AEU;
        chargeAeu = Double.isFinite(chargeAeu) ? Math.max(0.0D, Math.min(CAPACITY_AEU, chargeAeu)) : 0.0D;
        signature = signature == null ? "" : (signature.length() > 32 ? signature.substring(0, 32) : signature);
        schemaVersion = CURRENT_SCHEMA;
    }

    public static AetherCrystalComponent read(ItemStack stack) {
        AetherCrystalComponent val = stack.get(FantasyEarth.AETHER_CRYSTAL_COMPONENT.get());
        return val == null ? EMPTY : val;
    }

    public AetherCrystalComponent withCharge(double nextCharge, String nextSignature) {
        String sig = (nextSignature == null || nextSignature.isEmpty()) ? signature : nextSignature;
        return new AetherCrystalComponent(nextCharge, CAPACITY_AEU, sig, CURRENT_SCHEMA);
    }

    public static String computeSignature(Level level, BlockPos pos, String sourceKey) {
        ChunkPos cp = ChunkPos.containing(pos);
        String raw = level.dimension().identifier() + ":" + cp.x() + ":" + cp.z() + ":" + (sourceKey == null ? "" : sourceKey);
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash).substring(0, 16);
        } catch (Exception ignored) {
            return Integer.toHexString(raw.hashCode());
        }
    }
}
