package net.kapitencraft.kap_lib.core.patch;

import net.kapitencraft.kap_lib.core.LibConstants;
import net.kapitencraft.kap_lib.core.helpers.IOHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTagVisitor;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

public interface CompoundTagPatch {

    /**
     * @return if the tag contains the given id, and it's an Integer type, and it's value is > 0
     */
    default boolean checkForIntAbove0(String name) {
        CompoundTag tag = (CompoundTag) this;
        return tag.contains(name, 3) && tag.getInt(name) > 0;
    }

    /**
     * increase the given tag's float element found under name
     * @return the new set value
     */
    default float increaseFloatTagValue(String name, float f) {
        CompoundTag tag = (CompoundTag) this;
        float value = tag.getFloat(name)+f;
        tag.putFloat(name, value);
        return value;
    }

    /**
     * increases the given tag element
     * @see IOHelper#increaseFloatTagValue(CompoundTag, String, float)
     */
    default int increaseIntegerTagValue(String name, int i) {
        CompoundTag tag = (CompoundTag) this;
        int value = tag.getInt(name)+i;
        tag.putInt(name, value);
        return value;
    }

    /**
     * increases the given tag element if the element's value is above 0
     */
    default int increaseIntOnlyAbove0(String name, int i) {
        if (checkForIntAbove0(name)) {
            return increaseIntegerTagValue(name, i);
        }
        CompoundTag tag = (CompoundTag) this;
        return tag.getInt(name);
    }

    /**
     * reduces the given int tag element by 1
     */
    default int reduceBy1(String name) {
        return increaseIntOnlyAbove0(name, -1);
    }

    /**
     * convert a CompoundTag into a string
     */
    default String stringify() {
        return new StringTagVisitor().visit(((CompoundTag) this));
    }

    /**
     * gets or creates a new entry in the given Tag with the given name
     */
    default CompoundTag getOrCreateCompound(String name) {
        CompoundTag tag = (CompoundTag) this;
        if (tag.contains(name, 10)) return tag.getCompound(name);
        CompoundTag data = new CompoundTag();
        tag.put(name, data);
        return data;
    }

    /**
     * gets or creates a new list in the given Tag with the given name and type
     */
    default ListTag getOrCreateList(String name, int listType) {
        CompoundTag tag = (CompoundTag) this;
        if (tag.contains(name, Tag.TAG_LIST)) return tag.getList(name, listType);
        ListTag listTag = new ListTag();
        tag.put(name, listTag);
        return listTag;
    }
}
