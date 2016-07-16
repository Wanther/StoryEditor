package org.nojob.storyeditor.controller;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.util.Duration;
import org.nojob.storyeditor.StoryEditor;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by wanghe on 16/7/13.
 */
public class MainController {

    interface OnMainControlListener {
        void onNew();
        void onSave();
        void onSaveAs(File f);
        void onAbout();
        void onCreateStoryAction(Point position);
        void onViewEvents();
    }

    @FXML private Group zoomGroup;
    @FXML private Pane actionNodeContainer;

    private OnMainControlListener mainControlListener;

    @FXML
    protected void onNew() {

    }

    @FXML
    protected void onExit() {
        Platform.exit();
    }

    @FXML
    protected void onZoomIn() {
        // TODO: 这么方便的动画竟然没有pivot...
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), zoomGroup);
        scale.setByX(.5f);
        scale.setByY(.5f);
        scale.play();
    }

    @FXML
    protected void onZoomOut() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), zoomGroup);
        scale.setByX(-.5f);
        scale.setByY(-.5f);
        scale.play();
    }

    @FXML
    protected void onAddStoryAction() {
        try {
            Parent actionNodePane = FXMLLoader.load(StoryEditor.class.getResource("layout/actionNode.fxml"));
            actionNodeContainer.getChildren().add(actionNodePane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setOnMainControlListener(OnMainControlListener l) {
        mainControlListener = l;
    }
}
