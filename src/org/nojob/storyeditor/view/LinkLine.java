package org.nojob.storyeditor.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.transform.Affine;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.ActionLink;
import org.nojob.storyeditor.model.Clue;

/**
 * Created by wanghe on 16/8/7.
 */
public class LinkLine extends Control{

    public static LinkLine create(ActionLink link, Parent parent) {
        ActionNode from = (ActionNode)parent.lookup("#" + ActionNode.ID_PREFIX + link.getLinkFromId());
        ActionNode to = (ActionNode)parent.lookup("#" + ActionNode.ID_PREFIX + link.getLinkToId());

        Clue foundedClue = null;
        for (Clue clue : StoryEditor.Instance().getProject().getClueList()) {
            if (clue.getId() == link.getFoundedClueId()) {
                foundedClue = clue;
                break;
            }
        }
        return new LinkLine(from, to, link.getText(), foundedClue);
    }

    private ActionNode linkFrom;
    private ActionNode linkTo;
    private Clue foundedClue;
    private StringProperty text = new SimpleStringProperty(this, "text");

    private double centerY;
    private Affine affine;

    public LinkLine(ActionNode from, ActionNode to, String text, Clue foundedClue) {
        linkFrom = from;
        linkTo = to;
        this.text.set(text);
        this.foundedClue = foundedClue;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new LinkLineSkin(this);
    }

    public ActionNode getLinkFrom() {
        return linkFrom;
    }

    public void setLinkFrom(ActionNode linkFrom) {
        this.linkFrom = linkFrom;
    }

    public ActionNode getLinkTo() {
        return linkTo;
    }

    public void setLinkTo(ActionNode linkTo) {
        this.linkTo = linkTo;
    }

    public Clue getFoundedClue() {
        return foundedClue;
    }

    public void setFoundedClue(Clue foundedClue) {
        this.foundedClue = foundedClue;
    }

    public String getText() {
        return text.get();
    }

    public StringProperty textProperty() {
        return text;
    }

    public void setText(String text) {
        this.text.set(text);
    }

    public double getCenterY() {
        return centerY;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public Affine getAffine() {
        return affine;
    }

    public void setAffine(Affine affine) {
        this.affine = affine;
    }

    @Override
    public void setWidth(double value) {
        super.setWidth(value);
    }

    @Override
    public void setHeight(double value) {
        super.setHeight(value);
    }
}