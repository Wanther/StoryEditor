package org.nojob.storyeditor.model;

import com.oracle.javafx.jmx.json.JSONDocument;

/**
 * Created by wanghe on 16/7/17.
 */
public class Clue implements Cloneable {

    public static Clue create(int id) {
        Clue clue = new Clue();
        clue.setId(id);

        return clue;
    }

    public static Clue create(JSONDocument json) {
        if (json == null) {
            return null;
        }

        Clue clue = new Clue();

        clue.setId(json.getNumber("id").intValue());
        clue.setText(json.getString("text"));
        clue.setTextTW(json.getString("textTW"));
        clue.setTextENG(json.getString("textENG"));

        return clue;
    }

    public static Clue EMPTY = new Clue();

    private int id;
    private String text;
    private String textTW;
    private String textENG;

    public Clue() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTextTW() {
        return textTW;
    }

    public void setTextTW(String textTW) {
        this.textTW = textTW;
    }

    public String getTextENG() {
        return textENG;
    }

    public void setTextENG(String textENG) {
        this.textENG = textENG;
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

        return json;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Clue clue = (Clue) o;

        return id == clue.id;

    }

    @Override
    public int hashCode() {
        return getClass().hashCode() + id;
    }

    @Override
    public Clue clone() {
        Clue clue = new Clue();
        clue.setId(getId());
        clue.setText(getText());
        clue.setTextTW(getTextTW());
        clue.setTextENG(getTextENG());

        return clue;
    }

    public void merge(Clue other) {
        setText(other.getText());
        setTextTW(other.getTextTW());
        setTextENG(other.getTextENG());
    }
}
