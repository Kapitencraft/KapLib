package net.kapitencraft.kap_lib.util.string_converter.param_storage;

import java.util.HashMap;
import java.util.Map;

public class ParamStorage<T> {

    private final Map<String, T> params = new HashMap<>();

    public ParamStorage() {
    }

    public ParamStorage(Map<String, T> map) {
        params.putAll(map);
    }


    public T get(String id) {
        return params.get(id);
    }

    public boolean contains(String id) {
        return params.containsKey(id);
    }
}