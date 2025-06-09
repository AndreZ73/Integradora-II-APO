package org.icesi.integradora_ii;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

import java.io.IOException;

public class HelloApplication extends Application {
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL musicUrl = getClass().getResource("/BackgroundMusic/music.mp3");
        if (musicUrl != null) {
            Media media = new Media(musicUrl.toExternalForm());
            mediaPlayer = new MediaPlayer(media);

            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);

            mediaPlayer.setVolume(0.5);

            mediaPlayer.play();
            System.out.println("Playing music: " + musicUrl.toExternalForm());
        } else {
            System.err.println("Music file not found: /music/game_theme.mp3. Make sure it's in src/main/resources/music/");
        }

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MenuController-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), bounds.getWidth(), bounds.getHeight());

        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                primaryStage.setFullScreen(false);
            }
        });

        primaryStage.setTitle("GTA_VI");
        primaryStage.setScene(scene);

        primaryStage.setFullScreen(true);

        Image logo = new Image(getClass().getResourceAsStream("/Logo/Logo.jpg"));
        primaryStage.getIcons().add(logo);

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}