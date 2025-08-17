package net.kapitencraft.kap_lib.io.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.logging.LogUtils;
import net.kapitencraft.kap_lib.io.JsonHelper;
import org.slf4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

public class ModrinthUtils {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PROJECT_URL = "https://api.modrinth.com/v2/project/";

    public static Stream<JsonObject> readVersions(String projectId, String gameVersion, String agentName) throws IOException {

        String projectVersionURL = PROJECT_URL + projectId + "/version";
        String requestParams = "?loaders=" +
                URLEncoder.encode(JsonHelper.GSON.toJson(new String[]{"forge"}), StandardCharsets.UTF_8) +
                "&game_versions=" + URLEncoder.encode(JsonHelper.GSON.toJson(new Object[]{gameVersion}), StandardCharsets.UTF_8);
        URL url = new URL(projectVersionURL + requestParams);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", agentName + " for '" + projectId + "', (kapitencraft@gmail.com)");

        int response = connection.getResponseCode();

        InputStream dataStream;
        if (response != HttpsURLConnection.HTTP_OK) {
            return null;
        } else {
            dataStream = connection.getInputStream();
        }
        JsonReader reader = new JsonReader(new InputStreamReader(dataStream));

        JsonArray data = (JsonArray) Streams.parse(reader);

        reader.close();
        return JsonHelper.castToObjects(data);
    }
}
