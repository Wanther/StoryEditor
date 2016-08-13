package org.nojob.storyeditor.model;

import com.google.gson.JsonObject;

/**
 * Created by wanghe on 16/7/28.
 */
public class ActionLink {

    public static ActionLink create(JsonObject json, int actionId) {
        ActionLink link = new ActionLink();

        link.setLinkFromId(actionId);
        link.setLinkToId(json.get("id").getAsInt());
        link.setText(json.get("text").getAsString());
        link.setFoundedClueId(json.get("clue_found").getAsInt());

        return link;
    }

    private int linkFromId;
    private int linkToId;
    private String text;
    private int foundedClueId;

    public ActionLink() {}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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

    public JsonObject toJSONObject() {
        JsonObject json = new JsonObject();
        json.addProperty("text", getText());
        json.addProperty("id", getLinkToId());
        json.addProperty("clue_found", getFoundedClueId());
        return json;
    }
}
