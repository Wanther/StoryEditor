package org.nojob.storyeditor.model;

import com.oracle.javafx.jmx.json.JSONDocument;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Iterator;
import java.util.List;

/**
 * Created by wanghe on 16/7/14.
 */
public class StoryAction implements Cloneable {

    public static final int COPY_PASTE_DELTA_XY = 20;

    public static StoryAction create(int id, double x, double y) {
        StoryAction action = new StoryAction();
        action.setId(id);
        action.setX(x);
        action.setY(y);

        return action;
    }

    public static StoryAction create(JSONDocument json, Project project) {
        StoryAction action = new StoryAction();

        action.setId(json.getNumber("id").intValue());
        action.setKeyAction(json.getBoolean("keyNode"));
        action.setX(json.getNumber("x").doubleValue());
        action.setY(json.getNumber("y").doubleValue());

        action.setKeyActionText(json.getString("keyValue"));
        action.setKeyActionTextTW(json.getString("keyValueTW"));
        action.setKeyActionTextENG(json.getString("keyValueENG"));
        action.setAchievement(json.getNumber("achievement").intValue());
        action.setPayAmount(json.getNumber("payAmount").intValue());

        List<Object> objects = json.getList("next");
        if (objects != null && !objects.isEmpty()) {
            objects.stream().collect(action::getLinkList, (list, item) -> {
                ActionLink link = ActionLink.create((JSONDocument)item, action.getId());
                if (link != null) {
                    list.add(link);
                }
            }, List::addAll);
        }

        objects = json.getList("items");
        if (objects != null && !objects.isEmpty()) {
            objects.stream().collect(action::getItemList, (list, item) -> {
                ActionItem itm = ActionItem.create((JSONDocument)item, project);
                if (itm != null) {
                    list.add(itm);
                }
            }, List::addAll);
        }

        return action;
    }

    private IntegerProperty id;
    private BooleanProperty isKeyAction;
    private StringProperty keyActionText;
    private StringProperty keyActionTextTW;
    private StringProperty keyActionTextENG;
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

    public StringProperty keyActionTextTWProperty() {
        if (keyActionTextTW == null) {
            keyActionTextTW = new SimpleStringProperty(this, "keyActionTextTW");
        }
        return keyActionTextTW;
    }

    public String getKeyActionTextTW() {
        return keyActionTextTWProperty().get();
    }

    public void setKeyActionTextTW(String keyActionTextTW) {
        keyActionTextTWProperty().set(keyActionTextTW);
    }

    public StringProperty keyActionTextENGProperty() {
        if (keyActionTextENG == null) {
            keyActionTextENG = new SimpleStringProperty(this, "keyActionTextENG");
        }
        return keyActionTextENG;
    }

    public String getKeyActionTextENG() {
        return keyActionTextENGProperty().get();
    }

    public void setKeyActionTextENG(String keyActionTextENG) {
        keyActionTextENGProperty().set(keyActionTextENG);
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

    @Override
    public StoryAction clone() {
        StoryAction action = new StoryAction();
        action.setId(getId());
        action.setKeyAction(isKeyAction());
        action.setKeyActionText(getKeyActionText());
        action.setKeyActionTextTW(getKeyActionTextTW());
        action.setKeyActionTextENG(getKeyActionTextENG());
        action.setAchievement(getAchievement());
        action.setPayAmount(getPayAmount());
        action.setX(getX());
        action.setY(getY());
        linkList.stream().collect(action::getLinkList, (list, item) -> list.add(item.clone()), List::addAll);
        itemList.stream().collect(action::getItemList, (list, item) -> list.add(item.clone()), List::addAll);

        return action;
    }

    public StoryAction copy(Project project) {
        StoryAction action = clone();
        action.setId(project.nextActionID());
        action.getLinkList().clear();
        action.getItemList().forEach(item -> {
            item.setId(project.nextItemId());
        });

        return action;
    }

    public void merge(StoryAction other) {
        setKeyAction(other.isKeyAction());
        setKeyActionText(other.getKeyActionText());
        setKeyActionTextTW(other.getKeyActionTextTW());
        setKeyActionTextENG(other.getKeyActionTextENG());
        setAchievement(other.getAchievement());
        setPayAmount(other.getPayAmount());
        other.getLinkList().forEach(link -> {
            ActionLink merge = null;
            for (ActionLink lk : linkList) {
                if (lk.equals(link)) {
                    merge = lk;
                    break;
                }
            }
            if (merge != null) {
                merge.merge(link);
            } else {
                linkList.add(link);
            }
        });

        Iterator<ActionLink> linkIterator = linkList.iterator();
        while (linkIterator.hasNext()) {
            if (!other.getLinkList().contains(linkIterator.next())){
                linkIterator.remove();
            }
        }

        other.getItemList().forEach(item -> {
            ActionItem merge = null;
            for (ActionItem i : itemList) {
                if (i.equals(item)) {
                    merge = i;
                    break;
                }
            }
            if (merge != null) {
                merge.merge(item);
            } else {
                itemList.add(item);
            }
        });

        Iterator<ActionItem> itemIterator = itemList.iterator();
        while (itemIterator.hasNext()) {
            if (!other.getItemList().contains(itemIterator.next())){
                itemIterator.remove();
            }
        }
    }

    public JSONDocument toSaveJSON(int type) {
        JSONDocument action = JSONDocument.createObject();
        action.setNumber("id", getId());
        action.setBoolean("keyNode", isKeyAction());

        if (type == Project.ZH_CN) {
            action.setString("keyValue", getKeyActionText());
        } else if (type == Project.ZH_TW) {
            action.setString("keyValue", getKeyActionTextTW());
        } else if (type == Project.ENG) {
            action.setString("keyValue", getKeyActionTextENG());
        } else {
            action.setString("keyValue", getKeyActionText());
            action.setString("keyValueTW", getKeyActionTextTW());
            action.setString("keyValueENG", getKeyActionTextENG());
            action.setNumber("x", getX());
            action.setNumber("y", getY());
        }

        action.setNumber("achievement", getAchievement());
        action.setNumber("payAmount", getPayAmount());

        JSONDocument array = null;
        if (linkList != null && !linkList.isEmpty()) {
            array = JSONDocument.createArray();
            for (ActionLink link : linkList) {
                array.array().add(link.toSaveJSON(type));
            }
        }
        action.set("next", array);

        array = null;
        if (itemList != null && !itemList.isEmpty()) {
            array = JSONDocument.createArray();
            for (ActionItem item : itemList) {
                array.array().add(item.toSaveJSON(type));
            }
        }
        action.set("items", array);

        return action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoryAction action = (StoryAction) o;

        return getId() == action.getId();

    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + getId();
    }
}
