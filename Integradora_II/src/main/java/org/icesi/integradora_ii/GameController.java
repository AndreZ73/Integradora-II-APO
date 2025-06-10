package org.icesi.integradora_ii;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Stage;
import model.Car1; // Asegúrate de que esta ruta a tu clase Car1 es correcta

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
    private Button muteButton;


    @FXML
    private void toggleMute() {
        boolean isMuted = !MenuController.isMuted();

        if (HelloApplication.getMediaPlayer() != null) {
            HelloApplication.getMediaPlayer().setMute(isMuted);
        }

        muteButton.setText(isMuted ? "Activar" : "Silenciar");
        MenuController.setMuted(isMuted);
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
                canvas.widthProperty().addListener((obs, oldVal, newVal) -> updateMinScale());
                canvas.heightProperty().addListener((obs, oldVal, newVal) -> updateMinScale());

                canvas.widthProperty().bind(canvas.getScene().widthProperty());
                canvas.heightProperty().bind(canvas.getScene().heightProperty());

                updateMinScale();
            }

            muteButton.setText(MenuController.isMuted() ? "Activar" : "Silenciar");


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
            event.consume(); // Consumir el evento
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
        double scaleY = canvas.getHeight() / wallpaperHeight;
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