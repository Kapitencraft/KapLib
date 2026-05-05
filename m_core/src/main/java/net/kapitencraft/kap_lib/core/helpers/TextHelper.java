package net.kapitencraft.kap_lib.core.helpers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class TextHelper {
    public static final Component EMPTY = Component.literal("");
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * sends the given Component as the title for the given player
     */
    public static void sendTitle(Player player, Component title) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(title));
        }
    }

    /**
     * chains the given list of Components as you'd when enumerating something
     *
     * @param or whether it is a {@code or} or {@code and} list
     */
    public static Component chain(List<? extends Component> toChain, boolean or) {
        if (toChain.size() == 1) return toChain.getFirst();
        List<Component> copy = new ArrayList<>(toChain);
        MutableComponent component = Component.empty();
        component.append(copy.getFirst());
        copy.removeFirst();
        while (copy.size() > 1) {
            component.append(", ").append(copy.getFirst());
            copy.removeFirst();
        }
        if (or) {
            component.append(Component.translatable("component_chain.or"));
        } else {
            component.append(Component.translatable("component_chain.and"));
        }
        component.append(copy.getFirst());
        return component;
    }

    /**
     * merges the given list using line feed as the separator
     */
    public static Component listToPlainText(List<Component> list) {
        MutableComponent component = Component.empty();
        for (Component c : list) {
            component.append(c);
            component.append("\n");
        }
        return component;
    }

    /**
     * gets the width of the thickest element in the collection or 0 if the collection is empty
     */
    public static float getWidthFromMultiple(Collection<Component> collection, Font font) {
        return collection.stream().mapToInt(font::width).max().orElse(0);
    }

    /**
     * scans the translation files for the given keys each line and applies the given modifiers to the Component before adding it to the list to be returned.
     *
     * @param keyMapper an {@code I -> String} function that converts the list index into a String used for accessing the translation cache
     * @param styleMods nullable style modifiers applied to each Component found by the scanner
     * @param args      optional args applied to each Component
     */
    @Contract("null, _, _ -> fail; _, _, _ -> new")
    public static List<Component> getAllMatchingFilter(Function<Integer, String> keyMapper, @Nullable UnaryOperator<MutableComponent> styleMods, Object... args) {
        List<Component> list = new ArrayList<>();
        int i = 0;
        while (I18n.exists(keyMapper.apply(i))) {
            MutableComponent component = Component.translatable(keyMapper.apply(i), args);
            if (styleMods != null) list.add(styleMods.apply(component));
            else list.add(component);
            i++;
        }
        return list;
    }

    /**
     * returns the description in format {@code <name>.desc<n>} or {@code <name>.description<n>} where name is the given name and {@code <n>} is the index of the translation
     */
    public static List<Component> getDescriptionList(String name, @Nullable UnaryOperator<MutableComponent> styleMods, Object... args) {
        return getAllMatchingFilter(integer -> {
            String descId = name + ".desc";
            if (!I18n.exists(descId)) descId += "ription";
            if (integer != 0) descId += "." + integer;
            return descId;
        }, styleMods, args);
    }

    /**
     * overload for {@link #getDescriptionList(String, UnaryOperator, Object...)} to include the possibility that there is no registered description
     */
    public static List<Component> getDescriptionOrEmpty(String name, @Nullable UnaryOperator<MutableComponent> styleMods, Object... args) {
        List<Component> components = getDescriptionList(name, styleMods, args);
        if (!components.isEmpty()) return components;
        if (styleMods == null) {
            return List.of(Component.translatable("desc.missing"));
        }
        return List.of(styleMods.apply(Component.translatable("desc.missing")));
    }

    /**
     * removes empty lines when lines above are already empty
     */
    public static void removeUnnecessaryEmptyLines(List<Component> components) {
        AtomicReference<Component> previous = new AtomicReference<>();
        components.removeIf(component -> {
            if (component.getString().isEmpty() || component == CommonComponents.EMPTY) {
                Component value = previous.get();
                if (value != null && value.getString().isEmpty() || value == CommonComponents.EMPTY) {
                    return true;
                }
            }
            previous.set(component);
            return false;
        });
    }

    /**
     * @param targetSelector the selector of which entity should get the stack
     * @param stack          the stack to convert into a /give command
     * @return the string that gives any selected target the ItemStack serialized
     */
    public static String createGiveFromStack(String targetSelector, ItemStack stack) {
        return "/give " + targetSelector + " " + BuiltInRegistries.ITEM.getKey(stack.getItem()) + parseDataComponents(stack.getComponentsPatch());
    }

    @ApiStatus.Internal
    private static String parseDataComponents(DataComponentPatch patch) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        patch.entrySet().forEach(entry -> {
            if (entry.getValue().isEmpty() || entry.getKey().isTransient()) return;
            appendComponent(builder, entry, new StringTagVisitor());
        });
        builder.append("]");
        return builder.toString();
    }

    @ApiStatus.Internal
    private static <T> void appendComponent(StringBuilder builder, Map.Entry<DataComponentType<?>, Optional<?>> entry, StringTagVisitor visitor) {
        Codec<T> codec = (Codec<T>) entry.getKey().codecOrThrow();
        DataResult<Tag> parse = codec.encodeStart(NbtOps.INSTANCE, ((T) entry.getValue().get()));
        parse.resultOrPartial(s -> LOGGER.warn("unable to parse component!")).ifPresent(tag -> {
            builder.append(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(entry.getKey()));
            builder.append("=");
            builder.append(visitor.visit(tag));
        });
    }

    /**
     * surrounds the given source with a single obfuscated letter on both sides
     *
     * @param source the component to wrap
     * @return a new component wrapped around the obfuscated letters
     */
    public static MutableComponent wrapInObfuscation(MutableComponent source) {
        return Component.literal("§kA§r ").append(source).append(" §kA§r");
    }

    /**
     * surrounds the given name in name markers (')
     */
    public static String wrapInNameMarkers(String name) {
        return "'" + name + "'";
    }

    /**
     * sets the actionbar message
     */
    public static void setActionbar(Player player, Component display) {
        player.displayClientMessage(display, true);
    }

    /**
     * sets the subtitle to the given Component on the given player
     */
    public static void sendSubTitle(Player player, Component subtitle) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
        }
    }

    /**
     * converts an item to it's ResourceLocation
     */
    public static String getTextId(Item toGet) {
        return BuiltInRegistries.ITEM.getKey(toGet).toString();
    }

    /**
     * converts a ResourceLocation to it's dedicated item
     */
    public static Item getFromId(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

    /**
     * removes the active title from the given player
     */
    public static void clearTitle(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundClearTitlesPacket(true));
        }
    }

    /**
     * @return the index of the last matching chars between the two strings
     */
    public static int getMatchingAmount(String a, String b) {
        int i = 0;
        while (a.length() > i && b.length() > i && a.charAt(i) == b.charAt(i)) {
            i++;
        }
        return i;
    }

    /**
     * creates a String with the given length consisting of random latin characters
     */
    public static String createRandom(int length) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < length; i++) {
            s.append((char) MathHelper.RANDOM_SOURCE.nextInt());
        }
        return s.toString();
    }

    /**
     * converts the given vector into a humanly readable string representation
     */
    public static String fromVec3(Vec3 vec3) {
        return "Pos: [" + vec3.x + ", " + vec3.y + ", " + vec3.z + "]";
    }

    public static String fromBlockPos(BlockPos pos) {
        return fromVec3(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
    }

    /**
     * converts the given {@code snake_case} type string into a humanly readable name
     */
    public static String makeGrammar(String toName) {
        char[] chars = toName.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (i == 0) {
                chars[0] = Character.toUpperCase(chars[0]);
            } else if (chars[i] == '_') {
                chars[i++] = ' ';
                chars[i] = Character.toUpperCase(chars[i]);
            }
        }
        return new String(chars);
    }

    public static String convertId(String name) {
        return name.replace(' ', '_').toLowerCase();
    }

    /**
     * reads a Vec3 directly from a StringReader
     */
    public static Vec3 readVec3(StringReader pReader) throws CommandSyntaxException {
        pReader.expect('(');
        double x = pReader.readDouble();
        double y = pReader.readDouble();
        double z = pReader.readDouble();
        pReader.expect(')');
        return new Vec3(x, y, z);
    }

    //region latin
    private static final List<Pair<Integer, String>> latins = List.of(
            Pair.of(1, "I"),
            Pair.of(5, "V"),
            Pair.of(10, "X"),
            Pair.of(50, "L"),
            Pair.of(100, "C"),
            Pair.of(500, "D"),
            Pair.of(1000, "M")
    );

    private static final Map<Integer, String> latinCache = new HashMap<>();

    /**
     * converts the given number into the same number in latin format
     */
    public static String convertToLatin(int in) {
        if (latinCache.containsKey(in)) return latinCache.get(in);
        StringBuilder s = new StringBuilder();
        while (in > 0) {
            for (int i = latins.size() - 1; i >= 0; i--) {
                Pair<Integer, String> element = latins.get(i);
                if (element.getFirst() <= in) {
                    s.append(element.getSecond());
                    in -= element.getFirst();
                    break;
                } else {
                    for (int i1 = 0; i1 < i; i1 += 2) {
                        Pair<Integer, String> e1 = latins.get(i1);
                        if (element.getFirst() - e1.getFirst() <= in) {
                            s.append(e1.getSecond()).append(element.getSecond());
                            in -= element.getFirst() - e1.getFirst();
                            break;
                        }
                    }
                }
            }
        }
        String latin = s.toString();
        latinCache.put(in, latin);
        return latin;
    }
    //endregion
}