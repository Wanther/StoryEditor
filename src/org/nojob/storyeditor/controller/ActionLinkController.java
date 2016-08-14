package org.nojob.storyeditor.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.ActionLink;
import org.nojob.storyeditor.view.ActionNode;

import java.io.IOException;

/**
 * Created by wanghe on 16/8/14.
 */
public class ActionLinkController {
    public static Dialog<ActionLink> create(ActionNode actionNode) {
        Dialog<ActionLink> dialog = new Dialog<>();
        dialog.setTitle("创建连接");

        DialogPane createLinkPane = new DialogPane();
        try {
            createLinkPane.setContent(FXMLLoader.load(StoryEditor.class.getResource("layout/actionLink.fxml")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Label idLabel = (Label) createLinkPane.lookup("#id");
        idLabel.setText(actionNode.getAction().getId() + "");

        createLinkPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                TextField text = (TextField) createLinkPane.lookup("#linkId");
                String linkId = text.getText();
                text = (TextField) createLinkPane.lookup("#linkText");
                String linkText = text.getText();
                text = (TextField) createLinkPane.lookup("#foundedClueId");
                String foundedClueId = text.getText();

                //TODO: 校验

                ActionLink actionLink = new ActionLink();
                actionLink.setLinkFromId(actionNode.getAction().getId());
                actionLink.setLinkToId(Integer.parseInt(linkId));

                try {
                    actionLink.setFoundedClueId(Integer.parseInt(foundedClueId));
                } catch (NumberFormatException e) {

                }
                actionLink.setText(linkText);
                return actionLink;
            }
            return null;
        });

        dialog.setDialogPane(createLinkPane);

        return dialog;
    }

    public void initialize() {

    }
}
