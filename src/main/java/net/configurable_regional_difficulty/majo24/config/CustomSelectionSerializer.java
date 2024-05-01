package net.configurable_regional_difficulty.majo24.config;

import com.google.gson.*;
import net.configurable_regional_difficulty.majo24.config.selection.CircleSelection;
import net.configurable_regional_difficulty.majo24.config.selection.RectangleSelection;
import net.configurable_regional_difficulty.majo24.config.selection.Selection;

import java.lang.reflect.Type;

// Custom serializer and deserializer for the Selection interface, as otherwise Gson isn't able to serialize Interfaces
class CustomSelectionSerializer implements JsonSerializer<Selection>, JsonDeserializer<Selection> {
    @Override
    public JsonElement serialize(Selection src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", src.getClass().getSimpleName());
        jsonObject.add("properties", context.serialize(src, src.getClass()));
        return jsonObject;
    }

    @Override
    public Selection deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();

        return switch (type) {
            case "CircleSelection" -> context.deserialize(jsonObject.get("properties"), CircleSelection.class);
            case "RectangleSelection" -> context.deserialize(jsonObject.get("properties"), RectangleSelection.class);
            default -> throw new JsonParseException("Unknown element type: " + type);
        };
    }
}