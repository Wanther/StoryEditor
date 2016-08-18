package org.nojob.storyeditor.model;

import com.oracle.javafx.jmx.json.JSONDocument;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Created by wanghe on 16/7/28.
 */
public class ActionLink implements Cloneable {

    public static ActionLink create(JSONDocument json, int actionId) {
        ActionLink link = new ActionLink();

        link.setLinkFromId(actionId);
        link.setLinkToId(json.getNumber("id").intValue());
        link.setText(json.getString("text"));
        link.setTextTW(json.getString("textTW"));
        link.setTextENG(json.getString("textENG"));
        link.setFoundedClueId(json.getNumber("clue_found").intValue());

        return link;
    }

    private int linkFromId;
    private int linkToId;
    private StringProperty text;
    private StringProperty textTW;
    private StringProperty textENG;
    private int foundedClueId;

    public ActionLink() {}

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

    public int getLinkFromId() {
        return linkFromId;
    }

    public void setLinkFromId(int linkFromId) {
        this.linkFromId = linkFromId;
    }

    public int getLinkToId() {
        return linkToId;
    }

    public void setLinkToId(int linkToId) {
        this.linkToId = linkToId;
    }

    public int getFoundedClueId() {
        return foundedClueId;
    }

    public void setFoundedClueId(int foundedClueId) {
        this.foundedClueId = foundedClueId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ActionLink that = (ActionLink) o;

        if (linkFromId != that.linkFromId) return false;
        return linkToId == that.linkToId;

    }

    @Override
    public int hashCode() {
        int result = linkFromId;
        result = 31 * result + linkToId;
        return result;
    }

    public JSONDocument toSaveJSON(int type) {
        JSONDocument json = JSONDocument.createObject();

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

        json.setNumber("id", getLinkToId());
        json.setNumber("clue_found", getFoundedClueId());
        return json;
    }

    @Override
    public ActionLink clone() {
        ActionLink link;
        try {
            link = (ActionLink) super.clone();
        } catch (CloneNotSupportedException e) {
            link = new ActionLink();
            link.setLinkFromId(getLinkFromId());
            link.setLinkToId(getLinkToId());
            link.setFoundedClueId(getFoundedClueId());
        }
        link.text = new SimpleStringProperty(link, "text", getText());
        return link;
    }

    public void merge(ActionLink other) {
        setLinkFromId(other.getLinkFromId());
        setLinkToId(other.getLinkToId());
        setText(other.getText());
        setFoundedClueId(other.getFoundedClueId());
    }
}
