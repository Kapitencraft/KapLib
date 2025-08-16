package net.kapitencraft.kap_lib.spawn_table.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.kapitencraft.kap_lib.registry.custom.spawn_table.SpawnEntityFunctions;
import net.kapitencraft.kap_lib.spawn_table.SpawnContext;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityConditionalFunction;
import net.kapitencraft.kap_lib.spawn_table.functions.core.SpawnEntityFunctionType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

import java.util.ArrayList;
import java.util.List;

public class CommonPropertiesFunction extends SpawnEntityConditionalFunction {
    public static final MapCodec<CommonPropertiesFunction> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
            Codec.STRING.listOf().fieldOf("properties").forGetter(CommonPropertiesFunction::gatherProperties)
            ).and(commonFields(i).t1())
            .apply(i, CommonPropertiesFunction::new)
    );

    private final boolean noGravity, silent, invulnerable, glowing;

    protected CommonPropertiesFunction(List<String> properties, List<LootItemCondition> pPredicates) {
        super(pPredicates);
        boolean noGravity = false,
                silent = false,
                invulnerable = false,
                glowing = false;
        for (String s : properties) {
            switch (s) {
                case "noGravity" -> noGravity = true;
                case "silent" -> silent = true;
                case "invulnerable" -> invulnerable = true;
                case "glowing" -> glowing = true;
                default -> throw new IllegalArgumentException("unknown property: " + s);
            }
        }
        this.noGravity = noGravity;
        this.silent = silent;
        this.invulnerable = invulnerable;
        this.glowing = glowing;
    }

    @Override
    protected Entity run(Entity pEntity, SpawnContext pContext) {
        if (noGravity) pEntity.setNoGravity(true);
        if (silent) pEntity.setSilent(true);
        if (invulnerable) pEntity.setInvulnerable(true);
        if (glowing) pEntity.setGlowingTag(true);
        return null;
    }

    @Override
    public SpawnEntityFunctionType<?> getType() {
        return SpawnEntityFunctions.COMMON_PROPERTIES.get();
    }

    private List<String> gatherProperties() {
        List<String> l = new ArrayList<>();
        if (noGravity) l.add("noGravity");
        if (silent) l.add("silent");
        if (invulnerable) l.add("invulnerable");
        if (glowing) l.add("glowing");
        return l;
    }
}
