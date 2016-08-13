package org.nojob.storyeditor.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by wanghe on 16/7/17.
 */
public class Project {

    public static final String PROJECT_FILE = ".storyEditor";
    public static final String RESOURCE_DIR = "resources";
    public static final String PROJECT_CONTENT = "project.json";

    public static Project create(File rootDir) {
        Project project = new Project();
        project.setRootDir(rootDir);
        project.init();
        return project;
    }

    public static Project open(File rootDir) throws Exception {
        Project project = null;
        File projectFile = new File(rootDir, Project.PROJECT_CONTENT);
        if (!projectFile.exists()) {
            project = Project.create(rootDir);
        } else {
            JsonReader reader = null;
            try {
                reader = new JsonReader(new InputStreamReader(new FileInputStream(projectFile)));
                Gson gson = new Gson();

                project = new Project();
                project.setRootDir(rootDir);
                project.init();

                JsonObject projectJson = gson.fromJson(reader, JsonObject.class);

                project.currentId = projectJson.get("actionId").getAsInt();
                project.currentItemId = projectJson.get("itemId").getAsInt();
                project.currentEventId = projectJson.get("eventId").getAsInt();
                project.currentClueId = projectJson.get("clueId").getAsInt();

                JsonArray array = projectJson.getAsJsonArray("events");

                if (array != null) {
                    for (int i = 0, size = array.size(); i < size; i++) {
                        JsonObject json = (JsonObject) array.get(i);
                        StoryEvent event = StoryEvent.create(json);
                        project.getEventList().add(event);
                    }
                }

                array = projectJson.getAsJsonArray("clues");
                if (array != null) {
                    for (int i = 0, size = array.size(); i < size; i++) {
                        JsonObject json = (JsonObject) array.get(i);
                        Clue clue = Clue.create(json);
                        project.getClueList().add(clue);
                    }
                }

                array = projectJson.getAsJsonArray("node");
                if (array != null) {
                    for (int i = 0, size = array.size(); i < size; i++) {
                        JsonObject json = (JsonObject)array.get(i);
                        StoryAction action = StoryAction.create(json);
                        project.getActions().add(action);
                    }
                }

            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
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

    public File getResDir() {
        return new File(rootDir, RESOURCE_DIR);
    }

    public JsonObject toJSONObject() {
        JsonObject project = new JsonObject();
        JsonArray eventArray = new JsonArray();
        if (events != null && !events.isEmpty()) {
            for (StoryEvent event : events) {
                eventArray.add(event.toJSONObject());
            }
        }

        project.add("events", eventArray);

        JsonArray nodeArray = new JsonArray();
        if (actions != null && !actions.isEmpty()) {
            for (StoryAction action : actions) {
                nodeArray.add(action.toJSONObject());
            }
        }

        project.add("node", nodeArray);

        return project;
    }

    public JsonObject toSaveJSON() {
        JsonObject project = new JsonObject();
        project.addProperty("actionId", currentId);
        project.addProperty("itemId", currentItemId);
        project.addProperty("eventId", currentEventId);
        project.addProperty("clueId", currentClueId);

        JsonArray eventArray = new JsonArray();
        if (events != null && !events.isEmpty()) {
            for (StoryEvent event : events) {
                eventArray.add(event.toJSONObject());
            }
        }

        project.add("events", eventArray);

        JsonArray clueArray = new JsonArray();
        if (clues != null && !clues.isEmpty()) {
            for (Clue clue : clues) {
                clueArray.add(clue.toSaveJSON());
            }
        }

        project.add("clues", clueArray);

        JsonArray nodeArray = new JsonArray();
        if (actions != null && !actions.isEmpty()) {
            for (StoryAction action : actions) {
                nodeArray.add(action.toSaveJSON());
            }
        }

        project.add("node", nodeArray);

        return project;
    }
}
