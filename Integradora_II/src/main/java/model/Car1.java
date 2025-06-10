package model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Car1 extends Thread {
    private Canvas canvas;
    private GraphicsContext gc;

    private int frame;
    private int x;
    private int y;
    private int w;
    private int h;

    //0: North
    //1:South
    //2:East
    //3: West
    //4: NE
    //5: NW
    private int state;

    private ArrayList<Image> north;
    private ArrayList<Image> west;
    private Image NE;
    private Image NW;

    public Car1(Canvas canvas, int x, int y, int w, int h) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.state = 0;

        north = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            String filename = String.format("/Car1/North/POLICE_CLEAN_NORTH_%03d.png", i);
            north.add(new Image(Car1.class.getResourceAsStream(filename)));
        }

        west = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            String filename = String.format("/Car1/West/POLICE_CLEAN_WEST_%03d.png", i);
            west.add(new Image(Car1.class.getResourceAsStream(filename)));
        }

        NE = new Image(Car1.class.getResourceAsStream("/Car1/POLICE_CLEAN_NORTHWEST_000.png"));
    }

    @Override
    public void run() {
        // Animación o movimiento automático
        initial();
    }

    public boolean leaveInitialPosition() {
        if(x == 176 && y == 580) {
            while(x > 58) {
                try {
                    x--;
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            state = 5;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            state = 0;
            x = 51;
            while (y > 465) {
                try {
                    y--;
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return true;
        }
        return false;
    }

    public void initial() {
        if(x == 176 && y == 580) {
            state = 3;
            if(leaveInitialPosition()) {
                autoMove();
            }
        }
    }

    public void autoMove() {

    }

    public void paint() {
        // Solo dibuja si hay imágenes cargadas
        if (!north.isEmpty() && state == 0) {
            gc.drawImage(north.get(frame % north.size()), x, y, w, h);
            frame++;
        } else if(!west.isEmpty() && state == 3) {
            gc.drawImage(west.get(frame % west.size()), x, y, w, h);
            frame++;
        } else if(NE != null && state == 5) {
            gc.drawImage(NE, x, y, w, h);
        }
    }


}
