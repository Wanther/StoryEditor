package org.nojob.storyeditor.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.Callback;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.ActionLink;
import org.nojob.storyeditor.model.Clue;
import org.nojob.storyeditor.model.StoryAction;

import java.io.IOException;
import java.util.List;

/**
 * Created by wanghe on 16/8/14.
 */
public class ActionLinkController implements Callback<ButtonType, ActionLink>, EventHandler<ActionEvent> {
    public static Dialog<ActionLink> create(ActionLink link, Window owner) {
        Dialog<ActionLink> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("创建连接");

        DialogPane createLinkPane = new DialogPane();

        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(StoryEditor.class.getResource("layout/actionLink.fxml"));
        try {
            createLinkPane.setContent(loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }

        ActionLinkController controller = loader.getController();
        controller.initialize(link);

        dialog.setDialogPane(createLinkPane);

        createLinkPane.getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
        Button button = (Button)createLinkPane.lookupButton(ButtonType.FINISH);
        button.addEventFilter(ActionEvent.ACTION, controller);

        dialog.setResultConverter(controller);

        return dialog;
    }

    @FXML private Label id;
    @FXML private ComboBox<StoryAction> linkId;
    @FXML private TextField linkText;
    @FXML private ComboBox<Clue> clueList;

    private ActionLink editLink;

    public void initialize(ActionLink link) {

        try {
            editLink = link.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        id.setText(link.getLinkFromId() + "");
        linkText.setText(link.getText());
        linkText.textProperty().addListener((observable, oldValue, newValue) -> {
            editLink.setText(newValue);
        });

        ObservableList<StoryAction> actions = StoryEditor.Instance().getProject().getActions().stream().filter(action -> action.getId() != link.getLinkFromId())
                .collect(FXCollections::observableArrayList, List::add, List::addAll);
        linkId.setItems(actions);

        StoryEditor.Instance().getProject().getActions().stream().filter(action -> action.getId() == link.getLinkToId()).findFirst().ifPresent(linkId::setValue);

        linkId.valueProperty().addListener((observable, oldValue, newValue) -> {
            editLink.setLinkToId(newValue.getId());
        });

        if (link.getLinkToId() > 0) {
            linkId.setDisable(true);
        }

        ObservableList<Clue> clues = StoryEditor.Instance().getProject().getClueList().stream().collect(FXCollections::observableArrayList, List::add, List::addAll);
        clues.add(0, Clue.EMPTY);
        clueList.setItems(clues);
        clueList.setValue(Clue.EMPTY);

        StoryEditor.Instance().getProject().getClueList().stream().filter(clue -> clue.getId() == link.getFoundedClueId()).findFirst().ifPresent(clueList::setValue);

        clueList.valueProperty().addListener((observable, oldValue, newValue) -> {
            editLink.setFoundedClueId(newValue.getId());
        });
    }

    @Override
    public void handle(ActionEvent event) {

        if (editLink.getLinkFromId() <= 0 || editLink.getLinkToId() <= 0) {
            event.consume();
            new Alert(Alert.AlertType.ERROR, "请选择节点ID").showAndWait();
            return;
        }

        if (editLink.getText() == null || "".equals(editLink.getText().trim())) {
            event.consume();
            new Alert(Alert.AlertType.ERROR, "请填写按钮文字").showAndWait();
            return;
        }
    }

    @Override
    public ActionLink call(ButtonType param) {

        if (param == ButtonType.FINISH) {
            return editLink;
        }

        return null;
    }

}
