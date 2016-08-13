package org.nojob.storyeditor.view;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.behavior.KeyBinding;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.scene.transform.Affine;

import java.util.ArrayList;
import java.util.List;

import static javafx.scene.input.KeyCode.*;

/**
 * Created by wanghe on 16/8/2.
 */
public class ViewportBehavior extends BehaviorBase<Viewport> {

    private static final String TRANS_LEFT = "TransLeft";
    private static final String TRANS_RIGHT = "TransRight";
    private static final String TRANS_UP = "TransUp";
    private static final String TRANS_DOWN = "TransDown";

    private static final List<KeyBinding> VIEWPORT_BINDINGS = new ArrayList<>();
    static {
        VIEWPORT_BINDINGS.add(new KeyBinding(LEFT, TRANS_LEFT));
        VIEWPORT_BINDINGS.add(new KeyBinding(RIGHT, TRANS_RIGHT));

        VIEWPORT_BINDINGS.add(new KeyBinding(UP, TRANS_UP));
        VIEWPORT_BINDINGS.add(new KeyBinding(DOWN, TRANS_DOWN));
    }

    private double lastDragX;
    private double lastDragY;

    public ViewportBehavior(Viewport control) {
        super(control, VIEWPORT_BINDINGS);

        initialize();
    }

    private void initialize() {
        Viewport viewport = getControl();

        viewport.getControlGroup().getTransforms().clear();
        viewport.getControlGroup().getTransforms().add(new Affine());

        viewport.setOnMousePressed((MouseEvent e) -> {
            lastDragX = e.getX();
            lastDragY = e.getY();
        });
        viewport.setOnMouseDragged((MouseEvent e) -> {
            e.consume();

            final Point2D delta = getControl().getControlGroup().parentToLocal(e.getX() - lastDragX, e.getY() - lastDragY);

            final Affine affine = getAffine();
            affine.appendTranslation((e.getX() - lastDragX) / affine.getMxx(), (e.getY() - lastDragY) / affine.getMyy());

            lastDragX = e.getX();
            lastDragY = e.getY();
        });

        viewport.setOnZoom((ZoomEvent e) -> {

            e.consume();

            final Point2D focus = getControl().getControlGroup().sceneToLocal(e.getSceneX(), e.getSceneY());

            getAffine().appendScale(e.getZoomFactor(), e.getZoomFactor(), focus.getX(), focus.getY());

        });
    }

    protected Affine getAffine() {
        return (Affine) getControl().getControlGroup().getTransforms().get(0);
    }

    @Override
    protected void callAction(String name) {


        System.out.println(name);

        switch (name) {

        }

        super.callAction(name);
    }
}
