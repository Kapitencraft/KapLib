package net.kapitencraft.kap_lib.camera.registry;

import net.kapitencraft.kap_lib.camera.modifiers.*;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.camera.registry.custom.CameraRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public interface CameraModifiers {
    DeferredRegister<Modifier.Type<?>> REGISTRY = LibConstants.registry(CameraRegistries.Keys.CAMERA_MODIFIERS);

    Supplier<FixedPositionModifier.Type> FIXED_POSITION = REGISTRY.register("fixed_position", FixedPositionModifier.Type::new);
    Supplier<DelayModifier.Type> IDLE = REGISTRY.register("idle", DelayModifier.Type::new);
    Supplier<FixedTargetPositionModifier.Type> FIXED_TARGET_POSITION = REGISTRY.register("fixed_target_position", FixedTargetPositionModifier.Type::new);
    Supplier<FixedRotationModifier.Type> FIXED_ROTATION = REGISTRY.register("fixed_rotation", FixedRotationModifier.Type::new);
    Supplier<TrackingEntityRotator.Type> TRACKING_ENTITY = REGISTRY.register("tracking_entity_rotator", TrackingEntityRotator.Type::new);
    Supplier<GlideTowardsModifier.Type> GLIDE_TOWARDS = REGISTRY.register("glide_towards", GlideTowardsModifier.Type::new);
    Supplier<GroupModifier.Type> ROT_AND_POS = REGISTRY.register("rot_and_pos", GroupModifier.Type::new);
}
