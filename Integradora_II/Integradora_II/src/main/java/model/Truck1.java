package model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;

public class Truck1 extends Thread{
    private Canvas canvas;
    private GraphicsContext gc;

    private int frame;
    private int x;
    private int y;
    private int w;
    private int h;

    private boolean up;
    private boolean down;
    private boolean left;
    private boolean right;

    private int speed;

    private int state;

    private ArrayList<Image> in_motion;

    public Truck1(Canvas canvas, int x, int y, int w, int h) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();

        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        this.state = 0;

        in_motion = new ArrayList<>();

       //for (int i = 0; i < 5; i++) {
            Image image = new  Image(getClass().getResourceAsStream("/truck1/run/truck1.png"));
            in_motion.add(image);
        //}
    }

    public void onMove(){
        if(up){
            y -= 10;
        }
        if (down){
            y += 10;
        }
        if (left){
            x -= 10;
        }
        if (right){
            x += 10;
        }
    }

    public void paint() {
        onMove();

        if(state == 1 || state == 0){
            gc.drawImage(in_motion.get(0),x, y, h, w);
            frame++;
        }
    }

    public void setOnKeyPressed(KeyEvent e){
        System.out.println(e.getCode());

        switch (e.getCode()) {
            case UP -> {
                state = 1;
                up = true;
            }
            case DOWN -> {
                state = 1;
                down = true;
            }
            case LEFT -> {
                state = 1;
                left = true;
            }
            case RIGHT -> {
                state = 1;
                right = true;
            }
        }
    }

    public void setOnKeyReleased(KeyEvent e){
        switch (e.getCode()) {
            case UP -> {
                up = false;
                state = 0;
            }
            case DOWN -> {
                down = false;
                state = 0;
            }
            case LEFT -> {
                left = false;
                state = 0;
            }
            case RIGHT -> {
                right = false;
                state = 0;
            }
        }
    }
}
