package org.nojob.storyeditor.model;

import com.oracle.javafx.jmx.json.JSONDocument;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wanghe on 16/7/17.
 */
public class Project {

    public static final ObservableList<String> FONT_SIZE_DESC = FXCollections.observableArrayList();
    public static final List<Double> FONT_SIZE = new ArrayList<>();

    public static final int ALL = -1;
    public static final int ZH_CN = 0;
    public static final int ZH_TW = 1;
    public static final int ENG = 2;

    static {
        FONT_SIZE_DESC.add("普通");
        FONT_SIZE_DESC.add("小");
        FONT_SIZE_DESC.add("大");

        FONT_SIZE.add(Font.getDefault().getSize());
        FONT_SIZE.add(FONT_SIZE.get(0) - 4);
        FONT_SIZE.add(FONT_SIZE.get(0) + 4);
    }



    public static final String PROJECT_FILE = ".storyEditor";
    public static final String RESOURCE_DIR = "resources";
    public static final String SOUND_DIR = "audio";
    public static final String PROJECT_CONTENT = "project.json";

    public static Project create(File rootDir) {
        Project project = new Project();
        project.setRootDir(rootDir);
        project.init();
        return project;
    }

    public static Project create(File rootDir, JSONDocument doc) throws IOException{
        Project project = new Project();
        project.setRootDir(rootDir);

        project.setCurrentId(doc.getNumber("actionId").intValue());
        project.setEventId(doc.getNumber("eventId").intValue());
        project.setItemId(doc.getNumber("itemId").intValue());
        project.setClueId(doc.getNumber("clueId").intValue());

        List<Object> objects = doc.getList("events");
        if (objects != null && !objects.isEmpty()) {
            objects.stream().collect(project::eventsProperty, (list, item) -> {
                StoryEvent event = StoryEvent.create((JSONDocument)item);
                if (event != null) {
                    list.add(event);
                }
            }, List::addAll);
        }

        objects = doc.getList("clues");
        if (objects != null && !objects.isEmpty()) {
            objects.stream().collect(project::cluesProperty, (list, item) -> {
                Clue clue = Clue.create((JSONDocument)item);
                if (clue != null) {
                    list.add(clue);
                }
            }, List::addAll);
        }

        File soundDir = project.getSoundDir();
        File[] soundFiles = soundDir.listFiles((dir, name) -> name.endsWith(".mp3") || name.endsWith(".MP3"));
        if (soundFiles != null) {
            for (File soundFile : soundFiles) {
                project.getSoundList().add(soundFile);
            }
        }

        objects = doc.getList("node");
        if (objects != null && !objects.isEmpty()) {
            objects.stream().collect(project::actionsProperty, (list, item) -> {
                StoryAction action = StoryAction.create((JSONDocument)item, project);
                if (action != null) {
                    list.add(action);
                }
            }, List::addAll);
        }

        return project;
    }

    private volatile int currentId;
    private volatile int currentEventId;
    private volatile int currentClueId;
    private volatile int currentItemId;
    private File rootDir;
    private BooleanProperty isModified;
    private ObservableList<StoryAction> actions;
    private ObservableList<StoryEvent> events;
    private ObservableList<Clue> clues;
    private ObservableList<File> sounds;
    private BooleanProperty isLocked;

    public void init() {
        actionsProperty().addListener(new ListChangeListener<StoryAction>() {
            @Override
            public void onChanged(Change<? extends StoryAction> c) {
                setModified(true);
            }
        });

        eventsProperty().addListener(new ListChangeListener<StoryEvent>() {
            @Override
            public void onChanged(Change<? extends StoryEvent> c) {
                setModified(true);
            }
        });

        cluesProperty().addListener(new ListChangeListener<Clue>() {
            @Override
            public void onChanged(Change<? extends Clue> c) {
                setModified(true);
            }
        });

    }

    public void exportArchive(File dest) {

    }

    public List<Clue> findClueList(StoryAction action) {
        return null;
    }

    public File getRootDir() {
        return rootDir;
    }

