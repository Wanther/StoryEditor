package org.nojob.storyeditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StoryEditor extends Application {

    private static StoryEditor sInstance;

    public static StoryEditor Instance() {
        return sInstance;
    }


    @Override
    public void init() throws Exception {
        super.init();

        sInstance = this;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("layout/main.fxml"));

        primaryStage.setTitle("Story Editor");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style/application.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("style/actionNode.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        // application stop, release resources and stop tasks
    }

    public static void main(String[] args) {
        launch(args);
    }
}
