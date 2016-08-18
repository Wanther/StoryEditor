package org.nojob.storyeditor.view;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

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

        control.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> dragPosition = new Point2D(e.getX(), e.getY()));

        control.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            e.consume();

            final Point2D mouseInParent = control.localToParent(e.getX(), e.getY());

            control.relocate(mouseInParent.getX() - dragPosition.getX(), mouseInParent.getY() - dragPosition.getY());
        });

        control.addEventFilter(MouseEvent.MOUSE_RELEASED, e -> dragPosition = Point2D.ZERO);
    }
}
