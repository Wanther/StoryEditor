package org.nojob.storyeditor.view;

import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.ActionItem;
import org.nojob.storyeditor.model.StoryAction;

import java.io.IOException;

/**
 * Created by wanghe on 16/7/26.
 */
@DefaultProperty("content")
public class ActionNode extends Control {

    public static final String ID_PREFIX = "action_";

    private Label actionId;
    private Label keyActionText;

    public ActionNode() {}

    public static ActionNode create(StoryAction action) {

        ActionNode actionNode = null;
        try {
            actionNode = FXMLLoader.load(StoryEditor.class.getResource("layout/actionNode.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        actionNode.setAction(action);

        actionNode.initialize();

        return actionNode;
    }

    private StoryAction action;

    private ObjectProperty<Node> content;

    private void initialize() {
        setId(ID_PREFIX + action.getId());
        setLayoutX(action.getX());
        setLayoutY(action.getY());

        actionId = (Label) getContent().lookup("#actionId");
        actionId.setText(String.format("[%d]", action.getId()));

        keyActionText = (Label) getContent().lookup("#keyActionText");
        action.keyActionTextProperty().addListener((observable, oldValue, newValue) -> {
            keyActionText.setText(newValue);
        });

        action.getItemList().addListener(new ListChangeListener<ActionItem>() {
            @Override
            public void onChanged(Change<? extends ActionItem> c) {
                bindItem();
            }
        });
        bindItem();
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

    public void bindItem() {
        Pane itemContainer = (Pane) getContent().lookup("#itemContainer");
        itemContainer.getChildren().clear();
        action.getItemList().forEach(item -> {
            Label label = new Label(item.getText());
            itemContainer.getChildren().add(label);
        });
    }
}
