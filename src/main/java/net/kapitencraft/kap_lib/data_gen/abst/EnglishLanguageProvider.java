package net.kapitencraft.kap_lib.data_gen.abst;

import net.kapitencraft.kap_lib.helpers.CollectionHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class EnglishLanguageProvider extends LanguageProvider {
    public EnglishLanguageProvider(PackOutput output, String modid) {
        super(output, modid, "en_us");
    }

    public void addDeathMessage(String msgId, String msg) {
        this.add("death.attack." + msgId, msg);
        this.add("death.attack." + msgId + ".item", msg + " using %3$s");
    }

    public void addItem(RegistryObject<Item> item) {
        addItem(item, filter(item.getId().getPath()));
    }

    public void addEnchantmentWithDescription(RegistryObject<Enchantment> enchantment, String description) {
        String id = enchantment.get().getDescriptionId();
        add(id, filter(enchantment.getId().getPath()));
        add(id + ".desc", description);
    }

    public void addAttribute(RegistryObject<Attribute> attribute, @Nullable ChatFormatting color) {
        String id = attribute.getId().getPath();
        String name = (color != null ? "§" + color.getChar() : "") + filter(CollectionHelper.getLast(id.split("\\.")));
        add(id, name);
        String descriptionId = attribute.get().getDescriptionId();
        if (!id.equals(descriptionId)) add(descriptionId, name);
    }

    private String filter(String toName) {
        String val1 = toName.replace("_", " ");
        char[] chars = val1.toCharArray();
        return fromStrings(makeCapital(fromChars(chars)));
    }

    private String[] fromChars(char[] chars) {
        String[] strings = new String[chars.length];
        for (int i = 0; i < chars.length; i++) {
            strings[i] = String.valueOf(chars[i]);
        }
        return strings;
    }

    private String fromStrings(String[] strings) {
        return String.join("", strings);
    }

    private String[] makeCapital(String[] input) {
        boolean nextCapital = false;
        for (int i = 0; i < input.length; i++) {
            if (Objects.equals(input[i], " ")) {
                nextCapital = true;
            } else if (nextCapital || i == 0){
                input[i] = input[i].toUpperCase();
                nextCapital = false;
            }
        }
        if (nextCapital) {
            String[] toReturn = new String[input.length-1];
            System.arraycopy(input, 0, toReturn, 0, toReturn.length);
            return toReturn;
        }
        return input;
    }
}
