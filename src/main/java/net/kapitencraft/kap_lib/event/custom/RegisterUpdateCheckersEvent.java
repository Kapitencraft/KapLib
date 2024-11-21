package net.kapitencraft.kap_lib.event.custom;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.regex.Pattern;

/**
 * used to register update checkers for Modrinth
 */
public class RegisterUpdateCheckersEvent extends Event implements IModBusEvent {
    private final BiConsumer<String, String> sink;

    public static final Pattern VERSION_PATTERN = Pattern.compile("\\d+.\\d+(.\\d+)?");
    public static final Pattern DEFAULT_PATTERN = Pattern.compile("v(" + VERSION_PATTERN.pattern() + ")-mc" + VERSION_PATTERN.pattern() + "-FML" + VERSION_PATTERN.pattern());

    public RegisterUpdateCheckersEvent(BiConsumer<String, String> sink) {
        this.sink = sink;
    }

    /**
     * @param projectId the id or slug of the Modrinth project
     * @param modId the id of the mod
     */
    public void register(String projectId, String modId) {
        sink.accept(projectId, modId);
    }

    public void register(String modId) {
        register(modId, modId);
    }
}
