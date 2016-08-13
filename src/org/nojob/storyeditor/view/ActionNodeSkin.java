package org.nojob.storyeditor.view;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.scene.Node;

/**
 * Created by wanghe on 16/8/4.
 */
public class ActionNodeSkin extends BehaviorSkinBase<ActionNode, ActionBehavior> {

    protected ActionNodeSkin(ActionNode control) {
        super(control, new ActionBehavior(control));

        initialize();
    }

    protected void initialize() {
        getChildren().add(getSkinnable().getContent());

        registerChangeListener(getSkinnable().contentProperty(), "CONTENT");
    }

    @Override
    protected void handleControlPropertyChanged(String propertyReference) {
        super.handleControlPropertyChanged(propertyReference);

        if ("CONTENT".equals(propertyReference)) {
            Node content = getSkinnable().getContent();
            getChildren().clear();
            if (content != null) {
                getChildren().add(content);
            }
        }
    }
}
