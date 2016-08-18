package org.nojob.storyeditor.view;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.StoryAction;

import java.io.IOException;

/**
 * Created by wanghe on 16/7/26.
 */
@DefaultProperty("content")
public class ActionNode extends Control {

    public static final String ID_PREFIX = "action_";

    public static ActionNode create(StoryAction action) {

        ActionNode actionNode = null;
        try {
            actionNode = FXMLLoader.load(StoryEditor.class.getResource("layout/actionNode.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        actionNode.initialize(action);

        return actionNode;
    }

    private StoryAction action;
    private ObjectProperty<Node> content;

    private void initialize(StoryAction action) {

        setAction(action);

        setId(ID_PREFIX + action.getId());

    }

    public StoryAction getAction() {
        return action;
    }

    public void setAction(StoryAction action) {
        this.action = action;
    }

    public ObjectProperty<Node> contentProperty() {
        if (content == null) {
            content = new SimpleObjectProperty<>(this, "content");
        }
        return content;
    }

    public Node getContent() {
        return contentProperty().get();
    }

    public void setContent(Node content) {
        contentProperty().set(content);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ActionNodeSkin(this);
    }
}
