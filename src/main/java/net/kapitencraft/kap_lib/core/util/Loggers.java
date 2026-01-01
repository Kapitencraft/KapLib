package net.kapitencraft.kap_lib.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Loggers {
    Logger PARTICLE_ENGINE = getMarker("ParticleEngine");
    Logger SPAWN_TABLE_MANAGER = getMarker("SpawnTableManager");

    static Logger getMarker(String name) {
        return LoggerFactory.getLogger(name);
    }
}
