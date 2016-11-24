package com.kevinmost.lifx.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.JsonAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

import static com.kevinmost.lifx.model.LifxProduct.Defaults.A19_PLUS;

@JsonAdapter(LifxProduct.Adapter.class)
public interface LifxProduct {
  @NotNull String productName();
  @NotNull String company();
  @NotNull String identifier();
  @NotNull LifxProductCapabilities capabilities();

  enum Defaults implements LifxProduct {
    A19_PLUS {
      @NotNull @Override public String productName() {
        return "LIFX+ A19";
      }

      @NotNull @Override public String company() {
        return "LIFX";
      }

      @NotNull @Override public String identifier() {
        return "lifx_plus_a19";
      }

      @NotNull @Override public LifxProductCapabilities capabilities() {
        return LifxProductCapabilities.builder()
            .hasColor(true)
            .hasVariableColorTemp(true)
            .hasIR(true)
            .hasMultizone(false)
            .build();
      }
    },
  }


  class Adapter implements JsonDeserializer<LifxProduct> {
    @Override public LifxProduct deserialize(JsonElement json, Type typeOfT, final JsonDeserializationContext context) {
      final JsonObject root = json.getAsJsonObject();
      final String identifier = root.get("identifier").getAsString();
      switch (identifier) {
        case "lifx_plus_19":
          return A19_PLUS;
        default:
          return new LifxProduct() {
            @NotNull @Override public String productName() {
              return root.get("name").getAsString();
            }

            @NotNull @Override public String company() {
              return root.get("company").getAsString();
            }

            @NotNull @Override public String identifier() {
              return identifier;
            }

            @NotNull @Override public LifxProductCapabilities capabilities() {
              return context.deserialize(root.get("capabilities"), LifxProductCapabilities.class);
            }
          };
      }
    }
  }
}
