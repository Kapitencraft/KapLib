package net.kapitencraft.kap_lib.event.custom;

import net.kapitencraft.kap_lib.stream.Consumers;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.regex.Pattern;

/**
 * used to register update checkers for Modrinth
 */
public class RegisterUpdateCheckersEvent extends Event implements IModBusEvent {
    private final Consumers.C3<String, String, Pattern> sink;

    public static final Pattern VERSION_PATTERN = Pattern.compile("\\d+.\\d+(.\\d+)?");
    public static final Pattern DEFAULT_PATTERN = Pattern.compile("v(" + VERSION_PATTERN.pattern() + ")-mc" + VERSION_PATTERN.pattern() + "-FML" + VERSION_PATTERN.pattern());

    public RegisterUpdateCheckersEvent(Consumers.C3<String, String, Pattern> sink) {
        this.sink = sink;
    }

    /**
     * @param projectId the id or slug of the Modrinth project
     * @param modId the id of the mod
     * @param versionExtractor a pattern able to extract the mod's version from the version string. must contain exactly o
     */
    public void register(String projectId, String modId, Pattern versionExtractor) {
        sink.apply(projectId, modId, versionExtractor);
    }

    /**
     * normally used for projects using the autoPublish subsystem for publishing
     * @param modId the modId of the project
     * @see net.kapitencraft.kap_lib.publish.AutoPublisher AutoPublisher
     */
    public void register(String modId) {
        register(modId, modId, DEFAULT_PATTERN);
    }
}
