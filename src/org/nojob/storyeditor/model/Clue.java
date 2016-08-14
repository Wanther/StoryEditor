package org.nojob.storyeditor.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Created by wanghe on 16/7/17.
 */
public class Clue {

    public static Clue create(JsonElement json) {

        if (json.isJsonNull()) {
            return null;
        }

        JsonObject obj = (JsonObject)json;

        Clue clue = new Clue();

        clue.setId(obj.get("id").getAsInt());
        clue.setText(obj.get("text").getAsString());

        return clue;
    }

    private int id;
    private String text;

    public Clue() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Clue clue = (Clue) o;

        return id == clue.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
