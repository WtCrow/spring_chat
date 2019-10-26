package ru.test_task.custom_serialization;

import com.google.gson.*;
import ru.test_task.models.db_models.Message;

import java.lang.reflect.Type;


public class MessageSerializer implements JsonSerializer<Message> {
    /*
     * Custom serializer for hide author_id in Message object
     * */

    public JsonElement serialize(final Message message, final Type type, final JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("message", new JsonPrimitive(message.getMessage()));
        result.add("author", new JsonPrimitive(message.getAuthor().getName()));

        return result;
    }
}
