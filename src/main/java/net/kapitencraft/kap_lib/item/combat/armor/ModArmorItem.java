package net.kapitencraft.kap_lib.item.combat.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.kapitencraft.kap_lib.item.combat.armor.client.renderer.ArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * basic armor item.
 * <br>allows for full set effects TODO move to Bonuses?
 * <br>and for custom model implementation (override {@link #withCustomModel()} and {@link #getRenderer(LivingEntity, ItemStack, EquipmentSlot)} to enable
 */
public abstract class ModArmorItem extends ArmorItem {
    private static final String FULL_SET_ID = "hadFullSet";
    protected int fullSetTick = 0;

    public ModArmorItem(ArmorMaterial pMaterial, Type pType, Properties pProperties) {
        super(pMaterial, pType, pProperties);
    }


    public boolean equals(ArmorItem item) {
        return this == item || item.getMaterial() == this.getMaterial();
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotID, boolean isSelected) {
        if (entity instanceof LivingEntity living) {
            CompoundTag tag = living.getPersistentData();
            if (this.isFullSetActive(living)) {
                if (this.getEquipmentSlot() == EquipmentSlot.CHEST) {
                    if (this.fullSetTick == 0) {
                        if (!level.isClientSide) this.initFullSetTick(stack, level, living);
                        tag.putBoolean(FULL_SET_ID, true);
                        tag.putString("lastFullSet", this.getMaterial().getName());
                    }
                    this.fullSetTick++;
                    if (level.isClientSide) {
                        this.clientFullSetTick(stack, level, living);
                    } else {
                        this.fullSetTick(stack, level, living);
                    }
                }
            } else {
                if (living.getPersistentData().getBoolean(FULL_SET_ID)) {
                    if (!level.isClientSide) postFullSetTick(stack, level, living);
                    tag.putBoolean(FULL_SET_ID, false);
                }
                fullSetTick = 0;
            }
        }
    }
    public boolean isFullSetActive(LivingEntity living) {
        return isFullSetActive(living, this.getMaterial());
    }

    public static boolean isFullSetActive(LivingEntity living, ArmorMaterial materials) {
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
        return (head != null && legs != null && feet != null) && (head.getMaterial() == materials && chest.getMaterial() == materials && legs.getMaterial() == materials && feet.getMaterial() == materials);
    }

    protected void fullSetTick(ItemStack stack, Level level, LivingEntity living) {}
    protected void initFullSetTick(ItemStack stack, Level level, LivingEntity living) {}
    protected void postFullSetTick(ItemStack stack, Level level, LivingEntity living) {}
    protected void clientFullSetTick(ItemStack stack, Level level, LivingEntity living) {}

    public Multimap<Attribute, AttributeModifier> getAttributeMods(EquipmentSlot slot) {return null;}

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot slot) {
        HashMultimap<Attribute, AttributeModifier> builder = HashMultimap.create();
        builder.putAll(super.getDefaultAttributeModifiers(slot));
        if (this.getAttributeMods(slot) != null) {
            builder.putAll(this.getAttributeMods(slot));
        }
        return builder;
    }

    // display / model START

    protected boolean withCustomModel() { return false; }
    protected ArmorRenderer<?> getRenderer(LivingEntity living, ItemStack stack, EquipmentSlot slot) { return null;}

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        if (!withCustomModel()) return;
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity living, ItemStack stack, EquipmentSlot slot, HumanoidModel<?> original) {
                HumanoidModel<?> armorModel = new HumanoidModel<>(getRenderer(living, stack, slot).makeArmorParts(slot));
                armorModel.crouching = living.isShiftKeyDown();
                armorModel.riding = original.riding;
                armorModel.young = living.isBaby();
                return armorModel;
            }
        });
    }

    /**
     * creates a custom texture for your armor in
     * <br>{@code <nameSpace>:textures/models/armor/custom/<id>.png}
     */
    public static String makeCustomTextureLocation(String nameSpace, String id) {
        return new ResourceLocation(nameSpace, "textures/models/armor/custom/" + id + ".png").toString();
    }

    // display / model END
}