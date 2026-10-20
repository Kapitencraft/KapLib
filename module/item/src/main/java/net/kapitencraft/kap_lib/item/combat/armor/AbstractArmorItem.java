package net.kapitencraft.kap_lib.item.combat.armor;

import net.kapitencraft.kap_lib.item.creative_tab.TabGroup;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * basic armor item.
 */
public abstract class AbstractArmorItem extends ArmorItem {

    @SuppressWarnings("unused")
    protected AbstractArmorItem(Holder<ArmorMaterial> pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }

    /**
     * checks if the given entity has a fullset of this item
     *
     * @param living the given entity
     * @return whether there is a fullset or not
     */
    @SuppressWarnings("unused")
    public boolean isFullSetActive(LivingEntity living) {
        return isFullSetActive(living, this.getMaterial());
    }

    /**
     * checks if the given entity has a fullset of the given material
     *
     * @param living   the given entity
     * @param material the material to check for
     * @return whether the given entity has a fullset or not
     */
    public static boolean isFullSetActive(LivingEntity living, Holder<ArmorMaterial> material) {
        if (living == null) {
            return false;
        }
        ArmorItem head = living.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof ArmorItem armorItem ? armorItem : null;
        Item chestPlate = living.getItemBySlot(EquipmentSlot.CHEST).getItem();
        ArmorItem chest;
        if (chestPlate instanceof ElytraItem || chestPlate instanceof AirItem) {
            return false;
        } else {
            chest = (ArmorItem) living.getItemBySlot(EquipmentSlot.CHEST).getItem();
        }
        ArmorItem legs = living.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof ArmorItem armorItem ? armorItem : null;
        ArmorItem feet = living.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof ArmorItem armorItem ? armorItem : null;
        return (head != null && legs != null && feet != null) && (head.getMaterial() == material && chest.getMaterial() == material && legs.getMaterial() == material && feet.getMaterial() == material);
    }

    //region display / model

    /**
     * creates a custom texture for your armor in
     * <br>{@code <nameSpace>:textures/models/armor/custom/<id>.png}
     */
    @SuppressWarnings("unused")
    public static ResourceLocation makeCustomTextureLocation(String nameSpace, String id) {
        return ResourceLocation.fromNamespaceAndPath(nameSpace, "textures/models/armor/custom/" + id + ".png");
    }

    /**
     * creates a custom texture for your armor in
     * <br>{@code <nameSpace>:textures/models/armor/custom/<id><layer_suffix>.png}
     * the layers' suffix is used to differentiate between textures when using different layers, for example when using tinting
     */
    @SuppressWarnings("unused")
    public static ResourceLocation makeCustomLayeredTextureLocation(String nameSpace, String id, ArmorMaterial.Layer layer) {
        return ResourceLocation.fromNamespaceAndPath(nameSpace, "textures/models/armor/custom/" + id + layer + ".png");
    }

    //endregion

    /**
     * creates a type mapped registry entry for the given armor
     *
     * @param registry the Register to add to
     * @param baseName the base name of the armor
     * @param creator  a lambda function to create an instance of the armor, mostly a method reference to the constructor
     * @param group    the tab group to register there
     * @param <T>      armor item class type
     * @return a Map mapping the ArmorType to the RegObj for the slot
     */
    @SuppressWarnings("unused")
    public static <T extends AbstractArmorItem> Map<Type, DeferredItem<T>> createRegistry(DeferredRegister.Items registry, String baseName, Function<Type, T> creator, @Nullable TabGroup group) {
        return Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            for (Type type : Type.values()) {
                if (type == Type.BODY) continue; //ignore body
                DeferredItem<T> object = registry.register(baseName + "_" + type.getName(), () -> creator.apply(type));
                if (group != null) group.add(object);
                map.put(type, object);
            }
        });
    }
}