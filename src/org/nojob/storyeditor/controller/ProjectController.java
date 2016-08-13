package org.nojob.storyeditor.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.model.*;
import org.nojob.storyeditor.view.ActionNode;
import org.nojob.storyeditor.view.LinkLine;
import org.nojob.storyeditor.view.Viewport;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by wanghe on 16/7/17.
 */
public class ProjectController {
    @FXML private Viewport actionViewport;

    private ObservableList<Node> selectedNodes = FXCollections.observableArrayList();

    private ActionListChangeListener actionListChangeListener;
    private ActionLinkListChangeListener actionLinkListChangeListener;

    public void initialize() {

        actionListChangeListener = new ActionListChangeListener();
        actionLinkListChangeListener = new ActionLinkListChangeListener();

        renderViewport();

        actionViewport.setContextMenu(buildViewportContextMenu());

        StoryEditor.Instance().getProject().getActions().addListener(actionListChangeListener);

        selectedNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        c.getAddedSubList().stream().forEach(item -> {
                            item.getStyleClass().add("selected");
                        });
                    }

                    if (c.wasRemoved()) {
                        c.getRemoved().stream().forEach(item -> {
                            item.getStyleClass().remove("selected");
                        });
                    }
                }
            }
        });
    }

    public void onViewEvents() {

    }

    public void onZoomIn() {

    }

    public void onZoomOut() {

    }

    public void onSearch(String queryString) {

    }

    protected void showActionDetail(ActionNode actionNode) {
        Dialog<Map<String, Object>> dialog = ActionDetailController.createDialog(actionNode.getAction(), actionNode.getScene().getWindow());

        dialog.showAndWait().ifPresent(this::bindActionNode);
    }

    protected ContextMenu buildViewportContextMenu() {
        ContextMenu menu = new ContextMenu();
        MenuItem item = new MenuItem("新建节点");
        item.setOnAction(e -> {
            MenuItem clicked = (MenuItem)e.getSource();
            Point2D popup = actionViewport.getControlGroup().screenToLocal(new Point2D(clicked.getParentPopup().getAnchorX(), clicked.getParentPopup().getAnchorY()));

            StoryAction action = StoryAction.create(StoryEditor.Instance().getProject().nextActionID(), popup.getX(), popup.getY());
            action.getLinkList().addListener(actionLinkListChangeListener);

            StoryEditor.Instance().getProject().getActions().add(action);
        });
        menu.getItems().add(item);
        return menu;
    }

    protected ContextMenu buildActionContextMenu(ActionNode actionPane) {
        ContextMenu menu = new ContextMenu();

        MenuItem item = new MenuItem("创建连接");

        item.getProperties().put("target_action", actionPane);
        item.setOnAction(e -> {
            MenuItem clicked = (MenuItem)e.getSource();
            ActionNode ap = (ActionNode)clicked.getProperties().get("target_action");
            requestCreateLink(ap);
        });
        menu.getItems().add(item);

        item = new MenuItem("删除");
        item.getProperties().put("target_action", actionPane);
        item.setOnAction(e -> {
            MenuItem clicked = (MenuItem)e.getSource();
            ActionNode ap = (ActionNode)clicked.getProperties().get("target_action");
            StoryEditor.Instance().getProject().getActions().remove(ap.getAction());
        });
        menu.getItems().add(item);

        item = new MenuItem("属性");
        item.getProperties().put("target_action", actionPane);
        item.setOnAction(e -> {
            MenuItem clicked = (MenuItem)e.getSource();
            showActionDetail((ActionNode)clicked.getProperties().get("target_action"));
        });
        menu.getItems().add(item);

        return menu;
    }

    protected ContextMenu buildLinkContextMenu(LinkLine line) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem item = new MenuItem("删除连接");
        item.getProperties().put("target_line", line);
        item.setOnAction(e -> {
            MenuItem clicked = (MenuItem)e.getSource();
            LinkLine l = (LinkLine) clicked.getProperties().get("target_line");
            Iterator<ActionLink> it = line.getLinkFrom().getAction().getLinkList().iterator();
            while (it.hasNext()) {
                ActionLink link = it.next();
                if (link.getLinkToId() == l.getLinkTo().getAction().getId()) {
                    it.remove();
                }
            }
        });
        contextMenu.getItems().add(item);

        return contextMenu;
    }

    protected void requestCreateLink(ActionNode actionNode) {
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

        dialog.showAndWait().ifPresent(this::createLinkLine);

    }

    protected void createLinkLine(ActionLink actionLink) {
        StoryEditor.Instance().getProject().getActions().stream().filter(item -> item.getId() == actionLink.getLinkFromId()).forEach(item -> {
            item.getLinkList().add(actionLink);
        });
    }

    protected void bindActionNode(Map<String, Object> modifiedAction) {
        if (modifiedAction == null || modifiedAction.isEmpty()) {
            return;
        }
        int id = (int)modifiedAction.get("id");
        ActionNode node = (ActionNode)actionViewport.lookup("#" + ActionNode.ID_PREFIX + id);
        StoryAction action = node.getAction();
        Set<String> keySet = modifiedAction.keySet();
        for (String key : keySet) {
            Object value = modifiedAction.get(key);
            switch (key) {
                case "isKeyAction":
                    action.setKeyAction((boolean)value);
                    break;
                case "keyActionText":
                    action.setKeyActionText((String)value);
                    break;
                case "achievement":
                    action.setAchievement((int)value);
                    break;
                case "payAmount":
                    action.setPayAmount((int)value);
                    break;
                case "links":
                    break;
                case "items":
                    action.getItemList().clear();
                    action.getItemList().addAll((List<ActionItem>)value);
                    break;
            }
        }
    }

    protected void renderViewport() {
        Project project = StoryEditor.Instance().getProject();

        if (project == null) {
            return;
        }

        project.getActions().forEach(action -> {
            ActionNode actionNode = ActionNode.create(action);
            actionNode.setContextMenu(buildActionContextMenu(actionNode));
            actionNode.setOnMouseClicked((MouseEvent e) -> {
                if (e.getClickCount() >= 2) {
                    showActionDetail(actionNode);
                }
            });
            actionViewport.getItems().add(actionNode);
        });

        project.getActions().forEach(action -> {
            action.getLinkList().forEach(link -> {
                LinkLine line = LinkLine.create(link, actionViewport);
                line.setContextMenu(buildLinkContextMenu(line));
                actionViewport.getItems().add(0, line);
            });

            action.getLinkList().addListener(new ActionLinkListChangeListener());
        });
    }

    private class ActionListChangeListener implements ListChangeListener<StoryAction> {

        @Override
        public void onChanged(Change<? extends StoryAction> c) {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(item -> {
                        ActionNode actionNode = ActionNode.create(item);
                        actionNode.setContextMenu(buildActionContextMenu(actionNode));
                        actionNode.setOnMouseClicked((MouseEvent e) -> {
                            if (e.getClickCount() >= 2) {
                                showActionDetail(actionNode);
                            }
                        });
                        actionViewport.getItems().add(actionNode);
                    });
                }

                if (c.wasRemoved()) {
                    c.getRemoved().forEach(item -> {
                        ActionNode actionNode = (ActionNode) actionViewport.lookup("#" + ActionNode.ID_PREFIX + item.getId());
                        actionNode.getAction().getLinkList().clear();
                        StoryEditor.Instance().getProject().getActions().forEach(action -> {
                            Iterator<ActionLink> it = action.getLinkList().iterator();
                            while (it.hasNext()) {
                                ActionLink link = it.next();
                                if (link.getLinkToId() == actionNode.getAction().getId()) {
                                    it.remove();
                                }
                            }
                        });
                        actionViewport.getItems().remove(actionNode);
                        selectedNodes.remove(item);
                    });
                }
            }
        }
    }


    private class ActionLinkListChangeListener implements ListChangeListener<ActionLink> {

        @Override
        public void onChanged(Change<? extends ActionLink> c) {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(link -> {
                        LinkLine line = LinkLine.create(link, actionViewport);
                        line.setContextMenu(buildLinkContextMenu(line));
                        actionViewport.getItems().add(0, line);
                    });
                }

                if (c.wasRemoved()) {
                    c.getRemoved().forEach(link -> {
                        Iterator<Node> it = actionViewport.getItems().iterator();
                        while (it.hasNext()) {
                            Node node = it.next();
                            if (node instanceof LinkLine) {
                                LinkLine line = (LinkLine)node;
                                if (line.getLinkFrom().getAction().getId() == link.getLinkFromId()
                                        && line.getLinkTo().getAction().getId() == link.getLinkToId()) {
                                    it.remove();
                                }
                            }
                        }
                    });
                }
            }
        }
    }
}
