package org.nojob.storyeditor.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by wanghe on 16/8/16.
 */
public class ItemCondition implements Cloneable {
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
}
