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

    // Posición de la cámara en el mundo del juego (coordenadas sin zoom)
    private double cameraX = 0;
    private double cameraY = 0;
    private double cameraSpeed = 5;

    // Nivel de zoom: 1.0 es el tamaño normal.
    // Valores mayores a 1.0 (ej. 2.0) acercan la imagen (zoom in).
    // Valores menores a 1.0 (ej. 0.5) alejan la imagen (zoom out).
    private double zoomLevel = 1.0;
    private final double MAX_ZOOM = 10.0; // ¡HE CAMBIADO ESTE VALOR PARA MUCHO MÁS ZOOM!
    private final double MIN_ZOOM = 0.3; // Mínimo alejamiento (0.3 veces más pequeño)
    private final double ZOOM_INCREMENT = 0.1; // Cuánto cambia el zoom con cada pulsación

    private double wallpaperWidth;
    private double wallpaperHeight;

    // Banderas para el estado de las teclas de movimiento
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

        // Es esencial para que el canvas pueda recibir eventos de teclado
        canvas.setFocusTraversable(true);

        initEvents(); // Inicializa los manejadores de eventos

        // Asegura que las propiedades del canvas se ajusten al tamaño de la escena
        Platform.runLater(() -> {
            if (canvas.getScene() != null) {
                canvas.widthProperty().bind(canvas.getScene().widthProperty());
                canvas.heightProperty().bind(canvas.getScene().heightProperty());
            }

            // Inicia el bucle principal del juego en un nuevo hilo
            new Thread(
                    () -> {
                        while (true) {
                            try {
                                Platform.runLater(() -> {
                                    updateCamera(); // Actualiza la lógica de la cámara (movimiento y límites)
                                    paintGame();    // Dibuja todos los elementos del juego
                                });
                                Thread.sleep(17); // Pausa para lograr aproximadamente 60 FPS (1000ms / 60 frames ≈ 16.67ms)
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt(); // Restaura el estado de interrupción
                                System.err.println("Game loop interrupted: " + e.getMessage());
                                break; // Sale del bucle si el hilo es interrumpido
                            }
                        }
                    }
            ).start();
        });
    }

    /**
     * Configura los manejadores de eventos para el teclado.
     */
    public void initEvents(){
        // Manejador de eventos para cuando una tecla es presionada
        canvas.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W: W_PRESSED = true; break; // Mover hacia arriba
                case A: A_PRESSED = true; break; // Mover hacia la izquierda
                case S: S_PRESSED = true; break; // Mover hacia abajo
                case D: D_PRESSED = true; break; // Mover hacia la derecha
                case ADD: // Tecla '+' del teclado numérico para acercar
                case PLUS: // Tecla '+' de la parte superior del teclado para acercar
                    zoomLevel = Math.min(MAX_ZOOM, zoomLevel + ZOOM_INCREMENT);
                    break;
                case SUBTRACT: // Tecla '-' del teclado numérico para alejar
                case MINUS: // Tecla '-' de la parte superior del teclado para alejar
                    zoomLevel = Math.max(MIN_ZOOM, zoomLevel - ZOOM_INCREMENT);
                    break;
                default: break; // Ignorar otras teclas
            }
            // Asegura que el canvas siempre tenga el foco para seguir recibiendo eventos de teclado
            canvas.requestFocus();
            event.consume(); // Consumir el evento para que no se propague a otros nodos
        });

        // Manejador de eventos para cuando una tecla es liberada
        canvas.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W: W_PRESSED = false; break;
                case A: A_PRESSED = false; break;
                case S: S_PRESSED = false; break;
                case D: D_PRESSED = false; break;
                default: break; // Ignorar otras teclas
            }
            event.consume(); // Consumir el evento
        });
    }

    /**
     * Actualiza la posición de la cámara y asegura que se mantenga dentro de los límites del mapa,
     * considerando el nivel de zoom actual.
     */
    private void updateCamera() {
        // Aplica el movimiento de la cámara basado en las teclas presionadas
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

        // Calcula el ancho y alto del área del mundo que es visible en el canvas con el zoom actual.
        // Esto es la "ventana" a través de la cual vemos el mapa.
        double visibleWorldWidth = canvas.getWidth() / zoomLevel;
        double visibleWorldHeight = canvas.getHeight() / zoomLevel;

        // Restringe la posición de la cámara (cameraX, cameraY) para que la "ventana" visible del mundo
        // siempre se mantenga dentro de los límites del wallpaper.
        // Math.max(0, ...) asegura que no se vaya a la izquierda o arriba de 0.
        // Math.min(..., wallpaperWidth - visibleWorldWidth) asegura que el lado derecho o inferior
        // de la ventana visible no exceda el borde del wallpaper.
        cameraX = Math.max(0, Math.min(cameraX, wallpaperWidth - visibleWorldWidth));
        cameraY = Math.max(0, Math.min(cameraY, wallpaperHeight - visibleWorldHeight));

        // Si el wallpaper es más pequeño que el área visible en la pantalla (debido a un zoom out muy grande
        // o un canvas muy grande), centra la cámara para que el wallpaper se muestre en el centro del canvas.
        if (visibleWorldWidth > wallpaperWidth) {
            cameraX = (wallpaperWidth - visibleWorldWidth) / 2;
        }
        if (visibleWorldHeight > wallpaperHeight) {
            cameraY = (wallpaperHeight - visibleWorldHeight) / 2;
        }
    }

    /**
     * Dibuja los elementos del juego en el canvas, aplicando la cámara y el zoom.
     */
    private void paintGame() {
        // Limpia completamente el canvas en cada frame antes de dibujar de nuevo
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // --- Configuración para el Zoom ---
        // sourceWidth y sourceHeight determinan qué tan grande es la "porción" del wallpaper
        // que se va a dibujar. Al dividir por el zoomLevel, si el zoom es 2.0 (acercamiento),
        // sourceWidth y sourceHeight serán la mitad del tamaño del canvas, lo que significa
        // que una sección más pequeña del mapa se estira para llenar la pantalla.
        double sourceWidth = canvas.getWidth() / zoomLevel;
        double sourceHeight = canvas.getHeight() / zoomLevel;

        // sourceX y sourceY son las coordenadas de la esquina superior izquierda de la porción del wallpaper
        // que la cámara está viendo. Estas son las coordenadas del mundo sin zoom.
        double sourceX = cameraX;
        double sourceY = cameraY;

        // --- Asegurar límites de dibujo ---
        // Estos cálculos garantizan que la porción que intentamos dibujar (source)
        // no se salga de los límites reales de la imagen del wallpaper.
        sourceX = Math.max(0, sourceX);
        sourceY = Math.max(0, sourceY);
        sourceWidth = Math.min(sourceWidth, wallpaperWidth - sourceX);
        sourceHeight = Math.min(sourceHeight, wallpaperHeight - sourceY);

        // --- Configuración para el destino (el canvas) ---
        // destX, destY, destWidth, destHeight son las coordenadas y el tamaño en el canvas
        // donde se dibujará la porción del wallpaper. Siempre queremos que ocupe todo el canvas,
        // empezando en (0,0) del canvas.
        double destX = 0;
        double destY = 0;
        double destWidth = canvas.getWidth();
        double destHeight = canvas.getHeight();

        // Dibuja el wallpaper en el canvas.
        // Los primeros 4 parámetros definen la porción de la imagen original a dibujar (el "recorte").
        // Los últimos 4 parámetros definen dónde y con qué tamaño se dibuja esa porción en el canvas.
        gc.drawImage(wallpaper,
                sourceX, sourceY, sourceWidth, sourceHeight,
                destX, destY, destWidth, destHeight);

        // --- Dibujar otros elementos del juego con la cámara y el zoom ---
        // Si tienes otros objetos (jugador, enemigos, etc.) que se dibujan en el mundo,
        // necesitarás aplicar transformaciones al GraphicsContext para que se escalen y se muevan
        // correctamente con la cámara y el zoom.
        gc.save(); // Guarda el estado actual del GraphicsContext (sin transformaciones)

        // Aplica el escalado (zoom) y luego la traslación de la cámara.
        // Los objetos se dibujan en sus coordenadas del mundo (playerX, playerY, etc.).
        // Las transformaciones convierten esas coordenadas del mundo en coordenadas de pantalla.
        gc.translate(-cameraX, -cameraY); // Mueve el "origen" del dibujo para simular el movimiento de la cámara
        gc.scale(zoomLevel, zoomLevel);   // Aplica el escalado global a todo lo que se dibuje después

        // EJEMPLO: Si tuvieras un jugador con coordenadas playerX, playerY y un tamaño playerSize
        /*
        Image playerImage = new Image(getClass().getResourceAsStream("/Player/player_sprite.png"));
        double playerX = 100; // Coordenada X del jugador en el mundo
        double playerY = 150; // Coordenada Y del jugador en el mundo
        double playerSize = 50; // Tamaño del jugador

        // Dibuja el jugador. Sus coordenadas playerX, playerY ahora se ven afectadas por gc.translate y gc.scale
        gc.drawImage(playerImage, playerX, playerY, playerSize, playerSize);
        */

        gc.restore(); // Restaura el estado del GraphicsContext a como estaba antes de las transformaciones
        // Esto es CRUCIAL para que las transformaciones no afecten dibujos posteriores (como UI)
    }
}