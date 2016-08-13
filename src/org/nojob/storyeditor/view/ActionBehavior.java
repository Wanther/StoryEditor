package org.nojob.storyeditor.view;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.geometry.Point2D;

/**
 * Created by wanghe on 16/8/4.
 */
public class ActionBehavior extends BehaviorBase<ActionNode> {

    private Point2D dragPosition = Point2D.ZERO;

    public ActionBehavior(ActionNode control) {
        super(control, null);

        initialize();
    }

    public void initialize() {

        ActionNode control = getControl();
        control.autosize();

        control.layoutXProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                control.getAction().setX(newValue.doubleValue());
            }
        });

        control.layoutYProperty().addListener((observable, oldValue, newValue) -> {
            if (!oldValue.equals(newValue)) {
                control.getAction().setY(newValue.doubleValue());
            }
        });

        control.setOnMousePressed(e -> {
            dragPosition = new Point2D(e.getX(), e.getY());
        });

        control.setOnMouseDragged(e -> {
            e.consume();

            final Point2D mouseInParent = control.localToParent(e.getX(), e.getY());

            control.relocate(mouseInParent.getX() - dragPosition.getX(), mouseInParent.getY() - dragPosition.getY());
        });

        control.setOnMouseReleased(e -> {
            dragPosition = Point2D.ZERO;
        });
    }
}
