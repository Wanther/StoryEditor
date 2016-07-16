package org.nojob.storyeditor.view;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.util.List;

/**
 * Created by wanghe on 16/7/16.
 */
public class WorkSheetPane extends Pane {
    @Override
    protected void layoutChildren() {
        super.layoutChildren();

        List<Node> children = getManagedChildren();

        if (children != null) {
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                layoutInArea(child, i * 20, 100, 300, 300, 0, HPos.LEFT, VPos.TOP);
            }
        }

    }
}
