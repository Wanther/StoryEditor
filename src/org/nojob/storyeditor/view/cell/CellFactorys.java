package org.nojob.storyeditor.view.cell;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.*;

import java.io.File;

/**
 * Created by wanghe on 16/8/9.
 */
public class CellFactorys {
    public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> buttonForTableCell() {
        return list -> new ButtonTableCell<>();
    }

    public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> colorPickerForTabelCell() {
        return list -> new ColorPickerTableCell<>();
    }

    public static <S> Callback<TableColumn<S,Boolean>, TableCell<S,Boolean>> checkBoxForTableCell() {
        return CheckBoxTableCell.forTableColumn((TableColumn<S, Boolean>) null);
    }

    public static Callback<TableColumn<ActionItem,String>, TableCell<ActionItem,String>> actionItemTextForTableCell() {
        return list -> new ActionItemTextTableCell();
    }

    public static Callback<ListView<StoryEvent>, ListCell<StoryEvent>> eventCellForCombox() {
        return list -> new StoryEventListCell();
    }

    public static Callback<ListView<Clue>, ListCell<Clue>> clueCellForCombox() {
        return list -> new ClueListCell();
    }

    public static Callback<ListView<File>, ListCell<File>> soundCellForCombox() {
        return list -> new FilenameListCell();
    }

    public static Callback<ListView<StoryAction>, ListCell<StoryAction>> actionCellForCombox() {
        return list -> new StoryActionListCell();
    }

    public static Callback<ListView<ActionItem>, ListCell<ActionItem>> actionItemTextForListCell() {
        return list -> new ActionItemTextListCell();
    }

    public static class ButtonTableCell<S, T> extends TableCell<S, T>{

        public ButtonTableCell () {
            setAlignment(Pos.CENTER);
        }

        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            TableRow row = getTableRow();

            S rowItem = row == null ? null : (S) row.getItem();

            if (!empty && rowItem != null) {
                Button delBtn = new Button("X");
                delBtn.getStyleClass().add("button-small");
                delBtn.setOnAction(e -> {
                    if (rowItem instanceof StoryEvent) {
                        new Alert(Alert.AlertType.CONFIRMATION, "确认删除?").showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                            StoryEditor.Instance().getProject().getEventList().remove(rowItem);
                        });
                    } else if (rowItem instanceof  Clue){
                        new Alert(Alert.AlertType.CONFIRMATION, "确认删除?").showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                            StoryEditor.Instance().getProject().getClueList().remove(rowItem);
                        });
                    }
                });
                setGraphic(delBtn);
            } else {
                setGraphic(null);
            }
        }
    }

    public static class ColorPickerTableCell<S, T> extends TableCell<S, T>{
        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);

            S rowItem = (S) getTableRow().getItem();

            if (!empty && rowItem != null) {
                ColorPicker picker = new ColorPicker();
                picker.setOnAction(e -> {
                    System.out.println(picker.getValue());
                });
                setGraphic(picker);
            } else {
                setGraphic(null);
            }
        }
    }

    public static class ActionItemTextTableCell extends TableCell<ActionItem, String>{
        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            ActionItem actionItem = (ActionItem)getTableRow().getItem();
            if (!empty && actionItem != null) {
                setText(item);
                setTextFill(Color.valueOf(actionItem.getFontColor()));
                setFont(Font.font(Font.getDefault().getFamily(), actionItem.isBold() ? FontWeight.BOLD : FontWeight.NORMAL, Project.FONT_SIZE.get(actionItem.getFontSize())));
            } else {
                setText(null);
            }
        }
    }

    public static class ActionItemTextListCell extends ListCell<ActionItem> {
        @Override
        protected void updateItem(ActionItem item, boolean empty) {
            super.updateItem(item, empty);

            if (!empty && item != null) {
                setText(item.getText());
                setTextFill(Color.valueOf(item.getFontColor()));
            } else {
                setText(null);
            }
        }
    }

}
