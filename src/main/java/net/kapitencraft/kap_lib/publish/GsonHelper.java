package net.kapitencraft.kap_lib.publish;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.ApiStatus;

/**
 * helper for gson related code<br>
 * use {@link com.mojang.serialization.Codec Codecs} inside MC code
 */
@ApiStatus.Internal
public class GsonHelper {

    public static String getAsString(JsonObject object, String memberName) throws JsonParseException {
        JsonElement element = object.get(memberName);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString())
            throw new JsonParseException("member '" + memberName + "' should be string");
        return element.getAsJsonPrimitive().getAsString();
    }

    public static String getOptionalAsString(JsonObject object, String memberName) throws JsonParseException {
        if (!object.has(memberName)) {
            return null;
        }
        JsonElement element = object.get(memberName);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString())
            throw new JsonParseException("member '" + memberName + "' should be string");
        return element.getAsJsonPrimitive().getAsString();
    }

    public static String getOptionalAsString(JsonObject object, String memberName, String fallback) throws JsonParseException {
        if (!object.has(memberName)) {
            return null;
        }
        JsonElement element = object.get(memberName);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isString()) return fallback;
        return element.getAsJsonPrimitive().getAsString();
    }

    public static int getAsInt(JsonObject object, String memberName) {
        JsonElement element = object.get(memberName);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isNumber())
            throw new JsonParseException("member '" + memberName + "' should be string");
        return element.getAsJsonPrimitive().getAsInt();
    }

    public static boolean getAsBoolean(JsonObject object, String memberName) {
        JsonElement element = object.get(memberName);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean())
            throw new JsonParseException("member '" + memberName + "' should be string");
        return element.getAsJsonPrimitive().getAsBoolean();
    }

    public static boolean getOptionalAsBoolean(JsonObject object, String memberName, boolean fallback) {
        JsonElement element = object.get(memberName);
        if (!element.isJsonPrimitive() || !element.getAsJsonPrimitive().isBoolean()) return fallback;
        return element.getAsJsonPrimitive().getAsBoolean();
    }
}
