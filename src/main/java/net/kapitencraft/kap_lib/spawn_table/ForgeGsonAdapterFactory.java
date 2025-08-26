package net.kapitencraft.kap_lib.spawn_table;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.SerializerType;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;
import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * Creates Gson serializers based on {@link SerializerType} and {@link Serializer}.
 * The resulting serializers handle JSON in the following structure:
 * <code>
 * {
 *   "type": "minecraft:example",
 *   "otherProperties": "go here"
 * }
 * </code>
 * 
 * The {@code "type"} property is read (with a fallback to {@code defaultType}) and then looked up in the provided
 * registry to produce the {@code SerializerType}. The type's {@code Serializer} is then used to deserialize the rest of
 * the JSON object.
 * 
 * If the serializer receives a JSON element that is not an object, it will try to use the {@code InlineSerializer} if
 * provided.
 */
public class ForgeGsonAdapterFactory {
   public static <E, T extends SerializerType<E>> ForgeGsonAdapterFactory.Builder<E, T> builder(IForgeRegistry<T> pRegistry, String pId, String pName, Function<E, T> pTypeFunction) {
      return new ForgeGsonAdapterFactory.Builder<>(pRegistry, pId, pName, pTypeFunction);
   }

   public static class Builder<E, T extends SerializerType<E>> {
      private final IForgeRegistry<T> registry;
      private final String elementName;
      private final String typeKey;
      private final Function<E, T> typeGetter;
      @Nullable
      private Pair<T, ForgeGsonAdapterFactory.InlineSerializer<? extends E>> inlineType;
      @Nullable
      private T defaultType;

      Builder(IForgeRegistry<T> pRegistry, String pElementName, String pTypeKey, Function<E, T> pTypeGetter) {
         this.registry = pRegistry;
         this.elementName = pElementName;
         this.typeKey = pTypeKey;
         this.typeGetter = pTypeGetter;
      }

      public ForgeGsonAdapterFactory.Builder<E, T> withInlineSerializer(T pInlineType, ForgeGsonAdapterFactory.InlineSerializer<? extends E> pInlineSerializer) {
         this.inlineType = Pair.of(pInlineType, pInlineSerializer);
         return this;
      }

      public ForgeGsonAdapterFactory.Builder<E, T> withDefaultType(T pDefaultType) {
         this.defaultType = pDefaultType;
         return this;
      }

      public Object build() {
         return new ForgeGsonAdapterFactory.JsonAdapter<>(this.registry, this.elementName, this.typeKey, this.typeGetter, this.defaultType, this.inlineType);
      }
   }

   public interface InlineSerializer<T> {
      JsonElement serialize(T pValue, JsonSerializationContext pContext);

      T deserialize(JsonElement pJson, JsonDeserializationContext pContext);
   }

   static class JsonAdapter<E, T extends SerializerType<E>> implements JsonDeserializer<E>, JsonSerializer<E> {
      private final IForgeRegistry<T> registry;
      private final String elementName;
      private final String typeKey;
      private final Function<E, T> typeGetter;
      @Nullable
      private final T defaultType;
      @Nullable
      private final Pair<T, ForgeGsonAdapterFactory.InlineSerializer<? extends E>> inlineType;

      JsonAdapter(IForgeRegistry<T> pRegistry, String pElementName, String pTypeKey, Function<E, T> pTypeGetter, @Nullable T pDefaultType, @Nullable Pair<T, ForgeGsonAdapterFactory.InlineSerializer<? extends E>> pInlineType) {
         this.registry = pRegistry;
         this.elementName = pElementName;
         this.typeKey = pTypeKey;
         this.typeGetter = pTypeGetter;
         this.defaultType = pDefaultType;
         this.inlineType = pInlineType;
      }

      public E deserialize(JsonElement pJson, Type pTypeOfT, JsonDeserializationContext pContext) throws JsonParseException {
         if (pJson.isJsonObject()) {
            JsonObject jsonobject = GsonHelper.convertToJsonObject(pJson, this.elementName);
            String s = GsonHelper.getAsString(jsonobject, this.typeKey, "");
            T t;
            if (s.isEmpty()) {
               t = this.defaultType;
            } else {
               ResourceLocation resourcelocation = new ResourceLocation(s);
               t = this.registry.getValue(resourcelocation);
            }

            if (t == null) {
               throw new JsonSyntaxException("Unknown type '" + s + "'");
            } else {
               return t.getSerializer().deserialize(jsonobject, pContext);
            }
         } else if (this.inlineType == null) {
            throw new UnsupportedOperationException("Object " + pJson + " can't be deserialized");
         } else {
            return this.inlineType.getSecond().deserialize(pJson, pContext);
         }
      }

      public JsonElement serialize(E pSrc, Type pTypeOfSrc, JsonSerializationContext pContext) {
         T t = this.typeGetter.apply(pSrc);
         if (this.inlineType != null && this.inlineType.getFirst() == t) {
            return ((InlineSerializer<E>)this.inlineType.getSecond()).serialize(pSrc, pContext);
         } else if (t == null) {
            throw new JsonSyntaxException("Unknown type: " + pSrc);
         } else {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty(this.typeKey, this.registry.getKey(t).toString());
            ((Serializer<E>)t.getSerializer()).serialize(jsonobject, pSrc, pContext);
            return jsonobject;
         }
      }
   }
}