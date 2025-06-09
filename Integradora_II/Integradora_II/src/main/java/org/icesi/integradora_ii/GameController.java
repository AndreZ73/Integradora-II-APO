package org.icesi.integradora_ii;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

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
                canvas.widthProperty().bind(canvas.getScene().widthProperty());
                canvas.heightProperty().bind(canvas.getScene().heightProperty());
            }

            new Thread(
                    () -> {
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
                    }
            ).start();
        });
    }

    public void initEvents(){
        canvas.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W: W_PRESSED = true; break;
                case A: A_PRESSED = true; break;
                case S: S_PRESSED = true; break;
                case D: D_PRESSED = true; break;
                default: break; // Ignore other keys
            }
            canvas.requestFocus();
            event.consume();
        });

        canvas.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W: W_PRESSED = false; break;
                case A: A_PRESSED = false; break;
                case S: S_PRESSED = false; break;
                case D: D_PRESSED = false; break;
                default: break; // Ignore other keys
            }
            event.consume();
        });
    }

    private void updateCamera() {
        if (W_PRESSED) {
            cameraY -= cameraSpeed;
        }
        if (S_PRESSED) {
            cameraY += cameraSpeed;
        }
        if (A_PRESSED) {
            cameraX -= cameraSpeed;
        }
        if (D_PRESSED) {
            cameraX += cameraSpeed;
        }

        cameraX = Math.max(0, Math.min(cameraX, wallpaperWidth - canvas.getWidth()));
        cameraY = Math.max(0, Math.min(cameraY, wallpaperHeight - canvas.getHeight()));

        if (canvas.getWidth() > wallpaperWidth) {
            cameraX = (wallpaperWidth - canvas.getWidth()) / 2;
        }
        if (canvas.getHeight() > wallpaperHeight) {
            cameraY = (wallpaperHeight - canvas.getHeight()) / 2;
        }
    }

    private void paintGame() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double sourceX = cameraX;
        double sourceY = cameraY;
        double sourceWidth = canvas.getWidth();
        double sourceHeight = canvas.getHeight();

        sourceX = Math.max(0, sourceX);
        sourceY = Math.max(0, sourceY);
        sourceWidth = Math.min(sourceWidth, wallpaperWidth - sourceX);
        sourceHeight = Math.min(sourceHeight, wallpaperHeight - sourceY);

        double destX = 0;
        double destY = 0;
        double destWidth = canvas.getWidth();
        double destHeight = canvas.getHeight();

        gc.drawImage(wallpaper,
                sourceX, sourceY, sourceWidth, sourceHeight,
                destX, destY, destWidth, destHeight);

        gc.save();
        gc.translate(-cameraX, -cameraY);

        gc.restore();
    }
}