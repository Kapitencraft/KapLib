package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.kapitencraft.kap_lib.registry.custom.core.ExtraRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface TestCooldowns {
    DeferredRegister<Cooldown> REGISTRY = KapLibMod.registry(ExtraRegistries.Keys.COOLDOWNS);

    RegistryObject<Cooldown> TEST = REGISTRY.register("test", () -> new Cooldown(600, l -> {
        if (l instanceof Player player) player.sendSystemMessage(Component.literal("E"));
    }));
}
