package org.nojob.storyeditor.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by wanghe on 16/7/17.
 */
public class StoryEvent {

    public static StoryEvent create(JsonElement json) {
        if (json.isJsonNull()) {
            return null;
        }

        JsonObject obj = (JsonObject)json;

        StoryEvent event = new StoryEvent();
        event.setId(obj.get("id").getAsInt());
        event.setText(obj.get("text").getAsString());
        return event;
    }

    public static StoryEvent EMPTY = new StoryEvent();

    private IntegerProperty id;
    private StringProperty text;

    public StoryEvent() {}

    public IntegerProperty idProperty() {
        if (id == null) {
            id = new SimpleIntegerProperty(this, "id");
        }
        return id;
    }

    public int getId() {
        return idProperty().get();
    }

    public void setId(int id) {
        idProperty().set(id);
    }

    public StringProperty textProperty() {
        if (text == null) {
            text = new SimpleStringProperty(this, "text");
        }
        return text;
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String text) {
        textProperty().set(text);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoryEvent event = (StoryEvent) o;

        return id != null ? id.equals(event.id) : event.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public JsonObject toJSONObject() {
        JsonObject json = new JsonObject();
        json.addProperty("id", getId());
        json.addProperty("text", getText());
        return json;
    }

    public JsonObject toSaveJSON() {
        return toJSONObject();
    }
}
