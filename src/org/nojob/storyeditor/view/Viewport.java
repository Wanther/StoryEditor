package org.nojob.storyeditor.view;

import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Created by wanghe on 16/7/16.
 */
@DefaultProperty("items")
public class Viewport extends Control {
    private final Group controlGroup;

    public Viewport() {
        controlGroup = new Group();
        controlGroup.setManaged(false);
        getChildren().add(controlGroup);
    }

    public ObservableList<Node> getItems() {
        return controlGroup.getChildren();
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new ViewportSkin(this);
    }

    public Group getControlGroup() {
        return controlGroup;
    }

    public Bounds getViewportBounds() {
        return controlGroup.parentToLocal(getBoundsInLocal());
    }
}
