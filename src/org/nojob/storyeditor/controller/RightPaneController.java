package org.nojob.storyeditor.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.exception.AppException;
import org.nojob.storyeditor.model.Clue;
import org.nojob.storyeditor.model.StoryEvent;

import java.io.*;

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
    @FXML private TableView<File> soundListView;
    @FXML private Button addSoundBtn;
    @FXML private Button deleteSoundBtn;

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

        soundListView.setItems(StoryEditor.Instance().getProject().getSoundList());
        addSoundBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.setTitle("请选择音效文件");
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("音频文件", "*.mp3"));
            File f = chooser.showOpenDialog(addSoundBtn.getScene().getWindow());
            if (f != null) {
                StoryEditor.Instance().async(new ImportSoundTask(f));
            }
        });

        deleteSoundBtn.setOnAction(e -> {
            File f = soundListView.getSelectionModel().getSelectedItem();
            StoryEditor.Instance().async(new DeleteSoundTask(f));
        });
    }

    private class ImportSoundTask extends Task<File> {

        private File soundFile;
        private File targetFile;

        public ImportSoundTask(File soundFile) {
            StoryEditor.Instance().getProject().setLocked(true);
            this.soundFile = soundFile;
            this.targetFile = new File(StoryEditor.Instance().getProject().getSoundDir(), soundFile.getName());
        }

        @Override
        protected File call() throws Exception {

            if (targetFile.exists()) {
                throw new AppException("音效文件" + targetFile.getName() + "已存在");
            }

            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(soundFile);
                out = new FileOutputStream(targetFile);

                byte[] buffer = new byte[1024 * 8];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }

            return soundFile;
        }

        @Override
        protected void failed() {
            StoryEditor.Instance().getProject().setLocked(false);
            StoryEditor.Instance().catchException(getException());
        }

        @Override
        protected void succeeded() {
            StoryEditor.Instance().getProject().getSoundList().add(soundFile);
            StoryEditor.Instance().getProject().setLocked(false);
        }
    }

    private class DeleteSoundTask extends Task<File> {

        private File soundFile;

        public DeleteSoundTask(File soundFile) {
            StoryEditor.Instance().getProject().setLocked(true);
            this.soundFile = soundFile;
        }

        @Override
        protected File call() throws Exception {

            if (soundFile == null) {
                throw new AppException("请选择要删除的文件");
            }

            if (soundFile.exists()) {
                boolean deleted = soundFile.delete();
                if (!deleted) {
                    throw new AppException("删除失败");
                }
            }

            return soundFile;
        }

        @Override
        protected void failed() {
            StoryEditor.Instance().getProject().setLocked(false);
            StoryEditor.Instance().catchException(getException());
        }

        @Override
        protected void succeeded() {
            StoryEditor.Instance().getProject().getSoundList().remove(soundFile);
            StoryEditor.Instance().getProject().setLocked(false);
        }
    }
}
