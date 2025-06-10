package org.icesi.integradora_ii;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image; // Necesario si usas imágenes de fondo en initialize
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

public class MenuController {

    @FXML
    private Button muteButton;
    private static boolean isMuted = false;

    @FXML
    public void EnterGame(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GameController-view.fxml"));
            Scene gameScene = new Scene(fxmlLoader.<Parent>load());

            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.setScene(gameScene);
            currentStage.setTitle("SGMMS - Game");

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading game-view.fxml: " + e.getMessage());
        }
    }

    @FXML
    public void showManual(ActionEvent event) {
        ManualViewer.toggleManualVisibility();
    }

    @FXML
    public void toggleMute(ActionEvent event) {
        if (HelloApplication.getMediaPlayer() != null) {
            isMuted = !isMuted; // Invierte el estado de mute
            HelloApplication.getMediaPlayer().setMute(isMuted);
            muteButton.setText(isMuted ? "Sonido" : "Silenciar");
        }
    }

    public static boolean isMuted() {
        return isMuted;
    }

    public static void setMuted(boolean muted) {
        isMuted = muted;
    }


}