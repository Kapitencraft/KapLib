package net.kapitencraft.kap_lib.registry;

import net.kapitencraft.kap_lib.cooldown.registry.CooldownRegistries;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.cooldown.Cooldown;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.registries.DeferredRegister;

public interface TestCooldowns {
    DeferredRegister<Cooldown> REGISTRY = LibConstants.registry(CooldownRegistries.Keys.COOLDOWNS);

    Holder<Cooldown> TEST = REGISTRY.register("test", () -> new Cooldown(600, l -> {
        if (l instanceof Player player) player.sendSystemMessage(Component.literal("E"));
    }));
}
