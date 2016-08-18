package org.nojob.storyeditor.model;

import com.oracle.javafx.jmx.json.JSONDocument;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by wanghe on 16/8/16.
 */
public class ItemCondition implements Cloneable {

    public static ItemCondition create(JSONDocument json, Project project) {
        if (json == null) {
            return null;
        }

        ItemCondition condition = new ItemCondition();
        condition.setLogic(json.getNumber("logic").intValue());
        JSONDocument array = json.get("event_id");
        for (int i = 0, size = array.array().size(); i < size; i++) {
            condition.getEvents().add(project.findEventById(array.getNumber(i).intValue()));
        }

        return condition;
    }

    private IntegerProperty logic;
    private ObservableList<StoryEvent> events;

    public IntegerProperty logicProperty() {
        if (logic == null) {
            logic = new SimpleIntegerProperty(this, "logic");
        }
        return logic;
    }

    public int getLogic() {
        return logicProperty().get();
    }

    public void setLogic(int logic) {
        logicProperty().set(logic);
    }

    public ObservableList<StoryEvent> getEvents() {
        if (events == null) {
            events = FXCollections.observableArrayList();
        }
        return events;
    }

    public void setEvents(ObservableList<StoryEvent> events) {
        this.events = events;
    }

    @Override
    public ItemCondition clone() {
        ItemCondition condition = new ItemCondition();
        condition.setLogic(getLogic());
        condition.setEvents(getEvents());

        return condition;
    }

    public JSONDocument toSaveJSON(int type) {
        if (getEvents().isEmpty()) {
            return null;
        }
        JSONDocument json = JSONDocument.createObject();
        json.setNumber("logic", getLogic());
        JSONDocument array = JSONDocument.createArray();
        getEvents().forEach(event -> {
            array.array().add(event.getId());
        });
        json.set("event_id", array);

        return json;
    }
}
