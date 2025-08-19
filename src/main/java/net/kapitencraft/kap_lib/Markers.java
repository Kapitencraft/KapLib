package net.kapitencraft.kap_lib;

import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public interface Markers {
    Marker REQUESTS = getMarker("Requests");
    Marker PARTICLE_ENGINE = getMarker("ParticleEngine");
    Marker REQUIREMENTS_MANAGER = getMarker("RequirementManager");
    Marker BONUS_MANAGER = getMarker("BonusManager");
    Marker UPDATE_CHECKER = getMarker("UpdateChecker");
    Marker SPAWN_TABLE_MANAGER = getMarker("SpawnTableManager");

    static Marker getMarker(String name) {
        return MarkerFactory.getMarker(name);
    }
}
