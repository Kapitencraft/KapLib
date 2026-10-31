package net.kapitencraft.kap_lib.component;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ParamStorage {
    private final Map<String, String> params;

    public ParamStorage(Map<String, String> params) {
        this.params = params;
    }

    public @Nullable String getParam(String... paramNames) {
        String p = null;
        for (int i = 0; p == null && i < paramNames.length; i++) {
            p = params.get(paramNames[i]);
        }
        return p;
    }

    public float getFloatParam(float fallback, String... names) {
        String p = getParam(names);
        return p != null ? Float.parseFloat(p) : fallback;
    }
}
