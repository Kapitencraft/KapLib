package net.kapitencraft.kap_lib.particle;

import com.mojang.brigadier.CommandDispatcher;
import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.particle.config.ParticleClientModConfig;
import net.kapitencraft.kap_lib.particle.registry.ExtraParticleTypes;
import net.kapitencraft.kap_lib.particle.registry.particle_animation.*;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import org.jetbrains.annotations.ApiStatus;

@Mod(ParticleModule.MODULE_ID)
public class ParticleModule {

    public static final String MODULE_ID = LibConstants.MOD_ID + "_particle";

    public ParticleModule(IEventBus modEventBus, ModContainer container) {
        ExtraParticleTypes.REGISTRY.register(modEventBus);
        ActivationTriggers.REGISTRY.register(modEventBus);
        ElementTypes.REGISTRY.register(modEventBus);
        FinalizerTypes.REGISTRY.register(modEventBus);
        SpawnerTypes.REGISTRY.register(modEventBus);
        TerminatorTriggers.REGISTRY.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(ParticleModule::registerClient);
        NeoForge.EVENT_BUS.addListener(ParticleModule::registerServer);

        container.registerConfig(ModConfig.Type.CLIENT, ParticleClientModConfig.SPEC);
    }

    /**
     * register library commands
     */
    @ApiStatus.Internal
    static void registerClient(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ParticleClientTestCommand.register(dispatcher);
    }

    @ApiStatus.Internal
    static void registerServer(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        ParticleServerTestCommand.register(dispatcher);
    }
}
