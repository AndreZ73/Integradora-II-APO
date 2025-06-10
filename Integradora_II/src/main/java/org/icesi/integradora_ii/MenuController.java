package org.icesi.integradora_ii;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.application.Platform; // Importar Platform

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {

    @FXML
    private Button muteButton;

    @FXML
    private ImageView muteIcon;

    @FXML
    private BorderPane mainBorderPane;

    private static boolean isMuted = false;

    // Rutas a las imágenes de los iconos.
    // Confirma la ubicación de tus archivos de icono y ajusta las rutas si es necesario.
    // Basado en tu FXML, parecía que usabas /BackgroundMusic/ y un nombre específico.
    // Voy a mantener las rutas genéricas /Icons/ para claridad,
    // pero ASEGÚRATE de que las tuyas sean EXACTAS.
    private final String UNMUTE_ICON_PATH = "/Icons/unmute_icon.png"; // Icono para cuando el sonido está ON
    private final String MUTE_ICON_PATH = "/Icons/mute_icon.png";     // Icono para cuando el sonido está OFF


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // --- Lógica para cargar la imagen de fondo ---
        String imageUrlPath = "/Background/menu_background.png"; // Verifica si es .png o .jpg
        URL imageUrl = getClass().getResource(imageUrlPath);

        if (imageUrl != null) {
            mainBorderPane.setStyle(
                    "-fx-background-image: url('" + imageUrl.toExternalForm() + "'); " +
                            "-fx-background-size: cover; " +
                            "-fx-background-position: center center;"
            );
        } else {
            System.err.println("Error: Imagen de fondo no encontrada en " + imageUrlPath + ". Revisa la ruta y la extensión.");
        }

        // --- Lógica para el icono del botón de mute/unmute ---
        updateMuteIcon(); // Llama a este método para establecer la imagen inicial del icono
    }

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
            System.err.println("Error loading GameController-view.fxml: " + e.getMessage());
        }
    }

    @FXML
    public void showManual(ActionEvent event) {
        ManualViewer.toggleManualVisibility();
    }

    @FXML
    public void toggleMute(ActionEvent event) {
        if (HelloApplication.getMediaPlayer() != null) {
            isMuted = !isMuted;
            HelloApplication.getMediaPlayer().setMute(isMuted);
            updateMuteIcon();
        }
    }

    // Método NUEVO para salir de la aplicación
    @FXML
    public void exitApplication(ActionEvent event) {
        Platform.exit(); // Cierra la aplicación JavaFX
        System.exit(0); // Cierra el proceso JVM completamente
    }

    private void updateMuteIcon() {
        String iconPath = isMuted ? MUTE_ICON_PATH : UNMUTE_ICON_PATH;
        URL iconUrl = getClass().getResource(iconPath);

        if (iconUrl != null) {
            Image iconImage = new Image(iconUrl.toExternalForm());
            muteIcon.setImage(iconImage);
        } else {
            System.err.println("Error: Icono de mute no encontrado en " + iconPath + ". Revisa la ruta y la extensión.");
        }
    }

    public static boolean isMuted() {
        return isMuted;
    }

    public static void setMuted(boolean muted) {
        isMuted = muted;
    }
}