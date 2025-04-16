package net.kapitencraft.kap_lib.registry.custom;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.cam.modifiers.*;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface CameraModifiers {
    DeferredRegister<Modifier.Type<?>> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.CAMERA_MODIFIERS);

    RegistryObject<FixedPositionModifier.Type> FIXED_POSITION = REGISTRY.register("fixed_position", FixedPositionModifier.Type::new);
    RegistryObject<DelayModifier.Type> IDLE = REGISTRY.register("idle", DelayModifier.Type::new);
    RegistryObject<FixedTargetPositionModifier.Type> FIXED_TARGET_POSITION = REGISTRY.register("fixed_target_position", FixedTargetPositionModifier.Type::new);
    RegistryObject<FixedRotationModifier.Type> FIXED_ROTATION = REGISTRY.register("fixed_rotation", FixedRotationModifier.Type::new);
    RegistryObject<TrackingEntityRotator.Type> TRACKING_ENTITY = REGISTRY.register("tracking_entity_rotator", TrackingEntityRotator.Type::new);
    RegistryObject<GlideTowardsModifier.Type> GLIDE_TOWARDS = REGISTRY.register("glide_towards", GlideTowardsModifier.Type::new);
    RegistryObject<GroupModifier.Type> ROT_AND_POS = REGISTRY.register("rot_and_pos", GroupModifier.Type::new);
}
