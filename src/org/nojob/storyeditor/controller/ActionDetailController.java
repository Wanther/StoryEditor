package org.nojob.storyeditor.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Window;
import javafx.util.Callback;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.ActionItem;
import org.nojob.storyeditor.model.StoryAction;

import java.io.IOException;
import java.util.List;

/**
 * Created by wanghe on 16/7/28.
 */
public class ActionDetailController implements Callback<ButtonType, StoryAction>, EventHandler<ActionEvent> {

    public static Dialog<StoryAction> createDialog(StoryAction action, Window owner) {
        Dialog<StoryAction> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("节点");
        dialog.setResizable(true);

        FXMLLoader loader = new FXMLLoader();
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(StoryEditor.class.getResource("layout/actionDetail.fxml"));
        Node actionDetailPane = null;
        try {
            actionDetailPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ActionDetailController controller = loader.getController();
        controller.initialize(action);

        DialogPane dialogPane = new DialogPane();
        dialogPane.setContent(actionDetailPane);

        dialog.setDialogPane(dialogPane);

        dialogPane.getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);
        Button button = (Button)dialogPane.lookupButton(ButtonType.FINISH);
        button.addEventFilter(ActionEvent.ACTION, controller);

        dialog.setResultConverter(controller);

        return dialog;
    }

    @FXML private Parent root;
    @FXML private Label actionId;
    @FXML private CheckBox isKeyAction;
    @FXML private TextField keyActionName;
    @FXML private TextField keyActionNameTW;
    @FXML private TextField keyActionNameENG;
    @FXML private TextField achievementId;
    @FXML private TextField achievement;
    @FXML private TextField payAmount;
    @FXML private TableView<ActionItem> itemListView;
    @FXML private Button itemEditBtn;
    @FXML private Button itemDeleteBtn;

    private StoryAction editingAction;

    public void initialize(StoryAction action) {

        editingAction = action.clone();

        actionId.setText(editingAction.getId() + "");

        isKeyAction.setSelected(action.isKeyAction());
        isKeyAction.selectedProperty().addListener((observable, oldValue, newValue) -> {
            editingAction.setKeyAction(newValue);
            if (newValue) {
                keyActionName.setDisable(false);
                keyActionNameTW.setDisable(false);
                keyActionNameENG.setDisable(false);
            } else {
                keyActionName.setText(null);
                keyActionName.setDisable(true);

                keyActionNameTW.setText(null);
                keyActionNameTW.setDisable(true);

                keyActionNameENG.setText(null);
                keyActionNameENG.setDisable(true);
            }
        });

        keyActionName.setDisable(!action.isKeyAction());
        keyActionName.setText(action.getKeyActionText());
        keyActionName.textProperty().addListener((observable, oldValue, newValue) -> {
            editingAction.setKeyActionText(newValue);
        });

        keyActionNameTW.setDisable(!action.isKeyAction());
        keyActionNameTW.setText(action.getKeyActionTextTW());
        keyActionNameTW.textProperty().addListener((observable, oldValue, newValue) -> {
            editingAction.setKeyActionTextTW(newValue);
        });

        keyActionNameENG.setDisable(!action.isKeyAction());
        keyActionNameENG.setText(action.getKeyActionTextENG());
        keyActionNameENG.textProperty().addListener((observable, oldValue, newValue) -> {
            editingAction.setKeyActionTextENG(newValue);
        });

        achievementId.setText(action.getAchievementId());
        achievementId.textProperty().addListener(((observable, oldValue, newValue) -> {
            editingAction.setAchievementId(newValue);
        }));

        achievement.setText(action.getAchievement() + "");
        achievement.textProperty().addListener((observable, oldValue, newValue) -> {
            int value = 0;
            try {
                value = Integer.parseInt(newValue);
            } catch (Exception e) {

            }

            editingAction.setAchievement(value);
        });

        payAmount.setText(editingAction.getPayAmount() + "");
        payAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            int value = 0;
            try {
                value = Integer.parseInt(newValue);
            } catch (Exception e) {

            }

            editingAction.setPayAmount(value);
        });

        itemListView.setItems(editingAction.getItemList());

        itemListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            itemDeleteBtn.setDisable(newValue.intValue() < 0);
            itemEditBtn.setDisable(newValue.intValue() < 0);
        });
    }

    @Override
    public void handle(ActionEvent event) {
        //TODO: 校验

    }

    @Override
    public StoryAction call(ButtonType param) {
        if (param == ButtonType.FINISH) {
            return editingAction;
        }
        return null;
    }

    @FXML
    protected void onAddItem() {
        ActionItemController.createDialog(ActionItem.create(StoryEditor.Instance().getProject().nextItemId()), root.getScene().getWindow())
                .showAndWait().ifPresent(this::saveOrEditItem);
    }

    @FXML
    protected void onEditItem() {
        ActionItem selectedItem = itemListView.getSelectionModel().getSelectedItem();
        ActionItemController.createDialog(selectedItem, root.getScene().getWindow())
                .showAndWait().ifPresent(this::saveOrEditItem);
    }

    @FXML
    protected void onDeleteItem() {
        new Alert(Alert.AlertType.CONFIRMATION, "确定删除?").showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
            ActionItem selectedItem = itemListView.getSelectionModel().getSelectedItem();
            editingAction.getItemList().remove(selectedItem);
        });
    }

    protected void saveOrEditItem(ActionItem item) {
        ActionItem oldItem = findItemById(item.getId());

        if (oldItem != null) {
            oldItem.merge(item);
            itemListView.refresh();
        } else {
            editingAction.getItemList().add(item);
        }
    }

    protected ActionItem findItemById(int id) {
        List<ActionItem> itemList = editingAction.getItemList();
        if (itemList != null && !itemList.isEmpty()) {
            for (ActionItem item : itemList) {
                if (item.getId() == id) {
                    return item;
                }
            }
        }

        return null;
    }
}
