package org.nojob.storyeditor.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javafx.beans.property.*;

/**
 * Created by wanghe on 16/7/17.
 */
public class ActionItem implements Cloneable {

    public static ActionItem create(JsonObject json) {
        ActionItem item = new ActionItem();
        item.setId(json.get("id").getAsInt());
        item.setText(json.get("text").getAsString());
        item.setBold(json.get("bold").getAsBoolean());
        item.setFontColor(json.get("fontColor").getAsString());
        item.setFontSize(json.get("fontSize").getAsInt());
        item.setDelay(json.get("delay").getAsLong());
        JsonElement jsonElement = json.get("sound");
        if (jsonElement.isJsonNull()) {
            item.setSound(null);
        } else {
            item.setSound(jsonElement.getAsString());
        }
        item.setClue(Clue.create(json.get("clue")));
        item.setEvent(StoryEvent.create(json.get("event_trigger")));

        return item;
    }

    private IntegerProperty id;
    private StringProperty text;
    private BooleanProperty isBold;
    private StringProperty fontColor;
    private IntegerProperty fontSize;
    private LongProperty delay;
    private ObjectProperty<Clue> clue;
    private ObjectProperty<StoryEvent> event;
    private StringProperty sound;

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

    @Override
    public ActionItem clone() {
        ActionItem item = new ActionItem();
        item.setId(getId());
        item.setText(getText());
        item.setBold(isBold());
        item.setFontColor(getFontColor());
        item.setFontSize(getFontSize());
        item.setClue(getClue());
        item.setEvent(getEvent());
        item.setSound(getSound());
        item.setDelay(getDelay());

        return item;
    }

    public JsonObject toJSONObject() {
        JsonObject json = toSaveJSON();
        json.addProperty("fontColor", colorFormatConvert(getFontColor()));

        if (getEvent() == null) {
            json.add("event_trigger", JsonNull.INSTANCE);
        } else {
            json.addProperty("event_trigger", getEvent().getId());
        }

        return json;
    }

    public JsonObject toSaveJSON() {
        JsonObject json = new JsonObject();
        json.addProperty("id", getId());
        json.addProperty("text", getText());
        json.addProperty("bold", isBold());
        json.addProperty("fontColor", getFontColor());
        json.addProperty("fontSize", getFontSize());
        json.addProperty("delay", getDelay());
        json.addProperty("sound", getSound());
        if (getClue() == null) {
            json.add("clue", JsonNull.INSTANCE);
        } else {
            json.add("clue", getClue().toJSONObject());
        }
        if (getEvent() == null) {
            json.add("event_trigger", JsonNull.INSTANCE);
        } else {
            json.add("event_trigger", getEvent().toSaveJSON());
        }
        //TODO:
        json.add("condition", JsonNull.INSTANCE);

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

    public static class Condition{
        private int logic;
        private int eventId;

        public int getLogic() {
            return logic;
        }

        public void setLogic(int logic) {
            this.logic = logic;
        }

        public int getEventId() {
            return eventId;
        }

        public void setEventId(int eventId) {
            this.eventId = eventId;
        }
    }
}
