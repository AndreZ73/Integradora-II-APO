package model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Car1 extends Thread{
    private Canvas canvas;
    private GraphicsContext gc;

    private int frame;
    private int x;
    private int y;
    private int w;
    private int h;

    private int state;

    private ArrayList<Image> north;
    private ArrayList<Image> south;
    private ArrayList<Image> east;
    private ArrayList<Image> west;

    public Car1(Canvas canvas, int x, int y, int w, int h) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.state = 0;

        north = new ArrayList<>();
        //for (int i = 0; i < 11; i++) {
            Image image = new  Image(getClass().getResourceAsStream("/Car1/North/Red_COUPE_CLEAN_NORTH_000.png"));
            north.add(image);
        //}
    }


    public void move() {
        new Thread(() -> {
            while (y > 160) {
                try {
                    y--;
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
    }

    public void paint() {

        if(state == 1 || state == 0){
            gc.drawImage(north.get(frame%(north.size()-1)),x, y, h, w);
            frame++;
        }
    }
}

