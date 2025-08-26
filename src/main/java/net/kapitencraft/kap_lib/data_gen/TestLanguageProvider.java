package net.kapitencraft.kap_lib.data_gen;

import net.kapitencraft.kap_lib.data_gen.abst.EnglishLanguageProvider;
import net.minecraft.data.PackOutput;

public class TestLanguageProvider extends EnglishLanguageProvider {
    public TestLanguageProvider(PackOutput output) {
        super(output, "test");
    }

    @Override
    protected void addTranslations() {
        this.add("set.bonus.kap_lib.attributes", "Test Bonus");
        this.add("set.bonus.kap_lib.attributes.desc", "give 20 Luck");
    }

    @Override
    public String getName() {
        return "TEST_LANGUAGE";
    }
}
