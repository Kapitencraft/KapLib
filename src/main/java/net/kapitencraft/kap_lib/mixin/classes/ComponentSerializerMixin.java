package net.kapitencraft.kap_lib.mixin.classes;

import com.google.gson.*;
import net.kapitencraft.kap_lib.io.serialization.JsonSerializer;
import net.kapitencraft.kap_lib.registry.ExtraCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.GsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;

@Mixin(Component.Serializer.class)
public abstract class ComponentSerializerMixin {
    @Shadow public abstract MutableComponent deserialize(JsonElement pJson, Type pTypeOfT, JsonDeserializationContext pContext) throws JsonParseException;

    @Shadow protected abstract void serializeStyle(Style pStyle, JsonObject pObject, JsonSerializationContext pCtx);

    @Unique
    private static JsonSerializer<Component> createSerializer() {
        return new JsonSerializer<>(ExtraCodecs.COMPONENT);
    }


    @SuppressWarnings("ConstantValue")
    @Inject(method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/network/chat/MutableComponent;", at = @At(value = "INVOKE", target = "Lcom/google/gson/JsonElement;getAsJsonObject()Lcom/google/gson/JsonObject;"), cancellable = true, remap = false)
    private void moveDeserializeToCodec(JsonElement pJson, Type pTypeOfT, JsonDeserializationContext pContext, CallbackInfoReturnable<MutableComponent> cir) {
        JsonObject object = (JsonObject) pJson;
        MutableComponent mutableComponent = (MutableComponent) createSerializer().deserialize(object, pContext);
        if (mutableComponent == null) return; //ensure vanilla behaviour still working
        if (object.has("extra")) {
            if (object.has("extra")) {
                JsonArray array = GsonHelper.getAsJsonArray(object, "extra");
                if (array.size() <= 0) {
                    throw new JsonParseException("Unexpected empty array of components");
                }

                for (int j = 0; j < array.size(); ++j) {
                    mutableComponent.append(this.deserialize(array.get(j), pTypeOfT, pContext));
                }
            }
        }
        mutableComponent.setStyle(pContext.deserialize(pJson, Style.class));
        cir.setReturnValue(mutableComponent);
    }

    /**
     * @author Kapitencraft
     * @reason moving component contents to registry
     */
    @Overwrite
    public JsonElement serialize(Component pSrc, Type pTypeOfSrc, JsonSerializationContext pContext) {
        JsonObject jsonobject = new JsonObject();
        if (!pSrc.getStyle().isEmpty()) {
            this.serializeStyle(pSrc.getStyle(), jsonobject, pContext);
        }

        if (!pSrc.getSiblings().isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(Component component : pSrc.getSiblings()) {
                jsonarray.add(this.serialize(component, Component.class, pContext));
            }

            jsonobject.add("extra", jsonarray);
        }
        createSerializer().serialize(jsonobject, pSrc, pContext);

        return jsonobject;
    }
}
