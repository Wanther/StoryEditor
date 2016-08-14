package org.nojob.storyeditor.view.cell;

import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.ActionItem;
import org.nojob.storyeditor.model.Clue;
import org.nojob.storyeditor.model.StoryAction;
import org.nojob.storyeditor.model.StoryEvent;

import java.io.File;

/**
 * Created by wanghe on 16/8/9.
 */
public class CellFactorys {
    public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> buttonForTableCell() {
        return list -> new ButtonTableCell<S, String>();
    }

    public static <S> Callback<TableColumn<S,String>, TableCell<S,String>> colorPickerForTabelCell() {
        return list -> new ColorPickerTableCell<S, String>();
    }

    public static <S> Callback<TableColumn<S,Boolean>, TableCell<S,Boolean>> checkBoxForTableCell(TableColumn column) {
        return CheckBoxTableCell.forTableColumn(column);
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

    public static class ButtonTableCell<S, T> extends TableCell<S, T>{
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
                        StoryEditor.Instance().getProject().getEventList().remove(rowItem);
                    } else if (rowItem instanceof  Clue){
                        StoryEditor.Instance().getProject().getClueList().remove(rowItem);
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
            } else {
                setText(null);
            }
        }
    }

}
