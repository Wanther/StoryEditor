package org.nojob.storyeditor.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Created by wanghe on 16/7/14.
 */
public class StoryAction {

    public static StoryAction create(int id, double x, double y) {
        StoryAction action = new StoryAction();
        action.setId(id);
        action.setX(x);
        action.setY(y);

        return action;
    }

    public static StoryAction create(JsonObject json, Project project) {
        StoryAction action = new StoryAction();

        action.setId(json.get("id").getAsInt());
        action.setKeyAction(json.get("keyNode").getAsBoolean());
        action.setX(json.get("x").getAsDouble());
        action.setY(json.get("y").getAsDouble());

        JsonElement element = json.get("keyValue");
        if (element.isJsonNull()) {
            action.setKeyActionText(null);
        } else {
            action.setKeyActionText(element.getAsString());
        }
        action.setAchievement(json.get("achievement").getAsInt());
        action.setPayAmount(json.get("payAmount").getAsInt());

        JsonArray array = json.getAsJsonArray("next");
        if (array != null) {
            for (int i = 0, size = array.size(); i < size; i++) {
                JsonObject obj = (JsonObject) array.get(i);
                ActionLink link = ActionLink.create(obj, action.getId());
                action.getLinkList().add(link);
            }
        }

        array = json.getAsJsonArray("items");
        if (array != null) {
            for (int i = 0, size = array.size(); i < size; i++) {
                JsonObject obj = (JsonObject) array.get(i);
                ActionItem item = ActionItem.create(obj, project);
                action.getItemList().add(item);
            }
        }

        return action;
    }

    private IntegerProperty id;
    private BooleanProperty isKeyAction;
    private StringProperty keyActionText;
    private IntegerProperty achievement;
    private IntegerProperty payAmount;
    private ObservableList<ActionLink> linkList = FXCollections.observableArrayList();
    private ObservableList<ActionItem> itemList = FXCollections.observableArrayList();

    private DoubleProperty x;
    private DoubleProperty y;

    public StoryAction() {}

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

    public ObservableList<ActionItem> getItemList() {
        if (itemList == null) {
            itemList = FXCollections.observableArrayList();
        }
        return itemList;
    }

    public DoubleProperty xProperty() {
        if (x == null) {
            x = new SimpleDoubleProperty(this, "x");
        }
        return x;
    }

    public double getX() {
        return xProperty().get();
    }

    public void setX(double x) {
        xProperty().set(x);
    }

    public DoubleProperty yProperty() {
        if (y == null) {
            y = new SimpleDoubleProperty(this, "y");
        }
        return y;
    }

    public double getY() {
        return yProperty().get();
    }

    public void setY(double y) {
        yProperty().set(y);
    }

    public BooleanProperty isKeyActionProperty() {
        if (isKeyAction == null) {
            isKeyAction = new SimpleBooleanProperty(this, "isKeyAction");
        }
        return isKeyAction;
    }

    public boolean isKeyAction() {
        return isKeyActionProperty().get();
    }

    public void setKeyAction(boolean keyAction) {
        isKeyActionProperty().set(keyAction);
    }

    public StringProperty keyActionTextProperty() {
        if (keyActionText == null) {
            keyActionText = new SimpleStringProperty(this, "keyActionText");
        }
        return keyActionText;
    }

    public String getKeyActionText() {
        return keyActionTextProperty().get();
    }

    public void setKeyActionText(String keyActionText) {
        keyActionTextProperty().set(keyActionText);
    }

    public IntegerProperty achievementProperty() {
        if (achievement == null) {
            achievement = new SimpleIntegerProperty(this, "achievement");
        }
        return achievement;
    }

    public int getAchievement() {
        return achievementProperty().get();
    }

    public void setAchievement(int achievement) {
        achievementProperty().set(achievement);
    }

    public IntegerProperty payAmountProperty() {
        if (payAmount == null) {
            payAmount = new SimpleIntegerProperty(this, "payAmount");
        }
        return payAmount;
    }

    public int getPayAmount() {
        return payAmountProperty().get();
    }

    public void setPayAmount(int payAmount) {
        payAmountProperty().set(payAmount);
    }

    public ObservableList<ActionLink> getLinkList() {
        if (linkList == null) {
            linkList = FXCollections.observableArrayList();
        }
        return linkList;
    }

    public JsonObject toJSONObject() {
        JsonObject action = new JsonObject();
        action.addProperty("id", getId());
        action.addProperty("keyNode", isKeyAction());
        action.addProperty("keyValue", getKeyActionText());
        action.addProperty("achievement", getAchievement());
        action.addProperty("payAmount", getPayAmount());

        JsonArray nextArray = new JsonArray();
        if (linkList != null && !linkList.isEmpty()) {
            for (ActionLink link : linkList) {
                nextArray.add(link.toJSONObject());
            }
        }
        action.add("next", nextArray);

        JsonArray itemArray = new JsonArray();
        if (itemList != null && !itemList.isEmpty()) {
            for (ActionItem item : itemList) {
                itemArray.add(item.toJSONObject());
            }
        }
        action.add("items", itemArray);

        return action;
    }

    public JsonObject toSaveJSON() {
        JsonObject action = toJSONObject();

        JsonArray itemArray = new JsonArray();

        action.addProperty("x", getX());
        action.addProperty("y", getY());
        if (itemList != null && !itemList.isEmpty()) {
            for (ActionItem item : itemList) {
                itemArray.add(item.toSaveJSON());
            }
        }
        action.add("items", itemArray);

        return action;
    }
}
