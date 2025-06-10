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
    // Hacemos isMuted estático para que GameController pueda acceder a su estado
    private static boolean isMuted = false;

    // Métodos para el FXML:

    @FXML
    public void EnterGame(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GameController-view.fxml"));
            Scene gameScene = new Scene(fxmlLoader.<Parent>load());

            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.setScene(gameScene);
            currentStage.setTitle("SGMMS - Game");
            // No se pone en pantalla completa aquí para que GameController lo gestione si es necesario
            // currentStage.setFullScreen(true); // Esta línea ya no es necesaria aquí
            // currentStage.setFullScreenExitHint(""); // Esta línea ya no es necesaria aquí

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading game-view.fxml: " + e.getMessage());
        }
    }

    @FXML
    public void showManual(ActionEvent event) {
        // Llama al método correcto en ManualViewer
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

    // Métodos estáticos para acceder al estado de mute desde otras clases
    public static boolean isMuted() {
        return isMuted;
    }

    public static void setMuted(boolean muted) {
        isMuted = muted;
    }


}