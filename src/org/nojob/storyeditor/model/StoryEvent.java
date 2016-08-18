package org.nojob.storyeditor.model;

import com.oracle.javafx.jmx.json.JSONDocument;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by wanghe on 16/7/17.
 */
public class StoryEvent {

    public static StoryEvent create(JSONDocument json) {
        if (json == null) {
            return null;
        }

        StoryEvent event = new StoryEvent();
        event.setId(json.getNumber("id").intValue());
        event.setText(json.getString("text"));
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

        return getId() == event.getId();

    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + getId();
    }

    public JSONDocument toSaveJSON(int type) {
        JSONDocument json = JSONDocument.createObject();
        json.setNumber("id", getId());
        json.setString("text", getText());
        return json;
    }
}
