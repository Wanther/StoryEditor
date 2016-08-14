package org.nojob.storyeditor.view.cell;

import javafx.scene.control.ListCell;

import java.io.File;

/**
 * Created by wanghe on 16/8/11.
 */
public class FilenameListCell extends ListCell<File> {
    @Override
    protected void updateItem(File item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText(null);
        } else {
            setText(item.getName());
        }
    }
}