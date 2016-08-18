package org.nojob.storyeditor.view;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.nojob.storyeditor.model.ActionItem;
import org.nojob.storyeditor.model.Project;

/**
 * Created by wanghe on 16/8/4.
 */
public class ActionNodeSkin extends BehaviorSkinBase<ActionNode, ActionBehavior> {

    private Pane header;
    private Label actionId;
    private Label keyActionText;
    private Pane itemList;

    protected ActionNodeSkin(ActionNode control) {
        super(control, new ActionBehavior(control));

        initialize();
    }

    protected void initialize() {

        ActionNode actionNode = getSkinnable();

        getChildren().add(actionNode.getContent());

        header = (Pane) actionNode.lookup("#header");
        actionId = (Label)actionNode.lookup("#actionId");
        keyActionText = (Label)actionNode.lookup("#keyActionText");
        itemList = (Pane) actionNode.lookup("#itemList");

        if (actionNode.getAction().isKeyAction()) {
            header.getStyleClass().add("key-action");
        } else {
            header.getStyleClass().remove("key-action");
        }

        actionId.setText(String.format("[%d]", actionNode.getAction().getId()));
        keyActionText.setText(actionNode.getAction().getKeyActionText());

        getSkinnable().getAction().getItemList().forEach(item -> itemList.getChildren().add(createItemLabel(item)));

        actionNode.setLayoutX(actionNode.getAction().getX());
        actionNode.setLayoutY(actionNode.getAction().getY());

        registerChangeListener(actionNode.contentProperty(), "CONTENT");
        registerChangeListener(actionNode.getAction().isKeyActionProperty(), "isKeyAction");
        registerChangeListener(actionNode.getAction().keyActionTextProperty(), "keyActionText");
        actionNode.getAction().getItemList().addListener(new ListChangeListener<ActionItem>() {
            @Override
            public void onChanged(Change<? extends ActionItem> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        c.getAddedSubList().forEach(item -> {
                            itemList.getChildren().add(createItemLabel(item));
                            getSkinnable().autosize();
                        });
                    }

                    if (c.wasRemoved()) {
                        c.getRemoved().forEach(item -> {
                            itemList.getChildren().remove(getSkinnable().lookup("#" + ActionItem.ID_PREFIX + item.getId()));
                            getSkinnable().autosize();
                        });
                    }
                }
            }
        });
    }

    @Override
    protected void handleControlPropertyChanged(String propertyReference) {
        super.handleControlPropertyChanged(propertyReference);

        if ("CONTENT".equals(propertyReference)) {
            Node content = getSkinnable().getContent();
            getChildren().clear();
            if (content != null) {
                getChildren().add(content);
            }
        } else if ("isKeyAction".equals(propertyReference)) {
            if (getSkinnable().getAction().isKeyAction()) {
                header.getStyleClass().add("key-action");
            } else {
                header.getStyleClass().remove("key-action");
            }
        } else if ("keyActionText".equals(propertyReference)) {
            keyActionText.setText(getSkinnable().getAction().getKeyActionText());
        }
    }

    protected Label createItemLabel(ActionItem item) {
        Label label = new Label(item.getText());
        label.getStyleClass().add("action-item-text");
        label.setId(ActionItem.ID_PREFIX + item.getId());

        label.setFont(Font.font(Font.getDefault().getFamily(), item.isBold() ? FontWeight.BOLD : FontWeight.NORMAL, Project.FONT_SIZE.get(item.getFontSize())));
        label.setTextFill(Color.valueOf(item.getFontColor()));

        item.textProperty().addListener((observable, oldValue, newValue) -> {
            label.setText(newValue);
        });

        item.isBoldProperty().addListener((observable, oldValue, newValue) -> {
            label.setFont(Font.font(Font.getDefault().getFamily(), item.isBold() ? FontWeight.BOLD : FontWeight.NORMAL, Project.FONT_SIZE.get(item.getFontSize())));
        });
        item.fontColorProperty().addListener((observable, oldValue, newValue) -> {
            label.setTextFill(Color.valueOf(item.getFontColor()));
        });
        item.fontSizeProperty().addListener((observable, oldValue, newValue) -> {
            label.setFont(Font.font(Font.getDefault().getFamily(), item.isBold() ? FontWeight.BOLD : FontWeight.NORMAL, Project.FONT_SIZE.get(item.getFontSize())));
        });

        return label;
    }
}
