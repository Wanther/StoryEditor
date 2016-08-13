package org.nojob.storyeditor.view;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;

/**
 * Created by wanghe on 16/8/2.
 */
public class ViewportSkin extends BehaviorSkinBase<Viewport, ViewportBehavior> {

    public ViewportSkin(Viewport control) {
        super(control, new ViewportBehavior(control));
    }
}
