package org.nojob.storyeditor.view;

import com.sun.javafx.scene.control.behavior.BehaviorBase;
import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Affine;

/**
 * Created by wanghe on 16/8/14.
 */
public class LinkLineSkin extends BehaviorSkinBase<LinkLine, BehaviorBase<LinkLine>> {

    private static final int LINE_BLOCK_SIZE = 20;

    protected LinkLineSkin(LinkLine control) {
        super(control, new LinkLineBehavior(control));
        initialize();
    }

    private Canvas canvas;

    private Affine affine;

    private double centerY;

    private void initialize() {
        LinkLine line = getSkinnable();

        affine = new Affine();
        line.getTransforms().add(affine);

        canvas = new Canvas();
        canvas.getGraphicsContext2D().setFont(Font.font("宋体", 12));
        canvas.getGraphicsContext2D().setTextAlign(TextAlignment.CENTER);
        canvas.getGraphicsContext2D().setTextBaseline(VPos.CENTER);

        getChildren().add(canvas);

        line.setWidth(LINE_BLOCK_SIZE);
        line.setHeight(LINE_BLOCK_SIZE);
        canvas.setWidth(LINE_BLOCK_SIZE);
        canvas.setHeight(LINE_BLOCK_SIZE);

        centerY = line.getHeight() / 2;

        line.widthProperty().addListener((observable, oldValue, newValue) -> {
            canvas.setWidth(newValue.doubleValue());
            draw();
        });

        line.getLinkFrom().widthProperty().addListener((observable, oldValue, newValue) -> {
            link();
        });

        line.getLinkFrom().heightProperty().addListener((observable, oldValue, newValue) -> {
            link();
        });

        line.getLinkFrom().layoutXProperty().addListener((observable, oldValue, newValue) -> {
            link();
        });

        line.getLinkFrom().layoutYProperty().addListener((observable, oldValue, newValue) -> {
            link();
        });

        line.getLinkTo().widthProperty().addListener((observable, oldValue, newValue) -> {
            link();
        });

        line.getLinkTo().heightProperty().addListener((observable, oldValue, newValue) -> {
            link();
        });

        line.getLinkTo().layoutXProperty().addListener((observable, oldValue, newValue) -> {
            link();
        });

        line.getLinkTo().layoutYProperty().addListener((observable, oldValue, newValue) -> {
            link();
        });

        link();
    }

    void draw() {
        canvas.getGraphicsContext2D().clearRect(0, 0, getSkinnable().getWidth(), getSkinnable().getHeight());
        drawLine();
        drawText();
    }

    void drawText() {
        canvas.getGraphicsContext2D().fillText(getSkinnable().getText(), getSkinnable().getWidth() / 2, centerY);
    }

    void drawLine() {
        canvas.getGraphicsContext2D().strokeLine(0, centerY, getSkinnable().getWidth(), centerY);
    }

    void link(double x1, double y1, double x2, double y2) {
        affine.setToIdentity();

        affine.appendTranslation(x1, y1 - LINE_BLOCK_SIZE / 2);

        getSkinnable().setWidth(distance(x1, y1, x2, y2));

        double angle = angle(1, 0, x2 - x1, y2 - y1);
        affine.appendRotation(angle);
    }

    public void link() {
        LinkLine line = getSkinnable();
        ActionNode linkFrom = line.getLinkFrom();
        ActionNode linkTo = line.getLinkTo();
        if (linkFrom == null || linkTo == null || linkFrom == linkTo) {
            //TODO: 参数错误
            return;
        }

        link(linkFrom.getLayoutX() + linkFrom.getWidth() / 2, linkFrom.getLayoutY() + linkFrom.getHeight() / 2, linkTo.getLayoutX() + linkTo.getWidth() / 2, linkTo.getLayoutY() + linkTo.getHeight() / 2);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public double angle(double ax, double ay, double x, double y) {

        final double delta = (ax * x + ay * y) / Math.sqrt(
                (ax * ax + ay * ay) * (x * x + y * y));

        if (delta > 1.0) {
            return 0.0;
        }
        if (delta < -1.0) {
            return 180.0;
        }

        return (y - ay > 0 ? 1 : -1) * Math.toDegrees(Math.acos(delta));
    }
}
