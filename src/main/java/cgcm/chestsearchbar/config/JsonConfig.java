package cgcm.chestsearchbar.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonConfig {

   private final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
   private final List<ConfigEntry<?, ?>> CONFIG_ENTRIES = new ArrayList<>();

   public BooleanConfig booleanBuilder(String key, boolean def) {
      return new BooleanConfig(this, key, def);
   }

   public IntConfig intBuilder(String key, int def, int min, int max) {
      return new IntConfig(this, key, def, min, max);
   }

   public StringConfig stringBuilder(String key, String def) {
      return new StringConfig(this, key, def);
   }

   public StringListConfig stringListBuilder(String key, List<String> def) {
     return new StringListConfig(key, def);
   }

   public HashMapConfig HashMapConfigBuilder(String key, Map<String, Integer> def, int min, int max) {
      return new HashMapConfig(this, key, def, min, max);
   }

   public void load(File file) {
      if (!file.exists()) {
         this.save(file);
      } else {
         JsonObject json = null;

         try (FileReader reader = new FileReader(file)) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
            for (ConfigEntry<?, ?> entry : this.CONFIG_ENTRIES) {
               String key = entry.getKey();
               if (json.has(key)) {
                  JsonObject entryJson = json.getAsJsonObject(key);
                  if (entryJson.has(key)) {
                     entry.setValue(entryJson.get(key));
                  }
               }
            }
         } catch (IOException | JsonSyntaxException e) {
            // Silent catch - file will be recreated if needed
         }

         this.checkAndSave(file, json);
      }
   }

   private void save(File file) {
      try (FileWriter writer = new FileWriter(file)) {
         this.GSON.toJson(this.toJson(), writer);
      } catch (IOException e) {
         // Silent catch
      }
   }

   private void checkAndSave(File file, JsonObject value) {
      JsonObject result = this.toJson();
      if (!result.equals(value)) {
         try (FileWriter writer = new FileWriter(file)) {
            this.GSON.toJson(result, writer);
         } catch (IOException e) {
            // Silent catch
         }
      }
   }

   public JsonObject toJson() {
      JsonObject json = new JsonObject();
      for (ConfigEntry<?, ?> entry : this.CONFIG_ENTRIES) {
         String key = entry.getKey();
         JsonObject entryJson = new JsonObject();
         if (entry.description != null) {
            entryJson.addProperty("description", entry.description);
         }
         entryJson.add(key, entry.getJson());
         json.add(key, entryJson);
      }
      return json;
   }

   public class BooleanConfig extends ConfigEntry<Boolean, BooleanConfig> {
      public BooleanConfig(JsonConfig config, String key, Boolean defaultValue) {
         super(config, key, defaultValue);
      }

      @Override
      public void setValue(JsonElement value) {
         if (value.getAsJsonPrimitive().isBoolean()) {
            this.setValue(value.getAsBoolean());
         }
      }

      @Override
      public JsonElement getJson() {
         return new JsonPrimitive(this.get());
      }
   }

   public class IntConfig extends ConfigEntry<Integer, IntConfig> {
      private final int min;
      private final int max;

      public IntConfig(JsonConfig config, String key, Integer defaultValue, int min, int max) {
         super(config, key, defaultValue);
         this.min = min;
         this.max = max;
      }

      @Override
      public void setValue(Integer value) {
         super.setValue(Math.max(this.min, Math.min(this.max, value)));
      }

      @Override
      public void setValue(JsonElement value) {
         if (value.getAsJsonPrimitive().isNumber()) {
            this.setValue(value.getAsInt());
         }
      }

      @Override
      public JsonElement getJson() {
         return new JsonPrimitive(this.get());
      }
   }

   public class StringConfig extends ConfigEntry<String, StringConfig> {
      public StringConfig(JsonConfig config, String key, String defaultValue) {
         super(config, key, defaultValue);
      }

      @Override
      public void setValue(JsonElement value) {
         if (value.getAsJsonPrimitive().isString()) {
            this.setValue(value.getAsString());
         }
      }

      @Override
      public JsonElement getJson() {
         return new JsonPrimitive(this.get());
      }
   }

   public class StringListConfig extends ConfigEntry<List<String>, StringListConfig> {
      public StringListConfig(String key, List<String> defaultValue) {
         super(JsonConfig.this, key, defaultValue);
      }

      @Override
      public void setValue(JsonElement value) {
         if (value.isJsonArray()) {
            Type listType = new TypeToken<List<String>>() {}.getType();
            List<String> list = JsonConfig.this.GSON.fromJson(value, listType);
            this.setValue(list);
         }
      }

      @Override
      public JsonElement getJson() {
         return JsonConfig.this.GSON.toJsonTree(this.get()).getAsJsonArray();
      }
   }

   public static class HashMapConfig extends ConfigEntry<Map<String, Integer>, HashMapConfig> {
      private final int min;
      private final int max;

      public HashMapConfig(JsonConfig config, String key, Map<String, Integer> defaultValue, Integer min, Integer max) {
         super(config, key, defaultValue);
         this.min = min;
         this.max = max;
      }

      @Override
      public void setValue(JsonElement value) {
         if (value.isJsonObject()) {
            Map<String, Integer> map = new HashMap<>(this.get());
            for (Map.Entry<String, JsonElement> entry : value.getAsJsonObject().entrySet()) {
               if (entry.getValue().getAsJsonPrimitive().isNumber()) {
                  map.put(entry.getKey(), Math.max(this.min, Math.min(this.max, entry.getValue().getAsInt())));
               }
            }
            this.setValue(map);
         }
      }

      @Override
      public JsonElement getJson() {
         JsonObject mapEntry = new JsonObject();
         for (Map.Entry<String, Integer> ent : this.get().entrySet()) {
            mapEntry.addProperty(ent.getKey(), ent.getValue());
         }
         return mapEntry;
      }
   }

   public abstract static class ConfigEntry<T, S extends ConfigEntry<T, S>> {
      private final String key;
      private String description;
      private T value;

      public ConfigEntry(final JsonConfig config, String key, T defaultValue) {
         this.key = key;
         this.value = defaultValue;
         config.CONFIG_ENTRIES.add(this);
      }

      @SuppressWarnings("unchecked")
      public S description(String description) {
         this.description = description;
         return (S) this;
      }

      public String getKey() {
         return this.key;
      }

      public T get() {
         return this.value;
      }

      public void setValue(T value) {
         this.value = value;
      }

      public abstract void setValue(JsonElement value);

      public abstract JsonElement getJson();
   }

}