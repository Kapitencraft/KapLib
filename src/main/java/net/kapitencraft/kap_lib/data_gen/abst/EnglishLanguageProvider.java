package net.kapitencraft.kap_lib.data_gen.abst;

import net.kapitencraft.kap_lib.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.data.LanguageProvider;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public abstract class EnglishLanguageProvider extends LanguageProvider {
    /**
     * if FML would have just made it protected like any sane person would
     */
    private final String modId;

    public EnglishLanguageProvider(PackOutput output, String modId) {
        super(output, modId, "en_us");
        this.modId = modId;
    }

    public void addDeathMessage(String msgId, String msg) {
        this.add("death.attack." + msgId, msg);
        this.add("death.attack." + msgId + ".item", msg + " using %3$s");
    }

    public void addItem(Supplier<Item> item) {
        addItem(item, TextHelper.makeGrammar(BuiltInRegistries.ITEM.getKey(item.get()).getPath()));
    }

    public void bonusWithTranslation(boolean set, String key, String name, String... description) {
        String translationKey = (set ? "set." : "") + "bonus." + modId + "." + key;
        this.add(translationKey, name);
        translation(translationKey, description);
    }

    public void addEnchantmentWithDescription(Holder<Enchantment> enchantment, String... description) {
        ResourceLocation location = enchantment.getKey().location();
        String id = Util.makeDescriptionId("enchantment", location);
        add(id, TextHelper.makeGrammar(location.getPath()));
        translation(id, description);
    }

    public void addAttribute(Holder<Attribute> attribute, @Nullable ChatFormatting color) {
        String id = attribute.getKey().location().getPath();
        String name = (color != null ? "§" + color.getChar() : "") + TextHelper.makeGrammar(CollectionHelper.getLast(id.split("\\.")));
        add(id, name);
        String descriptionId = attribute.value().getDescriptionId();
        if (!id.equals(descriptionId)) add(descriptionId, name);
    }

    protected void translation(String baseName, String... description) {
        baseName += ".desc";
        for (int i = 0; i < description.length; i++) {
            this.add(baseName + (i == 0 ? "" : "." + i), description[i]);
        }
    }
}
