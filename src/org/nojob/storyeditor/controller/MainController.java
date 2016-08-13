package org.nojob.storyeditor.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.exception.AppException;
import org.nojob.storyeditor.model.Project;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by wanghe on 16/7/13.
 */
public class MainController {

    interface OnOperateDoneListener{
        void done();
    }

    @FXML private BorderPane mainLayout;
    @FXML private MenuItem miNew;
    @FXML private MenuItem miOpen;
    @FXML private MenuItem miSave;
    @FXML private MenuItem miExport;
    @FXML private MenuItem miExit;
    @FXML private MenuItem miZoomIn;
    @FXML private MenuItem miZoomOut;

    @FXML private Button tbNew;
    @FXML private Button tbOpen;
    @FXML private Button tbSave;
    @FXML private Button tbExport;
    @FXML private Button tbZoomIn;
    @FXML private Button tbZoomOut;
    @FXML private Button tbSearch;

    @FXML private Node progressOverlay;

    private ProjectController projectController;

    public void initialize() {
        onProjectChanged(null, StoryEditor.Instance().getProject());
        StoryEditor.Instance().projectProperty().addListener((observable, oldValue, newValue) -> {
            onProjectChanged(oldValue, newValue);
        });
    }

    @FXML
    protected void onNew() {
        saveProject(() -> {
            Dialog<File> newProjectDialog = NewProjectController.createDialog(mainLayout.getScene().getWindow());
            newProjectDialog.showAndWait().ifPresent(response -> {
                StoryEditor.Instance().async(new CreateProjectTask(response));
            });
        });
    }

