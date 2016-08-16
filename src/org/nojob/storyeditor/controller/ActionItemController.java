package org.nojob.storyeditor.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.nojob.storyeditor.exception.AppException;
import org.nojob.storyeditor.model.ActionItem;
import org.nojob.storyeditor.model.Clue;
import org.nojob.storyeditor.model.ConditionEvent;
import org.nojob.storyeditor.model.StoryEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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

    private static final File EMPTY_SOUND = new File("");

    private Map<String, Object> modifiedMap = new HashMap<>();

    @FXML private TextField text;
    @FXML private CheckBox isBold;
    @FXML private ChoiceBox<String> fontSize;
    @FXML private ColorPicker fontColor;
    @FXML private TextField delay;
    @FXML private ComboBox<StoryEvent> eventList;
    @FXML private ComboBox<Clue> clueList;
    @FXML private ComboBox<File> soundList;
    @FXML private TableView<ConditionEvent> conditionEvents;
    @FXML private TableColumn<ConditionEvent, Boolean> conditionSelectedColumn;
    @FXML private ToggleGroup conditionGroup;

    private ObservableList<ConditionEvent> conditionEventList;

    public void initialize() {

        text.textProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("text", newValue);
        });

        isBold.selectedProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("isBold", newValue);
        });

        fontSize.setItems(ActionItem.FONT_SIZE);
        fontSize.getSelectionModel().selectFirst();
        fontSize.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("fontSize", newValue.intValue());
        });

        fontColor.valueProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("fontColor", newValue.toString());
        });
        fontColor.setValue(Color.BLACK);

        delay.setText("0");
        delay.textProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("delay", Long.valueOf(newValue));
        });

        ObservableList<StoryEvent> events = StoryEditor.Instance().getProject().eventsProperty().stream().collect(FXCollections::observableArrayList, List::add, List::addAll);
        events.add(0, StoryEvent.EMPTY);
        eventList.setItems(events);
        eventList.setValue(StoryEvent.EMPTY);
        eventList.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == StoryEvent.EMPTY) {
                newValue = null;
            }
            modifiedMap.put("event", newValue);
        });

        ObservableList<Clue> clues = StoryEditor.Instance().getProject().cluesProperty().stream().collect(FXCollections::observableArrayList, List::add, List::addAll);
        clues.add(0, Clue.EMPTY);
        clueList.setItems(clues);
        clueList.setValue(Clue.EMPTY);
        clueList.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Clue.EMPTY) {
                newValue = null;
            }
            modifiedMap.put("clue", newValue);
        });

        ObservableList<File> sounds = StoryEditor.Instance().getProject().getSoundList().stream().collect(FXCollections::observableArrayList, List::add, List::addAll);
        sounds.add(0, EMPTY_SOUND);
        soundList.setItems(sounds);
        soundList.setValue(EMPTY_SOUND);
        soundList.valueProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("sound", newValue.getName().equals("") ? null : newValue.getName());
        });

        conditionEventList = StoryEditor.Instance().getProject().getEventList().stream().collect(FXCollections::observableArrayList,
                (list, item) -> list.add(new ConditionEvent(item, false)), List::addAll);

        conditionEvents.setItems(conditionEventList);
        conditionEvents.setSelectionModel(null);

        conditionGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            switch((String)newValue.getUserData()) {
                case "0":
                case "1":
                    conditionEvents.setVisible(true);
                    break;
                    default:
                        conditionEvents.setVisible(false);
            }
        });
    }

    public void bind(ActionItem item) {
        modifiedMap.put("id", item.getId());

        text.setText(item.getText());
        isBold.setSelected(item.isBold());
        fontSize.getSelectionModel().select(item.getFontSize());
        fontColor.setValue(Color.valueOf(item.getFontColor()));
        delay.setText(item.getDelay() + "");

        eventList.getSelectionModel().select(item.getEvent());

        clueList.getSelectionModel().select(item.getClue());

        if (item.getSound() == null || "".equals(item.getSound())) {
            soundList.setValue(EMPTY_SOUND);
        } else {
            for (File sd : StoryEditor.Instance().getProject().getSoundList()) {
                if (sd.getName().equals(item.getSound())) {
                    soundList.setValue(sd);
                    break;
                }
            }
        }

        if (item.getCondition() != null) {
            conditionEventList.stream().filter(e -> item.getCondition().getEvents().contains(e)).forEach(e -> {
                e.setSelected(true);
            });
            conditionEvents.refresh();
        }

        conditionSelectedColumn.setOnEditCommit(e -> {

        });
    }

    @Override
    public void handle(ActionEvent event) {
        String text = (String)modifiedMap.get("text");
        if (text == null || "".equals(text.trim())) {
            event.consume();
            StoryEditor.Instance().catchException(new AppException("请输入文字"));
        }
    }

    @Override
    public Map<String, Object> call(ButtonType param) {
        if (param == ButtonType.FINISH) {
            return modifiedMap;
        }
        return null;
    }
}
