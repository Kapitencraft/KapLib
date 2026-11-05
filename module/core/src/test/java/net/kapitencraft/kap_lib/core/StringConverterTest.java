package net.kapitencraft.kap_lib.core;

import net.kapitencraft.kap_lib.core.string_converter.converter.TextToDoubleConverter;
import net.kapitencraft.kap_lib.core.string_converter.converter.TextToIntConverter;
import net.kapitencraft.kap_lib.core.string_converter.param_storage.ParamStorage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class StringConverterTest {

    @Test
    public void test() {
        TextToIntConverter c = new TextToIntConverter("l * l");
        Assertions.assertEquals(4, c.transfer(new ParamStorage<>(Map.of("l", 2))));
        TextToDoubleConverter cD = new TextToDoubleConverter("l ^ 3");
        Assertions.assertEquals(27d, cD.transfer(new ParamStorage<>(Map.of("l", 3d))));
    }
}
