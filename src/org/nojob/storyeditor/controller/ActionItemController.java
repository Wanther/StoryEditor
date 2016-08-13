package org.nojob.storyeditor.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.util.Callback;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.ActionItem;
import org.nojob.storyeditor.model.Clue;
import org.nojob.storyeditor.model.StoryEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by wanghe on 16/8/10.
 */
public class ActionItemController implements Callback<ButtonType, Map<String, Object>>, EventHandler<ActionEvent> {

    public static Dialog<Map<String, Object>> createDialog(ActionItem item, Window owner) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setResizable(true);

        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(StoryEditor.class.getResource("layout/actionItem.fxml"));
        Node actionItemPane = null;
        try {
            actionItemPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ActionItemController controller = loader.getController();
        if (item != null) {
            controller.bind(item);
        }

        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(actionItemPane);

        dialog.setDialogPane(dialogPane);

        dialogPane.getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
        Button button = (Button)dialogPane.lookupButton(ButtonType.FINISH);
        button.addEventFilter(ActionEvent.ACTION, controller);

        dialog.setResultConverter(controller);

        return dialog;
    }

    private Map<String, Object> modifiedMap = new HashMap<>();

    @FXML private TextField text;
    @FXML private CheckBox isBold;
    @FXML private ChoiceBox<Integer> fontSize;
    @FXML private ColorPicker fontColor;
    @FXML private TextField delay;
    @FXML private ComboBox<StoryEvent> eventList;
    @FXML private ComboBox<Clue> clueList;

    public void initialize() {

        text.textProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("text", newValue);
        });

        isBold.selectedProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("isBold", newValue);
        });

        fontSize.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("fontSize", newValue);
        });

        fontColor.valueProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("fontColor", newValue.toString());
        });
        fontColor.setValue(Color.BLACK);

        delay.textProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("delay", Long.valueOf(newValue));
        });

        eventList.setItems(StoryEditor.Instance().getProject().eventsProperty());
        eventList.valueProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("event", newValue);
        });

        clueList.setItems(StoryEditor.Instance().getProject().cluesProperty());
        clueList.valueProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("clue", newValue);
        });
    }

    public void bind(ActionItem item) {

    }

    @Override
    public void handle(ActionEvent event) {
        //TODO: 校验
    }

    @Override
    public Map<String, Object> call(ButtonType param) {
        if (param == ButtonType.FINISH) {
            return modifiedMap;
        }
        return null;
    }
}
