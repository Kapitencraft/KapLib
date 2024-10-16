package net.kapitencraft.kap_lib.data_gen;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.List;

public class ExtraNumbersLangProvider extends LanguageProvider {
    public ExtraNumbersLangProvider(PackOutput output) {
        super(output, "extra_numbers", "en_us");
    }

    @Override
    protected void addTranslations() {
        for (int i = 6; i < 255; i++) {
            add("potion.potency." + i, makeLatin(i+1));
        }

        for (int i = 11; i < 255; i++) {
            add("enchantment.level." + i, makeLatin(i));
        }
    }

    static final List<Pair<Integer, String>> latins = List.of(
            Pair.of(1, "I"),
            Pair.of(5, "V"),
            Pair.of(10, "X"),
            Pair.of(50, "L"),
            Pair.of(100, "C"),
            Pair.of(500, "D"),
            Pair.of(1000, "M")
    );

    private static String makeLatin(int in) {
        StringBuilder s = new StringBuilder();
        while (in > 0) {
            for (int i = latins.size()-1; i >= 0; i--) {
                Pair<Integer, String> element = latins.get(i);
                if (element.getFirst() <= in) {
                    s.append(element.getSecond());
                    in -= element.getFirst();
                    break;
                } else {
                    for (int i1 = 0; i1 < i; i1+=2) {
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
        return s.toString();
    }
}
