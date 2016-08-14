package org.nojob.storyeditor.view.cell;

import javafx.scene.control.ListCell;
import org.nojob.storyeditor.model.StoryEvent;

/**
 * Created by wanghe on 16/8/11.
 */
public class StoryEventListCell extends ListCell<StoryEvent> {
    @Override
    protected void updateItem(StoryEvent item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setText(null);
        } else {
            setText(item.getText());
        }
    }
}