package org.nojob.storyeditor.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import javafx.util.Callback;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.exception.AppException;
import org.nojob.storyeditor.model.Clue;

import java.io.IOException;

/**
 * Created by wanghe on 16/8/17.
 */
public class ClueController implements Callback<ButtonType, Clue>, EventHandler<ActionEvent> {
    public static Dialog<Clue> create(Clue clue, Window owner) {
        Dialog<Clue> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("线索");
        dialog.setResizable(true);

        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(StoryEditor.class.getResource("layout/clue.fxml"));
        Pane cluePane = null;
        try {
            cluePane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClueController controller = loader.getController();
        controller.initialize(clue);

        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(cluePane);
        dialogPane.getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
        dialogPane.lookupButton(ButtonType.FINISH).addEventFilter(ActionEvent.ACTION, controller);

        dialog.setDialogPane(dialogPane);

        dialog.setResultConverter(controller);

        return dialog;
    }

    @FXML private TextField text;
    @FXML private TextField textTW;
    @FXML private TextField textENG;

    private Clue editingClue;

    public void initialize(Clue clue) {
        editingClue = clue.clone();

        text.setText(clue.getText());
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            editingClue.setText(newValue);
        });

        textTW.setText(clue.getTextTW());
        textTW.textProperty().addListener((observable, oldValue, newValue) -> {
            editingClue.setTextTW(newValue);
        });

        textENG.setText(clue.getTextENG());
        textENG.textProperty().addListener((observable, oldValue, newValue) -> {
            editingClue.setTextENG(newValue);
        });
    }

    @Override
    public void handle(ActionEvent event) {
        if (editingClue.getText() == null || "".equals(editingClue.getText().trim())) {
            event.consume();
            StoryEditor.Instance().catchException(new AppException("请填写线索文字"));
        }
    }

    @Override
    public Clue call(ButtonType param) {
        if (param == ButtonType.FINISH) {
            return editingClue;
        }
        return null;
    }
}
