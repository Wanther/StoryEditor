package org.nojob.storyeditor.view.cell;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import org.nojob.storyeditor.model.ActionItem;

/**
 * Created by wanghe on 16/8/11.
 */
public class EventCluePropertyValue implements Callback<TableColumn.CellDataFeatures<ActionItem,String>, ObservableValue<String>> {
    private String type;
    private String property;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public ObservableValue<String> call(TableColumn.CellDataFeatures<ActionItem, String> param) {
        ActionItem item = param.getValue();
        if ("event".equals(type)) {
            return item.getEvent() == null ? null : new ReadOnlyObjectWrapper<>(item.getEvent().getText());
        } else if ("clue".equals(type)) {
            return item.getClue() == null ? null : new ReadOnlyObjectWrapper<>(item.getClue().getText());
        }
        return null;
    }
}
