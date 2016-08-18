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
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import javafx.util.Callback;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.exception.AppException;
import org.nojob.storyeditor.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by wanghe on 16/8/10.
 */
public class ActionItemController implements Callback<ButtonType, ActionItem>, EventHandler<ActionEvent> {

    public static Dialog<ActionItem> createDialog(ActionItem item, Window owner) {
        Dialog<ActionItem> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("节点条目");
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
        controller.initialize(item);

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

    @FXML private TextField text;
    @FXML private TextField textTW;
    @FXML private TextField textENG;
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

    private ActionItem editingItem;
    private ObservableList<ConditionEvent> conditionEventList;

    public void initialize(ActionItem item) {
        editingItem = item.clone();

        text.setText(editingItem.getText());
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            editingItem.setText(newValue);
        });

        textTW.setText(editingItem.getTextTW());
        textTW.textProperty().addListener((observable, oldValue, newValue) -> {
            editingItem.setTextTW(newValue);
        });

        textENG.setText(editingItem.getTextENG());
        textENG.textProperty().addListener((observable, oldValue, newValue) -> {
            editingItem.setTextENG(newValue);
        });

        isBold.setSelected(editingItem.isBold());
        isBold.selectedProperty().addListener((observable, oldValue, newValue) -> {
            editingItem.setBold(newValue);
        });

        fontSize.setItems(Project.FONT_SIZE_DESC);
        fontSize.getSelectionModel().select(editingItem.getFontSize());
        fontSize.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            editingItem.setFontSize(newValue.intValue());
        });

        fontColor.setValue(Color.valueOf(editingItem.getFontColor()));
        fontColor.valueProperty().addListener((observable, oldValue, newValue) -> {
            editingItem.setFontColor(newValue.toString());
        });

        delay.setText(editingItem.getDelay() + "");
        delay.textProperty().addListener((observable, oldValue, newValue) -> {
            long value = 0;
            try {
                value = Long.parseLong(newValue);
            } catch (Exception e) {}
            editingItem.setDelay(value);
        });

        ObservableList<StoryEvent> events = StoryEditor.Instance().getProject().eventsProperty().stream().collect(FXCollections::observableArrayList, List::add, List::addAll);
        events.add(0, StoryEvent.EMPTY);
        eventList.setItems(events);
        eventList.setValue(editingItem.getEvent() == null ? StoryEvent.EMPTY : editingItem.getEvent());
        eventList.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == StoryEvent.EMPTY) {
                newValue = null;
            }
            editingItem.setEvent(newValue);
        });

        ObservableList<Clue> clues = StoryEditor.Instance().getProject().cluesProperty().stream().collect(FXCollections::observableArrayList, List::add, List::addAll);
        clues.add(0, Clue.EMPTY);
        clueList.setItems(clues);
        clueList.setValue(editingItem.getClue() == null ? Clue.EMPTY : editingItem.getClue());
        clueList.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Clue.EMPTY) {
                newValue = null;
            }
            editingItem.setClue(newValue);
        });

        ObservableList<File> sounds = StoryEditor.Instance().getProject().getSoundList().stream().collect(FXCollections::observableArrayList, List::add, List::addAll);
        sounds.add(0, EMPTY_SOUND);
        soundList.setItems(sounds);
        if (editingItem.getSound() == null || "".equals(editingItem.getSound())) {
            soundList.setValue(EMPTY_SOUND);
        } else {
            for (File sd : StoryEditor.Instance().getProject().getSoundList()) {
                if (sd.getName().equals(editingItem.getSound())) {
                    soundList.setValue(sd);
                    break;
                }
            }
        }
        soundList.valueProperty().addListener((observable, oldValue, newValue) -> {
            editingItem.setSound(newValue.getName().equals("") ? null : newValue.getName());
        });

        conditionSelectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(conditionSelectedColumn));

        conditionEventList = FXCollections.observableArrayList();
        StoryEditor.Instance().getProject().getEventList().forEach(event -> {
            boolean selected = editingItem.getCondition() != null && editingItem.getCondition().getEvents().contains(event);
            ConditionEvent ce = new ConditionEvent(event, selected);
            ce.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    editingItem.getCondition().getEvents().add(ce.getEvent());
                } else {
                    editingItem.getCondition().getEvents().remove(ce.getEvent());
                }
            });
            conditionEventList.add(ce);
        });
        conditionEvents.setItems(conditionEventList);
        conditionEvents.setSelectionModel(null);

        conditionGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            switch((String)newValue.getUserData()) {
                case "0":
                case "1":
                    conditionEvents.setVisible(true);
                    ItemCondition con = editingItem.getCondition();
                    if (con == null) {
                        con = new ItemCondition();
                        editingItem.setCondition(con);
                    }
                    con.setLogic(Integer.parseInt((String) newValue.getUserData()));
                    break;
                    default:
                        conditionEvents.setVisible(false);
                        editingItem.setCondition(null);
            }
        });

        String toggleUserData = "-1";
        if (editingItem.getCondition() != null) {
            toggleUserData = editingItem.getCondition().getLogic() + "";
        }

        for (Toggle toggle : conditionGroup.getToggles()) {
            if (toggleUserData.equals(toggle.getUserData())) {
                toggle.setSelected(true);
            }
        }
    }

    @Override
    public void handle(ActionEvent event) {
        String text = editingItem.getText();
        if (text == null || "".equals(text.trim())) {
            event.consume();
            StoryEditor.Instance().catchException(new AppException("请输入文字"));
        }
    }

    @Override
    public ActionItem call(ButtonType param) {
        if (param == ButtonType.FINISH) {
            return editingItem;
        }
        return null;
    }
}
