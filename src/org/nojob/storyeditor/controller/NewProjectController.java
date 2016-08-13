package org.nojob.storyeditor.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.Callback;
import org.nojob.storyeditor.StoryEditor;

import java.io.File;
import java.io.IOException;

/**
 * Created by wanghe on 16/8/12.
 */
public class NewProjectController implements Callback<ButtonType, File>, EventHandler<ActionEvent> {

    public static Dialog<File> createDialog(Window owner) {
        Dialog<File> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setResizable(false);

        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(StoryEditor.class.getResource("layout/newProject.fxml"));
        Node newProjectPane = null;
        try {
            newProjectPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        NewProjectController controller = loader.getController();

        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(newProjectPane);

        dialog.setDialogPane(dialogPane);

        dialogPane.getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
        Button button = (Button)dialogPane.lookupButton(ButtonType.FINISH);
        button.addEventFilter(ActionEvent.ACTION, controller);

        dialog.setResultConverter(controller);

        return dialog;
    }

    @FXML private TextField projectName;
    @FXML private Label projectPath;

    public void initialize() {
        projectPath.setText(new File(System.getProperty("user.home")).getAbsolutePath());
    }

    @Override
    public void handle(ActionEvent event) {
        String projectNameText = projectName.getText();
        if (projectNameText == null || !projectNameText.matches("[a-zA-Z0-9]+")) {
            event.consume();
            new Alert(Alert.AlertType.ERROR, "项目名称只能是数字或字母")
                    .show();
        }
    }

    @Override
    public File call(ButtonType param) {
        if (param == ButtonType.FINISH) {
            File f = new File(projectPath.getText(), projectName.getText());
            return f;
        }
        return null;
    }

    @FXML
    protected void onSelectProjectPath() {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("请选择工程目录");
        chooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File rootDir = chooser.showDialog(projectName.getScene().getWindow());
        if (rootDir != null) {
            projectPath.setText(rootDir.getAbsolutePath());
        }
    }
}
