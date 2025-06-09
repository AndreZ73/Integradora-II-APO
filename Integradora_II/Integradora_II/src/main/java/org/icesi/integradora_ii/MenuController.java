package org.icesi.integradora_ii;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.input.KeyCode;

import java.io.IOException;

public class MenuController {

    private Stage stage;
    private Scene scene;
    private Parent root;


    public void EnterGame(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GameController-view.fxml"));
        root = loader.load();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        scene = new Scene(root, bounds.getWidth(), bounds.getHeight());
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);

        if (!stage.isFullScreen()) {
            stage.setFullScreen(true);
        }

        scene.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                stage.setFullScreen(false);
            }
        });
        stage.show();
    }
}