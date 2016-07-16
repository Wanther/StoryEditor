package org.nojob.storyeditor.model;

/**
 * Created by wanghe on 16/7/17.
 */
public class StoryEvent {
    private String id;
    private String text;

    public StoryEvent() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
