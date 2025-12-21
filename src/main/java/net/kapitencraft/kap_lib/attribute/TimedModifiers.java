package net.kapitencraft.kap_lib.attribute;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

import java.util.*;

public class TimedModifiers {
    public static final Codec<TimedModifiers> CODEC = Entry.CODEC.listOf().xmap(TimedModifiers::new, m -> m.modifiers);

    private final List<Entry> modifiers = new ArrayList<>();

    public TimedModifiers() {
    }

    private TimedModifiers(List<Entry> entries) {
        this.modifiers.addAll(entries);
    }

    public static TimedModifiers get(LivingEntity living) {
        return living.getData(AttributeAttachmentTypes.TIMED_MODIFIERS.get());
    }

    public void tick(LivingEntity living) {
        List<Entry> toRemove = new ArrayList<>();
        modifiers.forEach((entry) -> {
            if (entry.tick()) {
                toRemove.add(entry);
            }
        });
        AttributeMap attributes = living.getAttributes();
        toRemove.forEach(entry -> Objects.requireNonNull(attributes.getInstance(entry.holder), "missing attribute " + entry.holder.getKey() + " on entity").removeModifier(entry.location));
        modifiers.removeAll(toRemove);
    }

    public void add(int duration, Holder<Attribute> attribute, ResourceLocation location) {
        this.modifiers.add(new Entry(duration, attribute, location));
    }

    private static class Entry {
        private static final Codec<Entry> CODEC = RecordCodecBuilder.create(i -> i.group(
                Codec.INT.fieldOf("duration").forGetter(e -> e.duration),
                BuiltInRegistries.ATTRIBUTE.holderByNameCodec().fieldOf("attribute").forGetter(e -> e.holder),
                ResourceLocation.CODEC.fieldOf("location").forGetter(e -> e.location)
        ).apply(i, Entry::new));

        private int duration;
        private final Holder<Attribute> holder;
        private final ResourceLocation location;

        private Entry(int duration, Holder<Attribute> holder, ResourceLocation location) {
            this.duration = duration;
            this.holder = holder;
            this.location = location;
        }

        private boolean tick() {
            return duration-- < 1;
        }
    }
}
