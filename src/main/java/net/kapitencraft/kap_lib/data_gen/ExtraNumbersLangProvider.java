package net.kapitencraft.kap_lib.data_gen;

import com.mojang.datafixers.util.Pair;
import net.kapitencraft.kap_lib.helpers.TextHelper;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.List;

public class ExtraNumbersLangProvider extends LanguageProvider {
    public ExtraNumbersLangProvider(PackOutput output) {
        super(output, "extra_numbers", "en_us");
    }

    @Override
    protected void addTranslations() {
        for (int i = 6; i < 255; i++) {
            add("potion.potency." + i, TextHelper.convertToLatin(i+1));
        }

        for (int i = 11; i < 255; i++) {
            add("enchantment.level." + i, TextHelper.convertToLatin(i));
        }
    }



    @Override
    public String getName() {
        return "EXTRA_NUMBERS";
    }
}
