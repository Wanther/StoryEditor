package org.nojob.storyeditor.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.Clue;
import org.nojob.storyeditor.model.StoryEvent;

/**
 * Created by wanghe on 16/8/7.
 */
public class RightPaneController {
    @FXML private TableView<StoryEvent> eventListView;
    @FXML private TextField addEventText;
    @FXML private Button addEventBtn;
    @FXML private TableColumn<StoryEvent, String> eventTextColumn;
    @FXML private TableView<Clue> clueListView;
    @FXML private TextField addClueText;
    @FXML private Button addClueBtn;
    @FXML private TableColumn<Clue, String> clueTextColumn;

    public void initialize() {
        eventListView.setItems(StoryEditor.Instance().getProject().eventsProperty());
        eventListView.setEditable(true);

        eventTextColumn.setOnEditCommit((event) -> {
            event.getRowValue().setText(event.getNewValue());
        });

        addEventBtn.setOnAction(e -> {
            String eventText = addEventText.getText();
            if (eventText != null && !"".equals(eventText)) {
                StoryEvent event = new StoryEvent();
                event.setId(StoryEditor.Instance().getProject().nextEventID());
                event.setText(eventText);
                StoryEditor.Instance().getProject().getEventList().add(event);
                addEventText.clear();
            }
        });

        clueListView.setItems(StoryEditor.Instance().getProject().cluesProperty());
        clueListView.setEditable(true);
        clueTextColumn.setOnEditCommit(e -> {
            e.getRowValue().setText(e.getNewValue());
        });

        addClueBtn.setOnAction(e -> {
            String clueText = addClueText.getText();
            if (clueText != null && !"".equals(clueText)) {
                Clue clue = new Clue();
                clue.setId(StoryEditor.Instance().getProject().nextClueId());
                clue.setText(clueText);
                StoryEditor.Instance().getProject().getClueList().add(clue);
                addClueText.clear();
            }
        });
    }
}
