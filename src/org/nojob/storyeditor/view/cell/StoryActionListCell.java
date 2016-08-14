package org.nojob.storyeditor.view.cell;

import javafx.scene.control.ListCell;
import org.nojob.storyeditor.model.StoryAction;

/**
 * Created by wanghe on 16/8/11.
 */
public class StoryActionListCell extends ListCell<StoryAction> {
    @Override
    protected void updateItem(StoryAction item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText(null);
        } else {
            setText(item.getId() + "");
        }
    }
}