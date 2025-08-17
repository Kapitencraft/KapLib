package net.kapitencraft.kap_lib.helpers;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kapitencraft.kap_lib.util.Reference;
import net.kapitencraft.kap_lib.util.Vec2i;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.rmi.MarshalledObject;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class TextHelper {
    public static final Component EMPTY = Component.literal("");

    public static void sendTitle(Player player, Component title) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetTitleTextPacket(title));
        }
    }

    public static Component chain(List<? extends Component> toChain, boolean or) {
        if (toChain.size() == 1) return toChain.get(0);
        List<Component> copy = new ArrayList<>(toChain);
        MutableComponent component = Component.empty();
        component.append(copy.get(0));
        copy.remove(0);
        while (copy.size() > 1) {
            component.append(", ").append(copy.get(0));
            copy.remove(0);
        }
        if (or) {
            component.append(Component.translatable("component_chain.or"));
        } else {
            component.append(Component.translatable("component_chain.and"));
        }
        component.append(copy.get(0));
        return component;
    }

    public static Component listToPlainText(List<Component> list) {
        MutableComponent component = Component.empty();
        for (Component c : list) {
            component.append(c);
            component.append("\n");
        }
        return component;
    }

    public static float getWidthFromMultiple(Collection<Component> collection, Font font) {
        float f = 0;
        for (Component comp : collection) {
            float f1 = font.width(comp);
            if (f < f1) f = f1;
        }
        return f;
    }

    public static void addEmpty(List<Component> components) {
        components.add(CommonComponents.EMPTY);
    }


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

    public static List<Component> getDescriptionList(String name, @Nullable UnaryOperator<MutableComponent> styleMods, Object... args) {
        return getAllMatchingFilter(integer -> {
            String descId = name + ".desc";
            if (!I18n.exists(descId)) descId += "ription";
            if (integer != 0) descId += "." + integer;
            return descId;
        }, styleMods, args);
    }

    public static List<Component> getDescriptionOrEmpty(String name, @Nullable UnaryOperator<MutableComponent> styleMods, Object... args) {
        List<Component> components = getDescriptionList(name, styleMods, args);
        return components.isEmpty() ? List.of(Component.translatable("desc.missing")) : components;
    }

    public static String wrapInObfuscation(String source) {
        return wrapInFormatting(source, ChatFormatting.OBFUSCATED);
    }

    public static String wrapInFormatting(Object source, ChatFormatting formatting) {
        char id = formatting.getChar();
        boolean obfuscate = id == 'k';
        StringBuilder builder = new StringBuilder();
        String format = "§" + id;
        builder.append(format);
        if (obfuscate) builder.append("k§r ");
        builder.append(source);
        if (obfuscate) builder.append(" §kk");
        builder.append("§r");
        return builder.toString();
    }

    public static String mergeRegister(String a, String b) {
        return a + "_" + b;
    }

    public static String swappedMergeRegister(String a, String b) {
        return mergeRegister(b, a);
    }

    public static void removeUnnecessaryEmptyLines(List<Component> components) {
        Reference<Component> reference = Reference.of(null);
        components.removeIf(component -> {
            if (component.getString().equals("") || component == CommonComponents.EMPTY) {
                Component value = reference.getValue();
                if (value != null && value.getString().equals("") || value == CommonComponents.EMPTY) {
                    return true;
                }
            }
            reference.setValue(component);
            return false;
        });
    }

    /**
     *
     * @param targetSelector the selector of which entity should get the stack
     * @param stack the stack to convert into a /give command
     * @return the string that gives any selected target the ItemStack serialized
     */
    public static String createGiveFromStack(String targetSelector, ItemStack stack) {
        return "/give " + targetSelector + " " + BuiltInRegistries.ITEM.getKey(stack.getItem()) + stack.getOrCreateTag(); //TODO do components
    }

    /**
     * surrounds the given source with a single obfuscated letter on both sides
     * @param source the component to wrap
     * @return a new component wrapped around the obfuscated letters
     */
    public static MutableComponent wrapInObfuscation(MutableComponent source) {
        return Component.literal("§kA§r ").append(source).append(" §kA§r");
    }

    public static String wrapInNameMarkers(String name) {
        return "'" + name + "'";
    }

    public static String wrapInRed(Object toWrap) {
        return wrapInFormatting(toWrap, ChatFormatting.RED);
    }

    public static void setHotbarDisplay(Player player, Component display) {
        player.displayClientMessage(display, true);
    }


    public static void sendSubTitle(Player player, Component subtitle) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.send(new ClientboundSetSubtitleTextPacket(subtitle));
        }
    }

    public static String getTextId(Item toGet) {
        return BuiltInRegistries.ITEM.getKey(toGet).toString();
    }

    public static Item getFromId(String id) {
        return BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
    }

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

    private static final List<String> NUMBERS = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
    private static final List<String> LETTERS_SMALL = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z");
    private static final List<String> LETTERS_BIG = LETTERS_SMALL.stream().map(String::toUpperCase).toList();

    public static String removeNumbers(String string) {
        for (String number : NUMBERS) {
            string = string.replace(number, "");
        }
        return string;
    }

    public static String createRandom(int length) {
        StringBuilder s = new StringBuilder();
        final List<String> ALL_DEFAULT = new ArrayList<>();
        ALL_DEFAULT.addAll(NUMBERS);
        ALL_DEFAULT.addAll(LETTERS_SMALL);
        ALL_DEFAULT.addAll(LETTERS_BIG);
        for (int i = 0; i < length; i++) {
            s.append(MathHelper.pickRandom(ALL_DEFAULT));
        }
        return s.toString();
    }

    public static ChatFormatting damageIndicatorColorGenerator(String type) {
        return switch (type) {
            case "heal" -> ChatFormatting.GREEN;
            case "wither" -> ChatFormatting.BLACK;
            case "ferocity" -> ChatFormatting.GOLD;
            case "drown" -> ChatFormatting.AQUA;
            case "ability", "fire" -> ChatFormatting.DARK_RED;
            case "dodge" -> ChatFormatting.DARK_GRAY;
            default -> ChatFormatting.RED;
        };
    }

    public static @NotNull ChatFormatting damageIndicatorColorFromDouble(double in) {
        return damageIndicatorColorGenerator(damageIndicatorDecoder(in));
    }

    public static String damageIndicatorDecoder(double in) {
        return switch ((int) in) {
            case 1 -> "heal";
            case 2 -> "wither";
            case 3 -> "ferocity";
            case 4 -> "drown";
            case 5 -> "ability";
            case 6 -> "dodge";
            case 7 -> "fire";
            default -> "normal";
        };
    }

    public static int damageIndicatorCoder(String id) {
        return switch (id) {
            case "heal" -> 1;
            case "wither" -> 2;
            case "ferocity" -> 3;
            case "drown" -> 4;
            case "ability" -> 5;
            case "dodge" -> 6;
            case "fire" -> 7;
            default -> 0;
        };
    }

    public static String fromVec3(Vec3 vec3) {
        return "Pos: [" + vec3.x + ", " + vec3.y + ", " + vec3.z + "]";
    }

    public static String fromBlockPos(BlockPos pos) {
        return fromVec3(new Vec3(pos.getX(), pos.getY(), pos.getZ()));
    }

    public static <T, K> T makeList(List<K> toMerge, Supplier<T> generator, Function<K, T> transfer, BiConsumer<T, T> useConsumer) {
        T t = generator.get();
        for (K k : toMerge) {
            T transferred = transfer.apply(k);
            useConsumer.accept(t, transferred);
        }
        return t;
    }

    public static <T> String makeList(List<T> toMerge, Function<T, String> provider) {
        StringBuilder builder = new StringBuilder();
        for (T t : toMerge) {
            String s = provider.apply(t);
            builder.append(s);
            if (t != toMerge.get(toMerge.size() - 1)) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    public static String makeGrammar(String toName) {
        String val1 = toName.replace("_", " ");
        char[] chars = val1.toCharArray();
        return fromStrings(makeCapital(fromChars(chars)));
    }

    private static String[] fromChars(char[] chars) {
        String[] strings = new String[chars.length];
        for (int i = 0; i < chars.length; i++) {
            strings[i] = String.valueOf(chars[i]);
        }
        return strings;
    }

    private static String fromStrings(String[] strings) {
        StringBuilder builder = new StringBuilder();
        for (String s : strings) {
            builder.append(s);
        }
        return builder.toString();
    }

    private static String[] makeCapital(String[] input) {
        boolean nextCapital = false;
        for (int i = 0; i < input.length; i++) {
            if (Objects.equals(input[i], " ")) {
                nextCapital = true;
            } else if (nextCapital || i == 0) {
                input[i] = input[i].toUpperCase();
                nextCapital = false;
            }
        }
        if (nextCapital) {
            String[] toReturn = new String[input.length - 1];
            System.arraycopy(input, 0, toReturn, 0, toReturn.length);
            return toReturn;
        }
        return input;
    }

    public static Vec3 readVec3(StringReader pReader) throws CommandSyntaxException {
        pReader.expect('(');
        double x = pReader.readDouble();
        double y = pReader.readDouble();
        double z = pReader.readDouble();
        pReader.expect(')');
        return new Vec3(x, y, z);
    }
}