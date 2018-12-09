package HaarSamples;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

class SquarePane {

    Square square;
    VBox vbox;

    int paneWidth = 50;
    int paneHeight = 50;

    class Square {
        int handleMoveRadius = 3;
        int handleScaleRadius = 3;
        int lx, ly, rx, ry;
        private Square(int ilx, int ily, int irx, int iry) {
            lx = ilx;
            ly = ily;
            rx = irx;
            ry = iry;
        }




        public boolean isPointMove(int x, int y) {
            return x > this.lx - handleMoveRadius && x < this.lx + handleMoveRadius && y > this.ly - handleMoveRadius && y < this.ly + handleMoveRadius;
        }

        public boolean isPointScale(int x, int y) {
            return x > this.rx -handleScaleRadius && x < this.rx +handleScaleRadius && y > this.ry -handleScaleRadius && y < this.ry +handleScaleRadius;
        }
    }



    public SquarePane(MainWindow mainWindow) {
        vbox = new VBox(5);
        square = new Square(20, 20, 40, 40);
        BorderPane bordImgViewPlane = new BorderPane();
        Canvas canvas = new Canvas( paneWidth, paneHeight );
        bordImgViewPlane.setCenter(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();




        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
        });


        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                e -> {
                    if (square.isPointScale((int)e.getX(), (int)e.getY())) {
                        System.out.println("drag " + e.getX() + ":" + e.getY());
                        double ex = e.getX();
                        double ey = e.getY();
                        if (ex >= 0 && ex <= paneWidth && ey >= 0 && ey <= paneHeight) {
                            square.rx = (int) e.getX();
                            square.ry = (int) e.getY();
                        }
                            square.handleScaleRadius = 10;

                    }

                    if (square.isPointMove((int)e.getX(), (int)e.getY())) {
                        System.out.println("drag " + e.getX() + ":" + e.getY());
                        double ex = e.getX();
                        double ey = e.getY();
                        if (ex >= 0 && ex <= paneWidth && ey >= 0 && ey <= paneHeight) {
                            square.lx = (int) e.getX();
                            square.ly = (int) e.getY();
                        }
                        square.handleMoveRadius = 10;
                    }

                });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                e -> {
                    square.handleMoveRadius = 5;
                    square.handleScaleRadius = 5;
                });


        new AnimationTimer()
        {
            final long startNanoTime = System.nanoTime();
            public void handle(long currentNanoTime)
            {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                gc.drawImage(mainWindow.currentImage, 0, 0);
                gc.setStroke(Color.GREEN);
                gc.strokeLine(square.lx, square.ly, square.lx, square.ry);
                gc.strokeLine(square.lx, square.ry, square.rx, square.ry);
                gc.strokeLine(square.rx, square.ry, square.rx, square.ly);
                gc.strokeLine(square.rx, square.ly, square.lx, square.ly);
                gc.strokeOval(square.lx -square.handleMoveRadius/2, square.ly - square.handleMoveRadius/2, square.handleMoveRadius, square.handleMoveRadius);
                gc.strokeOval(square.rx -square.handleScaleRadius/2, square.ry - square.handleScaleRadius/2, square.handleScaleRadius, square.handleScaleRadius);
                gc.strokeText("L", square.lx -2,square.ly -2);
                gc.strokeText("R", square.rx +2,square.ry +2);
            }
        }.start();




        vbox.getChildren().addAll(bordImgViewPlane);
    }

    public void addToBox(Pane box) {
        box.getChildren().add(vbox);
    }


}
