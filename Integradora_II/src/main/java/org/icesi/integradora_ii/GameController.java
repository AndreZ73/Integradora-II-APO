package org.icesi.integradora_ii;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView; // Importar ImageView
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import model.Car1;

import java.net.URL;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    private Image wallpaper;

    @FXML
    private Canvas canvas;
    private GraphicsContext gc;

    private double cameraX = 0;
    private double cameraY = 0;
    private double cameraSpeed = 5;

    private double wallpaperWidth;
    private double wallpaperHeight;

    private boolean W_PRESSED = false;
    private boolean A_PRESSED = false;
    private boolean S_PRESSED = false;
    private boolean D_PRESSED = false;

    private double scaleFactor = 1.0;
    private final double SCALE_STEP = 0.1;
    private double MIN_SCALE = 0.5;

    private Car1 car1;

    @FXML
    private Button muteButton; // Este botón debe estar en tu FXML con fx:id="muteButton"

    // YA NO NECESITAMOS @FXML para gameMuteIcon, porque lo crearemos en Java
    private ImageView gameMuteIcon; // Eliminamos @FXML y lo hacemos privado sin inyección

    @FXML
    private Button gameManualButton; // Si tienes este botón en tu FXML

    // Rutas a las imágenes de los iconos de mute/unmute.
    // **CRÍTICO: ASEGÚRATE de que estas rutas son EXACTAS y que los archivos existen en src/main/resources/Icons/**
    private final String UNMUTE_ICON_PATH = "/Icons/unmute_icon.png"; // Icono para sonido activado
    private final String MUTE_ICON_PATH = "/Icons/mute_icon.png";     // Icono para sonido desactivado


    @FXML
    private void toggleMute() {
        boolean isMuted = !MenuController.isMuted();
        if (HelloApplication.getMediaPlayer() != null) {
            HelloApplication.getMediaPlayer().setMute(isMuted);
        }
        MenuController.setMuted(isMuted); // Actualiza el estado global en MenuController
        updateGameMuteIcon(); // Llama para actualizar la imagen del icono
        canvas.requestFocus(); // Mantiene el foco en el canvas
    }

    @FXML
    private void showGameManual() {
        ManualViewer.toggleManualVisibility();
        canvas.requestFocus();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gc = canvas.getGraphicsContext2D();

        wallpaper = new Image(getClass().getResourceAsStream("/Background/map.png"));
        if (wallpaper.isError()) {
            System.err.println("Error loading map.png: " + wallpaper.getException());
        }

        wallpaperWidth = wallpaper.getWidth();
        wallpaperHeight = wallpaper.getHeight();

        initEvents();

        Platform.runLater(() -> {
            if (canvas.getScene() != null) {
                Stage stage = (Stage) canvas.getScene().getWindow();
                if (!stage.isFullScreen()) {
                    stage.setFullScreen(true);
                }

                canvas.widthProperty().addListener((obs, oldVal, newVal) -> updateMinScale());
                canvas.heightProperty().addListener((obs, oldVal, newVal) -> updateMinScale());

                canvas.widthProperty().bind(canvas.getScene().widthProperty());
                canvas.heightProperty().bind(canvas.getScene().heightProperty());

                updateMinScale();
            }

            // --- CÓDIGO NUEVO PARA EL BOTÓN DE MUTE SIN TOCAR EL FXML ---
            // Creamos el ImageView dinámicamente
            gameMuteIcon = new ImageView();
            gameMuteIcon.setFitHeight(26.0); // Ajusta estas dimensiones si tu icono es diferente
            gameMuteIcon.setFitWidth(33.0);  // Ajusta estas dimensiones si tu icono es diferente
            gameMuteIcon.setPickOnBounds(true);
            gameMuteIcon.setPreserveRatio(true);

            // Asignamos el ImageView al botón de mute que sí se inyecta desde el FXML
            if (muteButton != null) {
                muteButton.setGraphic(gameMuteIcon);
                // Si quieres que el botón sea transparente como antes, también puedes forzar el estilo aquí
                muteButton.setStyle("-fx-background-color: transparent;");
            } else {
                System.err.println("Error: muteButton no fue inyectado. Asegúrate de que tu GameController-view.fxml tiene un Button con fx:id=\"muteButton\".");
            }
            // -----------------------------------------------------------

            // Establece la imagen correcta del icono de mute al inicio del juego.
            updateGameMuteIcon();


            new Thread(() -> {
                while (true) {
                    try {
                        Platform.runLater(() -> {
                            updateCamera();
                            paintGame();
                        });
                        Thread.sleep(17);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Game loop interrupted: " + e.getMessage());
                        break;
                    }
                }
            }).start();
        });

        car1 = new Car1(canvas, 176, 580, 50, 50);
        car1.start();
    }

    // Método para actualizar la imagen del icono de mute en el juego
    private void updateGameMuteIcon() {
        // Solo intenta cambiar la imagen si gameMuteIcon fue creado
        if (gameMuteIcon != null) {
            String iconPath = MenuController.isMuted() ? MUTE_ICON_PATH : UNMUTE_ICON_PATH;
            URL iconUrl = getClass().getResource(iconPath);

            if (iconUrl != null) {
                Image iconImage = new Image(iconUrl.toExternalForm());
                gameMuteIcon.setImage(iconImage); // Establece la imagen en el ImageView creado dinámicamente
            } else {
                System.err.println("Error: Icono de mute del juego no encontrado en " + iconPath + ". Revisa la ruta y la extensión.");
            }
        }
    }

    public void initEvents() {
        canvas.setFocusTraversable(true);

        canvas.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> W_PRESSED = true;
                case A -> A_PRESSED = true;
                case S -> S_PRESSED = true;
                case D -> D_PRESSED = true;
                case F -> {
                    Stage stage = (Stage) canvas.getScene().getWindow();
                    stage.setFullScreen(!stage.isFullScreen());
                }
                default -> {}
            }
            canvas.requestFocus();
            event.consume();
        });

        canvas.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W -> W_PRESSED = false;
                case A -> A_PRESSED = false;
                case S -> S_PRESSED = false;
                case D -> D_PRESSED = false;
                default -> {}
            }
            event.consume();
        });

        canvas.setOnScroll((ScrollEvent event) -> {
            double mouseX = event.getX();
            double mouseY = event.getY();

            double mouseMapX = cameraX + mouseX / scaleFactor;
            double mouseMapY = cameraY + mouseY / scaleFactor;

            if (event.getDeltaY() > 0) {
                scaleFactor += SCALE_STEP;
            } else {
                scaleFactor -= SCALE_STEP;
            }

            scaleFactor = Math.max(MIN_SCALE, scaleFactor);

            double maxScaleX = wallpaperWidth / canvas.getWidth();
            double maxScaleY = wallpaperHeight / canvas.getHeight();
            double maxScale = Math.min(maxScaleX, maxScaleY);
            scaleFactor = Math.min(scaleFactor, maxScale * 2);

            cameraX = mouseMapX - (mouseX / scaleFactor);
            cameraY = mouseMapY - (mouseY / scaleFactor);

            constrainCamera();
            event.consume();
        });
    }

    private void updateMinScale() {
        if (canvas.getWidth() == 0 || canvas.getHeight() == 0) return;

        double scaleX = canvas.getWidth() / wallpaperWidth;
        double scaleY = canvas.getHeight() / wallpaperHeight; // Corregido: Removed redundant division
        MIN_SCALE = Math.max(scaleX, scaleY);

        if (scaleFactor < MIN_SCALE) {
            scaleFactor = MIN_SCALE;
        }

        constrainCamera();
    }

    private void updateCamera() {
        double effectiveSpeed = cameraSpeed / scaleFactor;

        if (W_PRESSED) cameraY -= effectiveSpeed;
        if (S_PRESSED) cameraY += effectiveSpeed;
        if (A_PRESSED) cameraX -= effectiveSpeed;
        if (D_PRESSED) cameraX += effectiveSpeed;

        constrainCamera();
    }

    private void constrainCamera() {
        double visibleWidth = canvas.getWidth() / scaleFactor;
        double visibleHeight = canvas.getHeight() / scaleFactor;

        if (wallpaperWidth <= visibleWidth) {
            cameraX = (wallpaperWidth - visibleWidth) / 2;
        } else {
            cameraX = Math.max(0, Math.min(cameraX, wallpaperWidth - visibleWidth));
        }

        if (wallpaperHeight <= visibleHeight) {
            cameraY = (wallpaperHeight - visibleHeight) / 2;
        } else {
            cameraY = Math.max(0, Math.min(cameraY, wallpaperHeight - visibleHeight));
        }
    }

    private void paintGame() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double sourceX = cameraX;
        double sourceY = cameraY;
        double sourceWidth = canvas.getWidth() / scaleFactor;
        double sourceHeight = canvas.getHeight() / scaleFactor;

        gc.drawImage(wallpaper,
                sourceX, sourceY, sourceWidth, sourceHeight,
                0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        gc.scale(scaleFactor, scaleFactor);
        gc.translate(-cameraX, -cameraY);
        car1.paint();
        gc.restore();
    }
}