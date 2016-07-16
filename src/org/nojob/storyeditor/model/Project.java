package org.nojob.storyeditor.model;

import java.io.File;
import java.util.List;

/**
 * Created by wanghe on 16/7/17.
 */
public class Project {
    private File file;
    private boolean isModified;
    private List<StoryAction> actionList;
    private List<StoryEvent> eventList;

    public void exportArchive(File dest) {

    }

    public void save() {
        saveAs(file);
    }

    public void saveAs(File dest) {

    }

    public List<Clue> findClueList(StoryAction action) {
        return null;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public boolean isModified() {
        return isModified;
    }

    public void setModified(boolean modified) {
        isModified = modified;
    }

    public List<StoryAction> getActionList() {
        return actionList;
    }

    public void setActionList(List<StoryAction> actionList) {
        this.actionList = actionList;
    }

    public List<StoryEvent> getEventList() {
        return eventList;
    }

    public void setEventList(List<StoryEvent> eventList) {
        this.eventList = eventList;
    }
}
