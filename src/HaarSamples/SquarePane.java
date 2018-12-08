package HaarSamples;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SquarePane {

    MainWindow mainWindow;
    Square square;
    VBox vbox;

    class Square {
        int handleMoveRadius = 3;
        int handleScaleRadius = 3;
        int x1, y1, x2, y2;
        private Square(int ix1, int iy1, int ix2, int iy2) {
            x1 = ix1;
            y1 = iy1;
            x2 = ix2;
            y2 = iy2;




        }




        public int getWidth() {
            return Math.abs(x2 - x1);
        }

        public int getHeight() {
            return Math.abs(y2 - y1);
        }


        public boolean isPointMove(int x, int y) {
            return  (x > this.x1-handleMoveRadius && x < this.x1+handleMoveRadius && y > this.y1-handleMoveRadius && y < this.y1+handleMoveRadius)?true:false;
        }

        public boolean isPointScale(int x, int y) {
            return  (x > this.x2-handleScaleRadius && x < this.x2+handleScaleRadius && y > this.y2-handleScaleRadius && y < this.y2+handleScaleRadius)?true:false;
        }
    }



    public SquarePane(MainWindow mainWindow) {
        vbox = new VBox(5);
        this.mainWindow = mainWindow;
        square = new Square(20, 20, 40, 40);
        BorderPane bordImgViewPlane = new BorderPane();
        Canvas canvas = new Canvas( 128, 128 );
        bordImgViewPlane.setCenter(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();




        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
        });


        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                e -> {
                    if (square.isPointScale((int)e.getX(), (int)e.getY())) {
                        System.out.println("drag " + e.getX() + ":" + e.getY());
                        square.x2 = (int)e.getX();
                        square.y2 = (int)e.getY();
                        square.handleScaleRadius = 10;
                    }

                    if (square.isPointMove((int)e.getX(), (int)e.getY())) {
                        System.out.println("drag " + e.getX() + ":" + e.getY());
                        square.x1 = (int)e.getX();
                        square.y1 = (int)e.getY();
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
                gc.setStroke(Color.WHITE);
                gc.strokeLine(square.x1, square.y1, square.x1, square.y2);
                gc.strokeLine(square.x1, square.y2, square.x2, square.y2);
                gc.strokeLine(square.x2, square.y2, square.x2, square.y1);
                gc.strokeLine(square.x2, square.y1, square.x1, square.y1);
                gc.strokeOval(square.x1-square.handleMoveRadius/2, square.y1 - square.handleMoveRadius/2, square.handleMoveRadius, square.handleMoveRadius);
                gc.strokeOval(square.x2-square.handleScaleRadius/2, square.y2 - square.handleScaleRadius/2, square.handleScaleRadius, square.handleScaleRadius);
                gc.strokeText("L", square.x1-2,square.y1-2);
                gc.strokeText("R", square.x2+2,square.y2+2);
            }
        }.start();




        vbox.getChildren().addAll(bordImgViewPlane);
    }

    public void addToBox(Pane box) {
        box.getChildren().add(vbox);
    }


}
