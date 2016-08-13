package org.nojob.storyeditor.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.nojob.storyeditor.model.Clue;
import org.nojob.storyeditor.model.StoryAction;
import org.nojob.storyeditor.model.StoryEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wanghe on 16/7/28.
 */
public class ActionDetailController implements Callback<ButtonType, Map<String, Object>>, EventHandler<ActionEvent> {

    public static Dialog<Map<String, Object>> createDialog(StoryAction action, Window owner) {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.initOwner(owner);
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
        controller.bind(action);

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
    @FXML private TextField achievement;
    @FXML private TextField payAmount;
    @FXML private TableView<ActionItem> itemListView;
    @FXML private Button itemEditBtn;
    @FXML private Button itemDeleteBtn;

    private Map<String, Object> modifiedMap = new HashMap<>();

    public void bind(StoryAction action) {
        actionId.setText(action.getId() + "");
        modifiedMap.put("id", action.getId());

        isKeyAction.setSelected(action.isKeyAction());
        isKeyAction.selectedProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("isKeyAction", newValue);
        });

        keyActionName.setText(action.getKeyActionText());
        keyActionName.textProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("keyActionText", newValue);
        });

        achievement.setText(action.getAchievement() + "");
        achievement.textProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("achievement", newValue);
        });

        payAmount.setText(action.getPayAmount() + "");
        payAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            modifiedMap.put("payAmount", newValue);
        });

        bindItemList(action.getItemList());
    }

    protected void bindItemList(ObservableList<ActionItem> itemList) {
        ObservableList<ActionItem> items = FXCollections.observableArrayList();
        if (itemList != null && !itemList.isEmpty()) {
            for (ActionItem item : itemList) {
                items.add(item.clone());
            }
        }
        itemListView.setItems(items);

        itemListView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() >= 0) {
                itemDeleteBtn.setDisable(false);
                itemEditBtn.setDisable(false);
            }
        });

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

    @FXML
    protected void onAddItem() {
        Dialog<Map<String, Object>> dialog = ActionItemController.createDialog(null, root.getScene().getWindow());
        dialog.showAndWait().ifPresent(response -> {
            saveOrEdit(response);
        });
    }

    @FXML
    protected void onEditItem() {
        ActionItem selectedItem = itemListView.getSelectionModel().getSelectedItem();
        Dialog<Map<String, Object>> dialog = ActionItemController.createDialog(selectedItem, root.getScene().getWindow());
        dialog.showAndWait().ifPresent(response -> {
            saveOrEdit(response);
        });
    }

    @FXML
    protected void onDeleteItem() {
        ActionItem selectedItem = itemListView.getSelectionModel().getSelectedItem();
        itemListView.getItems().remove(selectedItem);
        modifiedMap.put("items", itemListView.getItems());
    }

    protected void saveOrEdit(Map<String, Object> response) {
        boolean isEdit = false;
        ActionItem item;
        if (response.containsKey("id")) {
            // Edit
            isEdit = true;
            item = findItemById((int)response.get("id"));
        } else {
            // Add
            item = new ActionItem();
        }

        Set<String> keySet = response.keySet();
        for (String key : keySet) {
            Object value = response.get(key);
            switch (key) {
                case "text":
                    item.setText((String)value);
                    break;
                case "isBold":
                    item.setBold((boolean)value);
                    break;
                case "fontColor":
                    item.setFontColor((String)value);
                    break;
                case "fontSize":
                    item.setFontSize((int)value);
                    break;
                case "delay":
                    item.setDelay((long)value);
                    break;
                case "clue":
                    item.setClue((Clue)value);
                    break;
                case "event":
                    item.setEvent((StoryEvent)value);
                    break;
                case "sound":
                    item.setSound((String)value);
                    break;
            }
        }

        if (!isEdit) {
            item.setId(StoryEditor.Instance().getProject().nextItemId());
            itemListView.getItems().add(item);
        }

        modifiedMap.put("items", itemListView.getItems());
    }

    protected ActionItem findItemById(int id) {
        List<ActionItem> itemList = itemListView.getItems();
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
