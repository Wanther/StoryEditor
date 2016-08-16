package org.nojob.storyeditor.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.nojob.storyeditor.StoryEditor;
import org.nojob.storyeditor.exception.AppException;
import org.nojob.storyeditor.model.Project;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    @FXML private TextField tbSearchInput;

    @FXML private Node progressOverlay;

    private ProjectController projectController;

    public void initialize() {
        onProjectChanged(null, StoryEditor.Instance().getProject());
        StoryEditor.Instance().projectProperty().addListener((observable, oldValue, newValue) -> {
            onProjectChanged(oldValue, newValue);
        });
        miSave.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));

        tbSearchInput.setOnKeyPressed((KeyEvent e) -> {
            if (e.getCode() == KeyCode.ENTER) {
                onSearch();
            }
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
            fileChooser.setInitialFileName(StoryEditor.Instance().getProject().getRootDir().getName());
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Story File", "*.zip"));
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
        if (projectController != null) {
            projectController.onZoomIn();
        }
    }

    @FXML
    protected void onZoomOut() {
        if (projectController != null) {
            projectController.onZoomOut();
        }
    }

    @FXML
    protected void onSearch() {
        String searchString = tbSearchInput.getText();

        if (projectController != null) {
            projectController.onSearch(searchString);
        }
    }

    @FXML
    protected void onHelp() {

    }

    @FXML
    protected void onAbout() {

    }

    protected void saveProject(OnOperateDoneListener l) {
        Project project = StoryEditor.Instance().getProject();
        if (project != null && !project.isLocked()) {
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
        tbSearchInput.setDisable(canNotDoProjectOperate);

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

            newProject.isLockedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    showProgressOverlay();
                } else {
                    hideProgressOverlay();
                }
            });
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

            mkdirResult = project.getSoundDir().mkdir();
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

            StoryEditor.Instance().getProject().setLocked(true);
        }

        @Override
        protected Void call() throws Exception {
            Project project = StoryEditor.Instance().getProject();

            try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(exportFile))) {
                out.putNextEntry(new ZipEntry("story_zh-CN.json"));
                byte[] buffer = project.toJSONObject().toString().getBytes();
                out.write(buffer, 0, buffer.length);
                out.closeEntry();

                File[] files = project.getSoundDir().listFiles((dir, name) -> name.endsWith(".mp3") || name.endsWith(".MP3"));
                if (files != null) {
                    for (File f : files) {
                        out.putNextEntry(new ZipEntry("resources/audio/" + f.getName()));
                        try(InputStream in = new FileInputStream(f)) {
                            int len;
                            while ((len = in.read(buffer)) != -1) {
                                out.write(buffer, 0, len);
                            }
                        }
                        out.closeEntry();
                    }
                }

            }

            return null;
        }

        @Override
        protected void failed() {
            StoryEditor.Instance().getProject().setLocked(false);
            StoryEditor.Instance().catchException(getException());
        }

        @Override
        protected void succeeded() {
            StoryEditor.Instance().getProject().setLocked(false);
            new Alert(Alert.AlertType.INFORMATION, "导出成功").show();
        }
    }

    private class SaveProjectTask extends Task<Void> {

        private OnOperateDoneListener listener;

        public SaveProjectTask(OnOperateDoneListener l) {
            listener = l;
            StoryEditor.Instance().getProject().setLocked(true);
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

            Thread.sleep(200);

            return null;
        }

        @Override
        protected void failed() {
            StoryEditor.Instance().getProject().setLocked(false);
            StoryEditor.Instance().catchException(getException());
        }

        @Override
        protected void succeeded() {
            StoryEditor.Instance().getProject().setLocked(false);
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
