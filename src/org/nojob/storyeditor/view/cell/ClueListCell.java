package org.nojob.storyeditor.view.cell;

import javafx.scene.control.ListCell;
import org.nojob.storyeditor.model.Clue;

/**
 * Created by wanghe on 16/8/11.
 */
public class ClueListCell extends ListCell<Clue> {
    @Override
    protected void updateItem(Clue item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText(null);
        } else {
            setText(item.getText());
        }
    }
}