    @FXML
    protected void onOpen() {
        saveProject(() -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("选择工程目录");
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File projectRootDir = chooser.showDialog(mainLayout.getScene().getWindow());
            if (projectRootDir != null) {
                StoryEditor.Instance().async(new OpenProjectTask(projectRootDir));
            }
        });
    }

    @FXML
    protected void onSave() {
        saveProject(null);
    }

    @FXML
    protected void onExport() {
        saveProject(() -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("选择导出目录");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));
            File selectedFile = fileChooser.showSaveDialog(mainLayout.getScene().getWindow());
            if (selectedFile != null) {
                StoryEditor.Instance().async(new ExportProjectTask(selectedFile));
            }
        });
    }

    @FXML
    protected void onExit() {
        Platform.exit();
    }

    @FXML
    protected void onZoomIn() {

    }

    @FXML
    protected void onZoomOut() {

    }

    @FXML
    protected void onSearch() {

    }

    @FXML
    protected void onHelp() {

    }

    @FXML
    protected void onAbout() {

    }

    protected void saveProject(OnOperateDoneListener l) {
        Project project = StoryEditor.Instance().getProject();
        if (project != null) {
            StoryEditor.Instance().async(new SaveProjectTask(l));
        } else {
            if (l != null) {
                l.done();
            }
        }
    }

    protected void closeProject(OnOperateDoneListener l) {
        Project project = StoryEditor.Instance().getProject();
        if (project != null && project.isModified()) {
            new Alert(Alert.AlertType.WARNING, "是否保存所做的更改?", ButtonType.NO, ButtonType.CANCEL, ButtonType.YES)
                    .showAndWait()
                    .ifPresent(response -> {
                        if (response == ButtonType.NO) {
                            StoryEditor.Instance().setProject(null);
                            if (l != null) {
                                l.done();
                            }
                        } else if (response == ButtonType.YES) {
                            saveProject(() -> {
                                StoryEditor.Instance().setProject(null);
                                l.done();
                            });
                        }
                    });
        } else {
            StoryEditor.Instance().setProject(null);
            if (l != null) {
                l.done();
            }
        }
    }

    protected void onProjectChanged(Project oldProject, Project newProject) {
        boolean canNotDoProjectOperate = newProject == null;

        miSave.setDisable(canNotDoProjectOperate);
        miExport.setDisable(canNotDoProjectOperate);
        miZoomIn.setDisable(canNotDoProjectOperate);
        miZoomOut.setDisable(canNotDoProjectOperate);

        tbSave.setDisable(canNotDoProjectOperate);
        tbExport.setDisable(canNotDoProjectOperate);
        tbZoomIn.setDisable(canNotDoProjectOperate);
        tbZoomOut.setDisable(canNotDoProjectOperate);
        tbSearch.setDisable(canNotDoProjectOperate);

        if (newProject == null) {
            mainLayout.setCenter(null);
            projectController = null;
        } else {
            FXMLLoader loader = new FXMLLoader();
            loader.setBuilderFactory(new JavaFXBuilderFactory());
            loader.setLocation(StoryEditor.class.getResource("layout/project.fxml"));
            Parent projectPane = null;
            try {
                projectPane = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            projectController = loader.getController();

            mainLayout.setCenter(projectPane);
        }
    }

    protected void showProgressOverlay() {
        progressOverlay.setVisible(true);
    }

    protected void hideProgressOverlay() {
        progressOverlay.setVisible(false);
    }

    private class CreateProjectTask extends Task<Project> {

        private File rootDir;

        public CreateProjectTask(File rootDir) {
            this.rootDir = rootDir;

            showProgressOverlay();
        }

        @Override
        protected Project call() throws Exception {
            boolean mkdirResult = false;

            if (!rootDir.exists()) {
                mkdirResult = rootDir.mkdirs();
                if (!mkdirResult) {
                    throw new AppException("创建项目目录失败");
                }
            }

            if (!rootDir.isDirectory()) {
                throw new AppException("请选择一个目录");
            }

            String[] files = rootDir.list();
            for (String file : files) {
                if (!".".equals(file) && !"..".equals(file) && !".DS_Store".equals(file)) {
                    throw new AppException("请选择一个空目录作为项目目录");
                }
            }

            Project project = Project.create(rootDir);

            File resDir = project.getResDir();

            mkdirResult = resDir.mkdir();
            if (!mkdirResult) {
                throw new AppException("创建项目目录失败");
            }

            File projectDescFile = new File(rootDir, Project.PROJECT_FILE);
            projectDescFile.createNewFile();

            return project;
        }

        @Override
        protected void failed() {
            hideProgressOverlay();
            StoryEditor.Instance().catchException(getException());
        }

        @Override
        protected void succeeded() {
            hideProgressOverlay();
            StoryEditor.Instance().setProject(getValue());
        }
    }

    private class ExportProjectTask extends Task<Void> {

        private File exportFile;

        public ExportProjectTask(File exportFile) {
            this.exportFile = exportFile;

            showProgressOverlay();
        }

        @Override
        protected Void call() throws Exception {
            Project project = StoryEditor.Instance().getProject();

            OutputStream out = null;
            try {
                String jsonResult = project.toJSONObject().toString();
                out = new FileOutputStream(exportFile);
                byte[] jsonByte = jsonResult.getBytes();
                out.write(jsonByte, 0, jsonByte.length);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }

            return null;
        }

        @Override
        protected void failed() {
            hideProgressOverlay();
            StoryEditor.Instance().catchException(getException());
        }

        @Override
        protected void succeeded() {
            hideProgressOverlay();
        }
    }

    private class SaveProjectTask extends Task<Void> {

        private OnOperateDoneListener listener;

        public SaveProjectTask(OnOperateDoneListener l) {
            listener = l;
            showProgressOverlay();
        }

        @Override
        protected Void call() throws Exception {
            Project project = StoryEditor.Instance().getProject();

            String saveString = project.toSaveJSON().toString();

            OutputStream out = null;
            try {
                out = new FileOutputStream(new File(project.getRootDir(), Project.PROJECT_CONTENT));
                byte[] bytes = saveString.getBytes("UTF-8");
                out.write(bytes, 0, bytes.length);
            } finally {
                if (out != null) {
                    out.close();
                }
            }

            return null;
        }

        @Override
        protected void failed() {
            hideProgressOverlay();
            StoryEditor.Instance().catchException(getException());
        }

        @Override
        protected void succeeded() {
            hideProgressOverlay();
            StoryEditor.Instance().getProject().setModified(false);
            if (listener != null) {
                listener.done();
            }
        }
    }

    private class OpenProjectTask extends Task<Project> {

        private File projectRootDir;

        public OpenProjectTask(File projectRootDir) {
            showProgressOverlay();
            this.projectRootDir = projectRootDir;
        }

        @Override
        protected Project call() throws Exception {

            File projectDescFile = new File(projectRootDir, Project.PROJECT_FILE);
            if (!projectDescFile.exists()) {
                throw new AppException("该目录不是工程目录");
            }

            return Project.open(projectRootDir);
        }

        @Override
        protected void failed() {
            hideProgressOverlay();
            StoryEditor.Instance().catchException(getException());
        }

        @Override
        protected void succeeded() {
            hideProgressOverlay();
            StoryEditor.Instance().setProject(getValue());
        }
    }
}
