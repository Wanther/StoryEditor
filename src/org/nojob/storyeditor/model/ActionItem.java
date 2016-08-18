package org.nojob.storyeditor.model;

import com.oracle.javafx.jmx.json.JSONDocument;
import javafx.beans.property.*;
import javafx.scene.paint.Color;


/**
 * Created by wanghe on 16/7/17.
 */
public class ActionItem implements Cloneable {
    public static final String ID_PREFIX = "item_";

    public static ActionItem create(int id) {
        ActionItem item = new ActionItem();
        item.setId(id);
        item.setFontColor(Color.BLACK.toString());

        return item;
    }

    public static ActionItem create(JSONDocument json, Project project) {
        ActionItem item = new ActionItem();
        item.setId(json.getNumber("id").intValue());
        item.setText(json.getString("text"));
        item.setTextTW(json.getString("textTW"));
        item.setTextENG(json.getString("textENG"));
        item.setBold(json.getBoolean("bold"));
        item.setFontColor(json.getString("fontColor"));
        item.setFontSize(json.getNumber("fontSize").intValue());
        item.setDelay(json.getNumber("delay").longValue());
        item.setSound(json.getString("sound"));
        if (!json.isNull("condition")) {
            item.setCondition(ItemCondition.create(json.get("condition"), project));
        }

        if (!json.isNull("clue")) {
            int clueId = json.get("clue").getNumber("id").intValue();
            for (Clue cl : project.getClueList()) {
                if (cl.getId() == clueId) {
                    item.setClue(cl);
                    break;
                }
            }
        }

        if (!json.isNull("event_trigger")) {
            int eventId = json.get("event_trigger").getNumber("id").intValue();
            for (StoryEvent ev : project.getEventList()) {
                if (ev.getId() == eventId) {
                    item.setEvent(ev);
                    break;
                }
            }
        }

        return item;
    }

    private IntegerProperty id;
    private StringProperty text;
    private StringProperty textTW;
    private StringProperty textENG;
    private BooleanProperty isBold;
    private StringProperty fontColor;
    private IntegerProperty fontSize;
    private LongProperty delay;
    private ObjectProperty<Clue> clue;
    private ObjectProperty<StoryEvent> event;
    private StringProperty sound;
    private ObjectProperty<ItemCondition> condition;

    public ActionItem(){}

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

    public StringProperty textProperty() {
        if (text == null) {
            text = new SimpleStringProperty(this, "text");
        }
        return text;
    }

    public String getText() {
        return textProperty().get();
    }

    public void setText(String text) {
        textProperty().set(text);
    }

    public StringProperty textTWProperty() {
        if (textTW == null) {
            textTW = new SimpleStringProperty(this, "textTW");
        }
        return textTW;
    }

    public String getTextTW() {
        return textTWProperty().get();
    }

    public void setTextTW(String textTW) {
        textTWProperty().set(textTW);
    }

    public StringProperty textENGProperty() {
        if (textENG == null) {
            textENG = new SimpleStringProperty(this, "textENG");
        }
        return textENG;
    }

    public String getTextENG() {
        return textENGProperty().get();
    }

    public void setTextENG(String textENG) {
        textENGProperty().set(textENG);
    }

    public BooleanProperty isBoldProperty() {
        if (isBold == null) {
            isBold = new SimpleBooleanProperty(this, "isBold");
        }
        return isBold;
    }

    public boolean isBold() {
        return isBoldProperty().get();
    }

    public void setBold(boolean bold) {
        isBoldProperty().set(bold);
    }

    public StringProperty fontColorProperty() {
        if (fontColor == null) {
            fontColor = new SimpleStringProperty(this, "fontColor");
        }
        return fontColor;
    }

    public String getFontColor() {
        return fontColorProperty().get();
    }

    public void setFontColor(String fontColor) {
        fontColorProperty().set(fontColor);
    }

    public IntegerProperty fontSizeProperty() {
        if (fontSize == null) {
            fontSize = new SimpleIntegerProperty(this, "fontSize");
        }
        return fontSize;
    }

    public int getFontSize() {
        return fontSizeProperty().get();
    }

    public void setFontSize(int fontSize) {
        fontSizeProperty().set(fontSize);
    }

