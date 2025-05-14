package net.kapitencraft.kap_lib.data_gen.abst;

import net.kapitencraft.kap_lib.helpers.CollectionHelper;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

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

    public void addItem(RegistryObject<Item> item) {
        addItem(item, TextHelper.makeGrammar(item.getId().getPath()));
    }

    public void bonusWithTranslation(boolean set, String key, String name, String... description) {
        String translationKey = (set ? "set." : "") + "bonus." + modId + "." + key;
        this.add(translationKey, name);
        translation(translationKey, description);
    }

    public void addEnchantmentWithDescription(RegistryObject<Enchantment> enchantment, String... description) {
        String id = enchantment.get().getDescriptionId();
        add(id, TextHelper.makeGrammar(enchantment.getId().getPath()));
        translation(id, description);
    }

    public void addAttribute(RegistryObject<Attribute> attribute, @Nullable ChatFormatting color) {
        String id = attribute.getId().getPath();
        String name = (color != null ? "§" + color.getChar() : "") + TextHelper.makeGrammar(CollectionHelper.getLast(id.split("\\.")));
        add(id, name);
        String descriptionId = attribute.get().getDescriptionId();
        if (!id.equals(descriptionId)) add(descriptionId, name);
    }

    protected void translation(String baseName, String... description) {
        baseName += ".desc";
        for (int i = 0; i < description.length; i++) {
            this.add(baseName + (i == 0 ? "" : "." + i), description[i]);
        }
    }
}
