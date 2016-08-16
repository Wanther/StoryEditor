package org.nojob.storyeditor.model;

/**
 * Created by wanghe on 16/8/16.
 */
public class ConditionEvent {
    private StoryEvent event;
    private boolean selected;

    public ConditionEvent(StoryEvent event, boolean selected) {
        this.event = event;
        this.selected = selected;
    }

    public StoryEvent getEvent() {
        return event;
    }

    public void setEvent(StoryEvent event) {
        this.event = event;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getText() {
        return event.getText();
    }

    public int getId() {
        return event.getId();
    }
}
