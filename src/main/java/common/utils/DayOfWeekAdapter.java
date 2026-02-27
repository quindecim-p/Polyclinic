package common.utils;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.DayOfWeek;

public class DayOfWeekAdapter implements JsonSerializer<DayOfWeek>, JsonDeserializer<DayOfWeek> {

    @Override
    public JsonElement serialize(DayOfWeek dayOfWeek, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(dayOfWeek.name());
    }

    @Override
    public DayOfWeek deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return DayOfWeek.valueOf(json.getAsString());
    }
}