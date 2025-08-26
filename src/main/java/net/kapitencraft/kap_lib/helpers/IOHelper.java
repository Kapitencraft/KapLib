package net.kapitencraft.kap_lib.helpers;

import com.google.gson.*;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.kapitencraft.kap_lib.KapLibMod;
import net.kapitencraft.kap_lib.collection.MapStream;
import net.kapitencraft.kap_lib.io.JsonHelper;
import net.kapitencraft.kap_lib.io.StringSegment;
import net.kapitencraft.kap_lib.stream.Consumers;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class IOHelper {
    private static final String LENGTH_ID = "Length";


    /**
     * @return if the tag contains the given id, and it's an Integer type, and it's value is > 0
     */
    public static boolean checkForIntAbove0(CompoundTag tag, String name) {
        return tag.contains(name, 3) && tag.getInt(name) > 0;
    }


    /**
     * @return if the tag is null or empty
     */
    public static boolean isTagEmpty(@Nullable CompoundTag tag) {
        return tag == null || tag.isEmpty();
    }

    /**
     * increase the given tag's float element found under name
     * @return the new set value
     */
    public static float increaseFloatTagValue(CompoundTag tag, String name, float f) {
        float value = tag.getFloat(name)+f;
        tag.putFloat(name, value);
        return value;
    }

    /**
     * get the data result or the defaulted if the data result is empty
     */
    public static <T> T orElse(DataResult<T> result, @NotNull Supplier<T> defaulted) {
        Optional<T> optional = result.result();
        return optional.orElseGet(defaulted);
    }

    /**
     * creates or gets the given file and apply the given codec to the content or get the defaulted if
     */
    @SuppressWarnings("all")
    public static <T> T loadFile(File file, Codec<T> codec, Supplier<T> defaulted) {
        try {
            if (!file.exists()) return defaulted.get();

            return orElse(codec.parse(JsonOps.INSTANCE, Streams.parse(createReader(file))), defaulted);
        } catch (IOException e) {
            KapLibMod.LOGGER.warn("unable to load file: " + file.getPath());
        }
        return defaulted.get();
    }

    @SuppressWarnings("all")
    public static <T> T loadOrCreateFile(File file, Codec<T> codec, Supplier<T> defaulted) {
        try {
            if (!file.exists()) {
                saveFile(file, codec, defaulted.get());
                return defaulted.get();
            }
            return orElse(codec.parse(JsonOps.INSTANCE, Streams.parse(createReader(file))), defaulted);
        } catch (IOException e) {
        }
        return defaulted.get();
    }

    /**
     * creates the given file if it doesn't exist
     */
    @SuppressWarnings("all")
    private static void createFile(File file) {
        try {
            file.getParentFile().mkdirs();
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            KapLibMod.LOGGER.warn("unable to create file '{}': {}", file.getName(),  e.getMessage());
        }
    }

    /**
     * create a JsonReader of the given file
     * @throws FileNotFoundException if the file doesn't exist
     */
    private static JsonReader createReader(File file) throws FileNotFoundException {
        return new JsonReader(new FileReader(file));
    }

    /**
     * save the given element to the file, using the given codec
     * <br> will try to create the file if it doesn't already exist
     */
    @SuppressWarnings("all")
    public static <T> void saveFile(File file, Codec<T> codec, T in) {
        try {
            createFile(file);
            FileWriter writer = new FileWriter(file);
            writer.write(JsonHelper.GSON.toJson(orElse(codec.encodeStart(JsonOps.INSTANCE, in), JsonObject::new)));
            writer.close();
        } catch (Exception e) {
            KapLibMod.LOGGER.warn("unable to save file: {}", e.getMessage());
        }
    }

    /**
     * increases the given tag element
     * @see IOHelper#increaseFloatTagValue(CompoundTag, String, float)
     */
    public static int increaseIntegerTagValue(CompoundTag tag, String name, int i) {
        int value = tag.getInt(name)+i;
        tag.putInt(name, value);
        return value;
    }

    /**
     * increases the given tag element if the element's value is above 0
     */
    public static int increaseIntOnlyAbove0(CompoundTag tag, String name, int i) {
        if (checkForIntAbove0(tag, name)) {
            return increaseIntegerTagValue(tag, name, i);
        }
        return tag.getInt(name);
    }

    /**
     * reduces the given int tag element by 1
     */
    public static int reduceBy1(CompoundTag tag, String name) {
        return increaseIntOnlyAbove0(tag, name, -1);
    }

    /**
     * create a UUID -> Integer map compoundtag implementation
     */
    public static @NotNull CompoundTag putHashMapTag(@NotNull HashMap<UUID, Integer> hashMap) {
        CompoundTag mapTag = new CompoundTag();
        List<Integer> IntArray = CollectionHelper.fromAny(hashMap.values());
        mapTag.put("Uuids", putUuidList(CollectionHelper.fromAny(hashMap.keySet())));
        mapTag.putIntArray("Ints", IntArray);
        mapTag.putInt(LENGTH_ID, hashMap.size());
        return mapTag;
    }

    /**
     * merge the CompoundTag into the given entity
     */
    @SuppressWarnings("ALL")
    public static void injectCompoundTag(Entity toInject, CompoundTag tag) {
        CompoundTag data = toInject.getPersistentData();
        Set<String> allKeys = tag.getAllKeys();
        allKeys.forEach(s -> {
            if (tag.get(s) != null) {
                if (tag.get(s) instanceof CompoundTag cTag)
                    injectCompoundTag(toInject, cTag);
                else data.put(s, tag.get(s));
            }
        });
    }

    /**
     * convert a CompoundTag into a string
     */
    public static String fromCompoundTag(CompoundTag tag) {
        return new StringTagVisitor().visit(tag);
    }

    /**
     * read a CompoundTag list from the given tag
     */
    public static Stream<CompoundTag> readCompoundList(CompoundTag tag, String name) {
        return readList(tag, name, CompoundTag.class, Function.identity(), 10);
    }

    public static <K, T extends Tag> Stream<K> readList(CompoundTag tag, String name, Class<T> tClass, Function<T, K> creator, int elementId) {
        ListTag listTag = tag.getList(name, elementId);
        return listTag.stream().filter(tClass::isInstance)
                .map(tClass::cast)
                .map(creator);
    }

    public static CompoundTag fromString(String s) {
        try {
            return new TagParser(new StringReader(s)).readStruct();
        } catch (CommandSyntaxException e) {
            KapLibMod.LOGGER.warn("unable to read Tag '{}': {}", s, e.getMessage());
            return new CompoundTag();
        }
    }


    public static @NotNull HashMap<UUID, Integer> getHashMapTag(@Nullable CompoundTag tag) {
        HashMap<UUID, Integer> hashMap = new HashMap<>();
        if (tag == null) {
            return hashMap;
        }
        int[] intArray = tag.getIntArray("Ints");
        UUID[] UuidArray = getUuidArray(tag.getCompound("Uuids"));
        if (UuidArray != null) {
            for (int i = 0; i < (intArray.length == UuidArray.length ? intArray.length : 0); i++) {
                hashMap.put(UuidArray[i], intArray[i]);
            }
        }
        return hashMap;
    }

    public static UUID[] getUuidArray(CompoundTag arrayTag) {
        if (!arrayTag.contains(LENGTH_ID)) {
            KapLibMod.LOGGER.warn("tried to load UUID Array from Tag but Tag isn`t Array Tag");
        } else {
            int length = arrayTag.getInt(LENGTH_ID);
            UUID[] array = new UUID[length];
            for (int i = 0; i < length; i++) {
                array[i] = arrayTag.getUUID(String.valueOf(i));
            }
            return array;
        }
        return null;
    }

    public static CompoundTag putUuidList(List<UUID> list) {
        CompoundTag arrayTag = new CompoundTag();
        for (int i = 0; i < list.size(); i++) {
            arrayTag.putUUID(String.valueOf(i), list.get(i));
        }
        arrayTag.putInt(LENGTH_ID, list.size());
        return arrayTag;
    }

    /**
     * write a map to a list tag for data-saving
     * @param map the map to write
     * @param keyWriter the key writer
     * @param valueWriter the value writer
     * @return a list tag containing the written map
     * @see IOHelper#readMap(ListTag, BiFunction, BiFunction) readMap
     */
    public static <K, V> ListTag writeMap(Map<K, V> map, DataWriter<K> keyWriter, DataWriter<V> valueWriter) {
        ListTag listTag = new ListTag();
        map.forEach((k, v) -> {
            CompoundTag tag = new CompoundTag();
            tag.put("Key", keyWriter.createData(k));
            tag.put("Value", valueWriter.createData(v));
            listTag.add(tag);
        });
        return listTag;
    }

    public static CompoundTag getOrCreateTag(CompoundTag tag, String name) {
        if (tag.contains(name, 10)) return tag.getCompound(name);
        CompoundTag data = new CompoundTag();
        tag.put(name, data);
        return data;
    }

    @FunctionalInterface
    public interface DataWriter<T> {
        Tag createData(T t);
    }

    /**
     * @param tag the tag in which the map is saved
     * @param keyExtractor the function that extracts each key out of their entry
     * @param valueExtractor the function that extracts each value out of their entry
     * @return a MapStream containing all extracted map entries
     */
    public static <K, V> MapStream<K, V> readMap(ListTag tag, BiFunction<CompoundTag, String, K> keyExtractor, BiFunction<CompoundTag, String, V> valueExtractor) {
        HashMap<K, V> map = new HashMap<>();
        tag.stream()
                .filter(CompoundTag.class::isInstance)
                .map(CompoundTag.class::cast)
                .forEach(tag1 -> map.put(keyExtractor.apply(tag1, "Key"), valueExtractor.apply(tag1, "Value")));
        return MapStream.of(map);
    }

    /**
     * @param map the map to write
     * @param keyMapper a function to convert each key of the map into an JsonElement
     * @param valueMapper a function to convert each value of the map into an JsonElement
     * @return an JsonArray containing each entry of the map
     * @see IOHelper#readMap(JsonArray, BiFunction, BiFunction)
     */
    public static <K, V> JsonArray writeMap(Map<K, V> map, Function<K, JsonElement> keyMapper, Function<V, JsonElement> valueMapper) {
        JsonArray array = new JsonArray(map.size());
        map.forEach((k, v) -> {
            JsonObject object = new JsonObject();
            object.add("key", keyMapper.apply(k));
            object.add("value", valueMapper.apply(v));
        });
        return array;
    }

    /**
     * @param array the json array containing all map entries
     * @param keyExtractor a function that extracts each map key out of the entry
     * @param valueExtractor a function that extracts each map value out of the entry
     * @return a MapStream containing all the entries extracted
     */
    public static <K, V> MapStream<K, V> readMap(JsonArray array, BiFunction<JsonObject, String, K> keyExtractor, BiFunction<JsonObject, String, V> valueExtractor) {
        HashMap<K, V> map = new HashMap<>();
        JsonHelper.castToObjects(array)
                .forEach(object -> map.put(keyExtractor.apply(object, "key"), valueExtractor.apply(object, "value")));
        return MapStream.of(map);
    }

    public static <K, V> MapStream<K, V> readMap(JsonArray array, Function<JsonElement, K> keyMapper, Function<JsonElement, V> valueMapper) {
        return readMap(array, (object, string) -> keyMapper.apply(object.get(string)), (object, string) -> valueMapper.apply(object.get(string)));
    }


    public static <T> ListTag writeList(List<T> list, Function<T, Tag> mapper) {
        ListTag tag = new ListTag();
        list.stream().map(mapper).forEach(tag::add);
        return tag;
    }

    public static class TagBuilder {
        private final CompoundTag tag = new CompoundTag();

        public static TagBuilder create() {
            return new TagBuilder();
        }

        public <T> TagBuilder withArg(String name, T value, Consumers.C3<CompoundTag, String, T> consumer) {
            consumer.apply(tag, name, value);
            return this;
        }

        public TagBuilder withString(String name, String val) {
            return withArg(name, val, CompoundTag::putString);
        }

        public TagBuilder withUUID(String name, UUID val) {
            return withArg(name, val, CompoundTag::putUUID);
        }

        public CompoundTag build() {
            return tag;
        }
    }

    public static List<File> listResources(File file) {
        if (!file.exists()) return List.of();
        if (!file.isDirectory()) return List.of(file);
        List<File> finals = new ArrayList<>();
        List<File> queue = new ArrayList<>();
        queue.add(file);
        while (!queue.isEmpty()) {
            if (queue.get(0).isDirectory()) {
                String[] childNames = queue.get(0).list();
                if (childNames != null) for (String childName : childNames) {
                    queue.add(new File(queue.get(0), childName));
                }
            } else {
                finals.add(queue.get(0));
            }
            queue.remove(0);
        }
        return finals;
    }

    public static List<ResourceLocation> toNames(List<File> files) {
        File file = new File("test");
        return files.stream().map(File::getPath).map(s -> {
            String[] directories = s.split("\\\\"); //why 4 bro?
            return new ResourceLocation("a");
        }).toList();
    }

    /**
     * @param in the String to check
     * @param openRegex the char that opens the bracket
     * @param closeRegex the char that closes the bracket
     * @return a list of StringSegments that contain the start and end position and the substring inclusive the brackets
     */
    public static List<StringSegment> collectBracketContent(String in, String openRegex, String closeRegex) {
        List<StringSegment> strings = new ArrayList<>();
        Matcher openMatcher = Pattern.compile(openRegex).matcher(in);
        Matcher closeMatcher = Pattern.compile(closeRegex).matcher(in);
        for (int i = 0; i < in.length(); i++) {
            if (openMatcher.find(i) && closeMatcher.find(openMatcher.end())) {
                strings.add(StringSegment.fromString(openMatcher.start(), closeMatcher.end(), in));
            }
        }
        return strings;
    }

}
