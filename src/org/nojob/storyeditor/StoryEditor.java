package org.nojob.storyeditor;

import javafx.application.Application;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.nojob.storyeditor.model.Project;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class StoryEditor extends Application {

    private static StoryEditor sInstance;

    public static StoryEditor Instance() {
        return sInstance;
    }

    private ObjectProperty<Project> project;

    private ExecutorService backgroundExecutor;

    public ObjectProperty<Project> projectProperty() {
        if (project == null) {
            project = new SimpleObjectProperty<>(this, "project");
        }
        return project;
    }

    public Project getProject() {
        return projectProperty().get();
    }

    public void setProject(Project project) {
        this.projectProperty().set(project);
    }

    public Future<?> async(Task<?> task) {
        backgroundExecutor.execute(task);
        return task;
    }

    @Override
    public void init() throws Exception {
        super.init();

        sInstance = this;

        backgroundExecutor = Executors.newCachedThreadPool();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("layout/main.fxml"));

        primaryStage.setTitle("剧情编辑器");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/icon_app.png")));
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        backgroundExecutor.shutdown();

        while (!backgroundExecutor.isTerminated()) {
            backgroundExecutor.awaitTermination(1, TimeUnit.SECONDS);
        }
    }

    public void catchException(Throwable e) {
        e.printStackTrace();
        new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
