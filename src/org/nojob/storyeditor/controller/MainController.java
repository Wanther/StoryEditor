package org.nojob.storyeditor.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import org.nojob.storyeditor.StoryEditor;

import java.io.IOException;

/**
 * Created by wanghe on 16/7/13.
 */
public class MainController {
    @FXML private Group zoomGroup;
    @FXML private Pane actionNodeContainer;

    public void onExit() {
        Platform.exit();
    }

    public void onScaleUp() {
        Scale scale = new Scale(2f, 2f, 0, 0);
        zoomGroup.getTransforms().add(scale);
    }

    public void onScaleDown() {
        Scale scale = new Scale(0.5f, 0.5f, 0, 0);
        zoomGroup.getTransforms().add(scale);
    }

    public void onAddActionNode() {
        try {
            Parent actionNodePane = FXMLLoader.load(StoryEditor.class.getResource("layout/actionNode.fxml"));
            actionNodeContainer.getChildren().add(actionNodePane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
