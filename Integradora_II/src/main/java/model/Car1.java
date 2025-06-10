package model;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.Random;

import java.util.ArrayList;

public class Car1 extends Thread {
    private Random random;

    private Canvas canvas;
    private GraphicsContext gc;

    private int frame;
    private int x;
    private int y;
    private int w;
    private int h;
    private int speed;

    //0: North
    //1:South
    //2:East
    //3: West
    //4: NE
    //5: NW
    //6: SE
    //7: SW
    private int state;

    private ArrayList<Image> north;
    private ArrayList<Image> west;
    private ArrayList<Image> east;
    private ArrayList<Image> south;
    private Image NE;
    private Image NW;
    private Image SE;
    private Image SW;

    public Car1(Canvas canvas, int x, int y, int w, int h) {
        this.canvas = canvas;
        this.random = new Random();
        this.gc = canvas.getGraphicsContext2D();
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.state = 0;
        this.speed = 1;

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

        east = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            String filename = String.format("/Car1/East/POLICE_CLEAN_EAST_%03d.png", i);
            east.add(new Image(Car1.class.getResourceAsStream(filename)));
        }

        south = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            String filename = String.format("/Car1/South/POLICE_CLEAN_SOUTH_%03d.png", i);
            south.add(new Image(Car1.class.getResourceAsStream(filename)));
        }

        NE = new Image(Car1.class.getResourceAsStream("/Car1/POLICE_CLEAN_NORTHWEST_000.png"));
        NW = new Image(Car1.class.getResourceAsStream("/Car1/POLICE_CLEAN_NORTHEAST_000.png"));
        SE = new Image(Car1.class.getResourceAsStream("/Car1/POLICE_CLEAN_SOUTHEAST_000.png"));
    }

    @Override
    public void run() {
        // Animación o movimiento automático
        initial();
    }

    public int leaveInitialPosition() {
        if(x == 176 && y == 580) {
            while(x > 58) {
                try {
                    x--;
                    Thread.sleep(speed);
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
            while (y > 160) {
                try {
                    y--;
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return 0;
        }
        return -1;
    }

    public void initial() {
        if(x == 176 && y == 580) {
            state = 3;
            if(leaveInitialPosition() == 0) {
                autoMove(0);
            }
        }
    }

    public void autoMove(int checkpoint) {
        if(checkpoint == 0) {
            state = 4;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            state = 2;
            y = 160;
            while (x < 405) {
                try {
                    x++;
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            state = 6;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            state = 1;
            x = 412;
            autoMove(1);
        } if(checkpoint == 1) {
            while(y < 670) {
                y++;
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            int a = random.nextInt(1,3);
            if(a == 1) {
                while(y < 986) {
                    y++;
                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if(a == 2) {
                state = 6;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                state = 2;
                while(x < 1006) {
                    x++;
                    try {
                        Thread.sleep(speed);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public void paint() {
        if (!north.isEmpty() && state == 0) {
            gc.drawImage(north.get(frame % north.size()), x, y, w, h);
            frame++;
        } else if (!south.isEmpty() && state == 1) {
            gc.drawImage(south.get(frame % west.size()), x, y, w, h);
            frame++;
        } else if(!east.isEmpty() && state == 2) {
            gc.drawImage(east.get(frame % east.size()), x, y, w, h);
            frame++;
        } else if(!west.isEmpty() && state == 3) {
            gc.drawImage(west.get(frame % west.size()), x, y, w, h);
            frame++;
        } else if(NW != null && state == 4) {
            gc.drawImage(NW, x, y, w, h);
        }
        else if(NE != null && state == 5) {
            gc.drawImage(NE, x, y, w, h);
        } else if(SE != null && state == 6) {
            gc.drawImage(SE, x, y, w, h);
        }
    }



}
