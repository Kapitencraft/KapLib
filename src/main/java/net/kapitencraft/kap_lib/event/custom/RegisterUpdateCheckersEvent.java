package net.kapitencraft.kap_lib.event.custom;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.regex.Pattern;

/**
 * used to register update checkers for Modrinth
 */
public class RegisterUpdateCheckersEvent extends Event implements IModBusEvent {
    private final TriConsumer<String, Pattern, String> sink;

    public static final Pattern VERSION_PATTERN = Pattern.compile("\\d+.\\d+(.\\d+)?");
    public static final Pattern DEFAULT_PATTERN = Pattern.compile("v(" + VERSION_PATTERN.pattern() + ")-mc" + VERSION_PATTERN.pattern() + "-FML" + VERSION_PATTERN.pattern());

    public RegisterUpdateCheckersEvent(TriConsumer<String, Pattern, String> sink) {
        this.sink = sink;
    }

    /**
     * @param projectId the id or slug of the Modrinth project
     * @param versionPattern the version pattern for the project, must contain exactly on group
     */
    public void register(String projectId, Pattern versionPattern, String modId) {
        sink.accept(projectId, versionPattern, modId);
    }

    public void register(String projectId, String modiD) {
        register(projectId, DEFAULT_PATTERN, modiD);
    }

    public void register(String modId) {
        register(modId, modId);
    }
}
