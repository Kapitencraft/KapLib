package net.kapitencraft.kap_lib.registry.custom.camera;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.client.cam.rot.FixedPositionRotator;
import net.kapitencraft.kap_lib.client.cam.rot.FixedRotator;
import net.kapitencraft.kap_lib.client.cam.rot.Rotator;
import net.kapitencraft.kap_lib.client.cam.rot.TrackingEntityRotator;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistryKeys;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface Rotators {
    DeferredRegister<Rotator.Type<?>> REGISTRY = KapLibMod.registry(ExtraRegistryKeys.CAMERA_ROTATORS);

    RegistryObject<FixedPositionRotator.Type> FIXED_POSITION = REGISTRY.register("fixed_position", FixedPositionRotator.Type::new);
    RegistryObject<FixedRotator.Type> FIXED_ROTATION = REGISTRY.register("fixed_rotation", FixedRotator.Type::new);
    RegistryObject<TrackingEntityRotator.Type> TRACKING_ENTITY = REGISTRY.register("tracking_entity", TrackingEntityRotator.Type::new);
}
