package de.home.vs.resource;

import de.home.vs.model.item.Item;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import java.util.Collection;
import java.util.function.Function;

public class ItemSerializer {

    private final String baseURL;

    public ItemSerializer(String baseURL) {
        this.baseURL = baseURL;
    }

    public JsonArray serializeItems(Collection<Item> items, Function<Item, JsonObject> serializationMethod){
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        items.forEach(item -> arrayBuilder.add(serializationMethod.apply(item)));
        return arrayBuilder.build();
    }

    public JsonObject serializeFull(Item item){
        return Json.createObjectBuilder()
                .add("id", item.getId())
                .add("description", item.getDescription())
                .add("price", item.getPrice())
                .add("name", item.getName())
//                .add("link", resolveLink(item, baseURL))
                .build();
    }

    public JsonObject serializeShort(Item item){
        return Json.createObjectBuilder()
                .add("id", item.getId())
                .add("name", item.getName())
                .add("link", resolveLink(item, baseURL))
                .build();
    }

    public Item deserializeWithId(JsonObject itemObject, int newId){
        return new Item(
                newId,
                itemObject.getString("name"),
                itemObject.getString("description"),
                itemObject.getInt("price")
        );
    }


    private String resolveLink(Item item, String baseURL){
        return baseURL + item.getId();
    }

}
