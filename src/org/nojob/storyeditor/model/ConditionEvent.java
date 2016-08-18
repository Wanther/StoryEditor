package org.nojob.storyeditor.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Created by wanghe on 16/8/16.
 */
public class ConditionEvent {
    private StoryEvent event;
    private BooleanProperty selected;

    public ConditionEvent(StoryEvent event, boolean selected) {
        this.event = event;
        selectedProperty().set(selected);
    }

    public StoryEvent getEvent() {
        return event;
    }

    public void setEvent(StoryEvent event) {
        this.event = event;
    }

    public BooleanProperty selectedProperty() {
        if (selected == null) {
            selected = new SimpleBooleanProperty(this, "selected");
        }
        return selected;
    }

    public boolean isSelected() {
        return selectedProperty().get();
    }

    public void setSelected(boolean selected) {
        selectedProperty().set(selected);
    }

    public String getText() {
        return event.getText();
    }

    public int getId() {
        return event.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConditionEvent that = (ConditionEvent) o;

        return event != null ? event.equals(that.event) : that.event == null;

    }

    @Override
    public int hashCode() {
        return event != null ? event.hashCode() : 0;
    }
}
