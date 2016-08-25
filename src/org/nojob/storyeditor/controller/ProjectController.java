package org.nojob.storyeditor.controller;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.exception.AppException;
import org.nojob.storyeditor.model.ActionItem;
import org.nojob.storyeditor.model.ActionLink;
import org.nojob.storyeditor.model.Project;
import org.nojob.storyeditor.model.StoryAction;
import org.nojob.storyeditor.view.ActionNode;
import org.nojob.storyeditor.view.LinkLine;
import org.nojob.storyeditor.view.Viewport;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

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

        actionViewport.setContextMenu(buildViewportContextMenu());

        initializeActions();

        selectedNodes.addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                while (c.next()) {
                    if (c.wasAdded()) {
                        c.getAddedSubList().forEach(item -> {
                            item.getStyleClass().add("selected");
                            if (item instanceof ActionNode) {
                                item.toFront();
                            }
                        });
                    }

                    if (c.wasRemoved()) {
                        c.getRemoved().forEach(item -> {
                            item.getStyleClass().remove("selected");
                        });
                    }
                }
            }
        });

        actionViewport.setOnMouseClicked((MouseEvent e) -> {
            selectedNodes.clear();
        });
    }

    public void onZoomIn() {
        actionViewport.zoomIn();
    }

    public void onZoomOut() {
        actionViewport.zoomOut();
    }

    public void onCopy() {
        copySelected();
    }

    public void onPaste() {
        pasteCoppied(null, null);
    }

    public void onSearch(String queryString) {
        actionViewport.lookupAll(".searched").forEach(actionNode -> {
            actionNode.getStyleClass().remove("searched");
        });
        if (queryString == null || "".equals(queryString.trim())) {
            return;
        }
        StoryEditor.Instance().async(new SearchTask(queryString));
    }

    protected void copySelected() {
        if (!selectedNodes.isEmpty() && selectedNodes.get(0) instanceof ActionNode) {
            ActionNode source = (ActionNode) selectedNodes.get(0);
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent cc = new ClipboardContent();
            cc.putString(source.getId());
            clipboard.setContent(cc);
        }
    }

    protected void pasteCoppied(Double newX, Double newY) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        if (clipboard.hasString()) {
            String content = clipboard.getString();
            if (content.startsWith("action_")) {
                ActionNode actionNode = (ActionNode)actionViewport.lookup("#" + content);
                if (actionNode != null) {
                    StoryAction source = actionNode.getAction();
                    StoryAction action = source.copy(StoryEditor.Instance().getProject());
                    if (newX == null) {
                        newX = action.getX() + StoryAction.COPY_PASTE_DELTA_XY;
                    }
                    if (newY == null) {
                        newY = action.getY() + StoryAction.COPY_PASTE_DELTA_XY;
                    }
                    action.setX(newX);
                    action.setY(newY);

                    StoryEditor.Instance().getProject().getActions().add(action);
                }
            }
        }
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

        item = new MenuItem("粘贴");
        item.setOnAction(e -> {
            MenuItem clicked = (MenuItem)e.getSource();
            Point2D popup = actionViewport.getControlGroup().screenToLocal(new Point2D(clicked.getParentPopup().getAnchorX(), clicked.getParentPopup().getAnchorY()));
            pasteCoppied(popup.getX(), popup.getY());
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

        item = new MenuItem("属性");
        item.getProperties().put("target_action", actionPane.getAction());
        item.setOnAction(e -> {
            MenuItem clicked = (MenuItem)e.getSource();
            showActionDetail((StoryAction) clicked.getProperties().get("target_action"));
        });
        menu.getItems().add(item);

        item = new MenuItem("复制");
        item.getProperties().put("target_action", actionPane.getAction());
        item.setOnAction(e -> copySelected());
        menu.getItems().add(item);

        item = new MenuItem("删除");
        item.getProperties().put("target_action", actionPane);
        item.setOnAction(e -> {
            new Alert(Alert.AlertType.CONFIRMATION, "确定删除?").showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
                MenuItem clicked = (MenuItem)e.getSource();
                ActionNode ap = (ActionNode)clicked.getProperties().get("target_action");
                StoryEditor.Instance().getProject().getActions().remove(ap.getAction());
            });
        });
        menu.getItems().add(item);

        return menu;
    }

    protected ContextMenu buildLinkContextMenu(LinkLine line) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem item = new MenuItem("删除连接");
        item.getProperties().put("target_line", line);
        item.setOnAction(e -> {
            new Alert(Alert.AlertType.CONFIRMATION, "确定删除?").showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
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
        });
        contextMenu.getItems().add(item);

        return contextMenu;
    }

    protected void requestCreateLink(ActionNode actionNode) {

        if (actionNode.getAction().getLinkList().size() >= 2) {
            StoryEditor.Instance().catchException(new AppException("该节点连接数超过上限"));
            return;
        }

        ActionLink link = new ActionLink();
        link.setLinkFromId(actionNode.getAction().getId());
        ActionLinkController.create(link, actionViewport.getScene().getWindow()).showAndWait().ifPresent(this::createLinkLine);
    }

    protected void showLinkDetail(ActionLink link) {
        ActionLinkController.create(link, actionViewport.getScene().getWindow()).showAndWait().ifPresent(this::updateLinkLine);
    }

    protected void createLinkLine(ActionLink link) {
        StoryEditor.Instance().getProject().getActions().stream().filter(item -> item.getId() == link.getLinkFromId()).findFirst().ifPresent(item -> {
            if (item.getLinkList().size() >= 2) {
                StoryEditor.Instance().catchException(new AppException("该节点连接数超过上限"));
                return;
            }

            if (item.getLinkList().contains(link)) {
                StoryEditor.Instance().catchException(new AppException("连接已存在"));
                return;
            }

            item.getLinkList().add(link);
        });
    }

    protected void updateLinkLine(ActionLink link) {
        StoryEditor.Instance().getProject().getActions().stream().filter(item -> item.getId() == link.getLinkFromId()).findFirst().ifPresent(item -> {
            item.getLinkList().stream().filter(lk -> lk.equals(link)).findFirst().ifPresent(lk -> {
                lk.setText(link.getText());
                lk.setFoundedClueId(link.getFoundedClueId());
            });
        });
    }

    protected void updateActionNode(StoryAction action) {
        StoryAction oldAction = StoryEditor.Instance().getProject().findActionById(action.getId());
        if (oldAction == null) {
            throw new RuntimeException("oldAction = null");
        } else {
            oldAction.merge(action);
        }
    }

    protected void initializeActions() {
        Project project = StoryEditor.Instance().getProject();

        if (project == null) {
            return;
        }

        project.getActions().forEach(action -> actionViewport.getItems().add(createActionNode(action)));
        project.getActions().addListener(actionListChangeListener);

        project.getActions().forEach(action -> {
            action.getLinkList().forEach(link -> {
                LinkLine line = LinkLine.create(link, actionViewport);
                line.setContextMenu(buildLinkContextMenu(line));
                line.setOnMouseClicked((MouseEvent e) -> {
                    if (e.getClickCount() >= 2) {
                        showLinkDetail(link);
                    } else {
                        selectedNodes.clear();
                        selectedNodes.add(line);
                    }
                });
                actionViewport.getItems().add(0, line);
            });

            action.getLinkList().addListener(actionLinkListChangeListener);
        });
    }

    protected ActionNode createActionNode(StoryAction action) {
        ActionNode actionNode = ActionNode.create(action);
        actionNode.setContextMenu(buildActionContextMenu(actionNode));
        actionNode.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getClickCount() >= 2) {
                showActionDetail(action);
            }
        });
        actionNode.setOnMousePressed((MouseEvent e) -> {
            selectedNodes.clear();
            selectedNodes.add(actionNode);
        });
        return actionNode;
    }

    protected void showActionDetail(StoryAction action) {
        ActionDetailController.createDialog(action, actionViewport.getScene().getWindow()).showAndWait().ifPresent(this::updateActionNode);
    }

    private class ActionListChangeListener implements ListChangeListener<StoryAction> {

        @Override
        public void onChanged(Change<? extends StoryAction> c) {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(item -> actionViewport.getItems().add(createActionNode(item)));
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
                        selectedNodes.remove(actionNode);
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
                        line.setOnMouseClicked((MouseEvent e) -> {
                            if (e.getClickCount() >= 2) {
                                showLinkDetail(link);
                            }
                        });
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

    private class SearchTask extends Task<List<StoryAction>> {
        private String searchString;

        public SearchTask(String searchString) {
            StoryEditor.Instance().getProject().setLocked(true);
            this.searchString = searchString;
        }

        @Override
        protected List<StoryAction> call() throws Exception {
            return StoryEditor.Instance().getProject().getActions().stream().filter(action -> {
                if (action.getKeyActionText() != null && action.getKeyActionText().contains(searchString)) {
                    return true;
                }

                for (ActionItem item : action.getItemList()) {
                    if (item.getText() != null && item.getText().contains(searchString)) {
                        return true;
                    }

                    if (item.getEvent() != null && item.getEvent().getText().contains(searchString)) {
                        return true;
                    }

                    if (item.getClue() != null && item.getClue().getText().contains(searchString)) {
                        return true;
                    }

                    if (item.getSound() != null && item.getSound().contains(searchString)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
        }

        @Override
        protected void failed() {
            StoryEditor.Instance().getProject().setLocked(false);
            StoryEditor.Instance().catchException(getException());
        }

        @Override
        protected void succeeded() {

            List<StoryAction> actions = getValue();

            if (actions != null) {
                actions.stream().forEach(action -> {
                    actionViewport.lookup("#" + ActionNode.ID_PREFIX + action.getId()).getStyleClass().add("searched");
                });
            }

            StoryEditor.Instance().getProject().setLocked(false);
        }
    }
}