    public void setRootDir(File rootDir) {
        this.rootDir = rootDir;
    }

    public BooleanProperty isModifiedProperty() {
        if (isModified == null) {
            isModified = new SimpleBooleanProperty(this, "isModified");
        }
        return isModified;
    }

    public boolean isModified() {
        return isModifiedProperty().get();
    }

    public void setModified(boolean modified) {
        isModifiedProperty().set(modified);
    }

    public ObservableList<StoryAction> getActions() {
        return actionsProperty();
    }

    public ObservableList<StoryAction> actionsProperty() {
        if (actions == null) {
            actions = FXCollections.observableArrayList();
        }
        return actions;
    }

    public List<StoryEvent> getEventList() {
        return eventsProperty();
    }

    public ObservableList<StoryEvent> eventsProperty() {
        if (events == null) {
            events = FXCollections.observableArrayList();
        }
        return events;
    }

    public List<Clue> getClueList() {
        return cluesProperty();
    }

    public ObservableList<Clue> cluesProperty() {
        if (clues == null) {
            clues = FXCollections.observableArrayList();
        }
        return clues;
    }

    public int nextActionID() {
        return ++currentId;
    }

    public int nextEventID() {
        return ++currentEventId;
    }

    public int nextClueId() {
        return ++currentClueId;
    }

    public int nextItemId() {
        return ++currentItemId;
    }

    public int getCurrentID() {
        return currentId;
    }

    public int getEventID() {
        return currentEventId;
    }

    public int getClueID() {
        return currentClueId;
    }

    public int getItemID() {
        return currentItemId;
    }

    public void setCurrentId(int currentId) {
        this.currentId = currentId;
    }

    public void setEventId(int currentEventId) {
        this.currentEventId = currentEventId;
    }

    public void setClueId(int currentClueId) {
        this.currentClueId = currentClueId;
    }

    public void setItemId(int currentItemId) {
        this.currentItemId = currentItemId;
    }

    public File getResDir() {
        return new File(rootDir, RESOURCE_DIR);
    }

    public File getSoundDir() {
        return new File(getResDir(), SOUND_DIR);
    }

    public ObservableList<File> getSoundList() {
        if (sounds == null) {
            sounds = FXCollections.observableArrayList();
        }
        return sounds;
    }

    public BooleanProperty isLockedProperty() {
        if (isLocked == null) {
            isLocked = new SimpleBooleanProperty(this, "isLocked");
        }
        return isLocked;
    }

    public void setLocked(boolean isLocked) {
        isLockedProperty().set(isLocked);
    }

    public boolean isLocked() {
        return isLockedProperty().get();
    }

    public StoryAction findActionById(int id) {
        for (StoryAction action : actionsProperty()) {
            if (action.getId() == id) {
                return action;
            }
        }
        return null;
    }

    public StoryEvent findEventById(int id) {
        for (StoryEvent event : eventsProperty()) {
            if (event.getId() == id) {
                return event;
            }
        }
        return null;
    }

    public JSONDocument toSaveJSON(int type) {
        JSONDocument project = JSONDocument.createObject();

        if (type == Project.ALL) {
            project.setNumber("actionId", currentId);
            project.setNumber("itemId", currentItemId);
            project.setNumber("eventId", currentEventId);
            project.setNumber("clueId", currentClueId);
        }

        JSONDocument array = null;
        if (events != null && !events.isEmpty()) {
            array = JSONDocument.createArray();
            for (StoryEvent event : events) {
                array.array().add(event.toSaveJSON(type));
            }
        }

        project.set("events", array);

        array = null;
        if (clues != null && !clues.isEmpty()) {
            array = JSONDocument.createArray();
            for (Clue clue : clues) {
                array.array().add(clue.toSaveJSON(type));
            }
        }

        project.set("clues", array);

        array = null;
        if (actions != null && !actions.isEmpty()) {
            array = JSONDocument.createArray();
            for (StoryAction action : actions) {
                array.array().add(action.toSaveJSON(type));
            }
        }

        project.set("node", array);

        return project;
    }
}
