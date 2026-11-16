package net.kapitencraft.kap_lib.publish.info;

import com.google.gson.*;
import net.kapitencraft.kap_lib.publish.GsonHelper;
import net.kapitencraft.kap_lib.publish.changelog.Changelog;
import net.kapitencraft.kap_lib.publish.changelog.ListChangelog;
import net.kapitencraft.kap_lib.publish.changelog.PlainChangelog;

import java.lang.reflect.Type;

public record ChangelogInfo(String format, String style) {

    public static final ChangelogInfo DEFAULT = new ChangelogInfo("html", "list");

    public Changelog createLog(String categoriesLocation) {
        return switch (style) {
            case "list" -> {
                ListChangelog.gatherCategories(categoriesLocation);
                yield new ListChangelog();
            }
            case "none" -> new PlainChangelog();
            default -> throw new IllegalArgumentException("unknown style: " + style);
        };
    }

    public String compileLog(Changelog log) {
        return switch (format) {
            case "html" -> log.toHtml();
            case "md" -> log.toMd();
            case "plain" -> log.toPlainText();
            default -> throw new IllegalArgumentException("unknown format: " + format);
        };
    }

    public static class Deserializer implements JsonDeserializer<ChangelogInfo> {

        @Override
        public ChangelogInfo deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            if (!jsonElement.isJsonObject()) throw new JsonParseException("changelog config must be object");
            JsonObject object = jsonElement.getAsJsonObject();
            String format = GsonHelper.getOptionalAsString(object, "format", "html");
            String style = GsonHelper.getOptionalAsString(object, "style", "list");
            return new ChangelogInfo(format, style);
        }
    }
}