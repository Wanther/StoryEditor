package org.nojob.storyeditor.view;

import javafx.beans.DefaultProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.transform.Affine;

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

    public void zoomIn() {
        Affine affine = (Affine) controlGroup.getTransforms().get(0);
        Point2D center = localToScene(getWidth() / 2, getHeight() / 2);
        center = controlGroup.sceneToLocal(center);
        affine.appendScale(1.1, 1.1, center.getX(), center.getY());
    }

    public void zoomOut() {
        Affine affine = (Affine) controlGroup.getTransforms().get(0);
        Point2D center = localToScene(getWidth() / 2, getHeight() / 2);
        center = controlGroup.sceneToLocal(center);
        affine.appendScale(0.9, 0.9, center.getX(), center.getY());
    }
}