    public String getFontSizeText() {
        return Project.FONT_SIZE_DESC.get(getFontSize());
    }

    public LongProperty delayProperty() {
        if (delay == null) {
            delay = new SimpleLongProperty(this, "delay");
        }
        return delay;
    }

    public long getDelay() {
        return delayProperty().get();
    }

    public void setDelay(long delay) {
        delayProperty().set(delay);
    }

    public ObjectProperty<Clue> clueProperty() {
        if (clue == null) {
            clue = new SimpleObjectProperty<>(this, "clue");
        }
        return clue;
    }

    public Clue getClue() {
        return clueProperty().get();
    }

    public void setClue(Clue clue) {
        clueProperty().set(clue);
    }

    public ObjectProperty<StoryEvent> eventProperty() {
        if (event == null) {
            event = new SimpleObjectProperty<>(this, "event");
        }
        return event;
    }

    public StoryEvent getEvent() {
        return eventProperty().get();
    }

    public void setEvent(StoryEvent event) {
        eventProperty().set(event);
    }

    public StringProperty soundProperty() {
        if (sound == null) {
            sound = new SimpleStringProperty(this, "sound");
        }
        return sound;
    }

    public String getSound() {
        return soundProperty().get();
    }

    public void setSound(String sound) {
        soundProperty().set(sound);
    }

    public ObjectProperty<ItemCondition> conditionProperty() {
        if (condition == null) {
            condition = new SimpleObjectProperty<>(this, "condition");
        }
        return condition;
    }

    public ItemCondition getCondition() {
        return conditionProperty().get();
    }

    public void setCondition(ItemCondition condition) {
        conditionProperty().set(condition);
    }

    @Override
    public ActionItem clone() {
        ActionItem item = new ActionItem();
        item.setId(getId());
        item.setText(getText());
        item.setTextTW(getTextTW());
        item.setTextENG(getTextENG());
        item.setBold(isBold());
        item.setFontColor(getFontColor());
        item.setFontSize(getFontSize());
        item.setClue(getClue());
        item.setEvent(getEvent());
        item.setSound(getSound());
        item.setDelay(getDelay());
        item.setCondition(getCondition() == null ? null : getCondition().clone());

        return item;
    }

    public void merge(ActionItem other) {
        setText(other.getText());
        setTextTW(other.getTextTW());
        setTextENG(other.getTextENG());
        setBold(other.isBold());
        setFontColor(other.getFontColor());
        setFontSize(other.getFontSize());
        setDelay(other.getDelay());
        setClue(other.getClue());
        setEvent(other.getEvent());
        setSound(other.getSound());
        setCondition(other.getCondition());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionItem that = (ActionItem) o;

        return getId() == that.getId();

    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + getId();
    }

    public JSONDocument toSaveJSON(int type) {
        JSONDocument json = JSONDocument.createObject();
        json.setNumber("id", getId());

        if (type == Project.ZH_CN) {
            json.setString("text", getText());
        } else if (type == Project.ZH_TW) {
            json.setString("text", getTextTW());
        } else if (type == Project.ENG) {
            json.setString("text", getTextENG());
        } else {
            json.setString("text", getText());
            json.setString("textTW", getTextTW());
            json.setString("textENG", getTextENG());
        }

        json.setBoolean("bold", isBold());

        if (type == Project.ALL) {
            json.setString("fontColor", getFontColor());
        } else {
            json.setString("fontColor", colorFormatConvert(getFontColor()));
        }
        json.setNumber("fontSize", getFontSize());
        json.setNumber("delay", getDelay());
        json.setString("sound", getSound());
        json.set("clue", getClue() == null ? null : getClue().toSaveJSON(type));
        json.set("event_trigger", getEvent() == null ? null : getEvent().toSaveJSON(type));
        json.set("condition", getCondition() == null ? null : getCondition().toSaveJSON(type));

        return json;
    }

    public String colorFormatConvert(String hex) {
        hex = hex.substring(2, 8);
        String result = "";
        for (int i = 0; i < hex.length(); i = i + 2){
            if (i > 0) {
                result += ",";
            }
            String value = hex.substring(i, i + 2);
            result += Integer.parseInt(value, 16);
        }
        return result;
    }
}
