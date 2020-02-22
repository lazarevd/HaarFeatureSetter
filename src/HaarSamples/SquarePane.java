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




    int HANDLE_MOVE_RADIUS = 3;

    Square square;
    AngleLine angleLine;
    VBox vbox;

    Canvas canvas = null;
    GraphicsContext gc = null;

    int paneWidth = 200;
    int paneHeight = 200;

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
            return x > this.rx - handleScaleRadius && x < this.rx + handleScaleRadius && y > this.ry - handleScaleRadius && y < this.ry + handleScaleRadius;
        }
    }


    class AngleLine {
        int handleBaseRadius = 3;
        int handleDirRadius = 3;
        int baseX;
        int baseY;
        int dirX;
        int dirY;

        public AngleLine(int baseX, int baseY, int dirX, int dirY) {
            this.baseX = baseX;
            this.baseY = baseY;
            this.dirX = dirX;
            this.dirY = dirY;
        }

        public boolean isBasePoint(int x, int y) {
            return x > this.baseX - handleBaseRadius && x < this.baseX + handleBaseRadius && y > this.baseY - handleBaseRadius && y < this.baseY + handleBaseRadius;
        }

        public boolean isDirectionPoint(int x, int y) {
            return x > this.dirX - handleDirRadius && x < this.dirX + handleDirRadius && y > this.dirY - handleDirRadius && y < this.dirY + handleDirRadius;
        }

    }


    private void dragSquares(MouseEvent e) {
        if (square.isPointScale((int) e.getX(), (int) e.getY())) {
            System.out.println("drag " + e.getX() + ":" + e.getY());
            double ex = e.getX();
            double ey = e.getY();
            if (ex >= 0 && ex <= paneWidth && ey >= 0 && ey <= paneHeight) {
                square.rx = (int) e.getX();
                square.ry = (int) e.getY();
            }
            square.handleScaleRadius = 10;

        }

        if (square.isPointMove((int) e.getX(), (int) e.getY())) {
            System.out.println("drag " + e.getX() + ":" + e.getY());
            double ex = e.getX();
            double ey = e.getY();
            if (ex >= 0 && ex <= paneWidth && ey >= 0 && ey <= paneHeight) {
                square.lx = (int) e.getX();
                square.ly = (int) e.getY();
            }
            square.handleMoveRadius = 10;
        }
    }




    private void dragAngleLine(MouseEvent e) {
        if (angleLine.isBasePoint((int) e.getX(), (int) e.getY())) {
            System.out.println("drag " + e.getX() + ":" + e.getY());
            double ex = e.getX();
            double ey = e.getY();
            if (ex >= 0 && ex <= paneWidth && ey >= 0 && ey <= paneHeight) {
                angleLine.baseX = (int) e.getX();
                angleLine.baseY = (int) e.getY();
            }
            angleLine.handleBaseRadius = 10;

        }

        if (angleLine.isDirectionPoint((int) e.getX(), (int) e.getY())) {
            System.out.println("drag " + e.getX() + ":" + e.getY());
            double ex = e.getX();
            double ey = e.getY();
            if (ex >= 0 && ex <= paneWidth && ey >= 0 && ey <= paneHeight) {
                angleLine.dirX = (int) e.getX();
                angleLine.dirY= (int) e.getY();
            }
            angleLine.handleDirRadius = 10;
        }
    }

    private void releaseAngleLine(MouseEvent e) {
        angleLine.handleBaseRadius = 5;
        angleLine.handleDirRadius = 5;
    }


    private void releaseSquares(MouseEvent e) {
        square.handleMoveRadius = 5;
        square.handleScaleRadius = 5;
    }

    private void drawSquares() {
        gc.setStroke(Color.GREEN);
        gc.strokeLine(square.lx, square.ly, square.lx, square.ry);
        gc.strokeLine(square.lx, square.ry, square.rx, square.ry);
        gc.strokeLine(square.rx, square.ry, square.rx, square.ly);
        gc.strokeLine(square.rx, square.ly, square.lx, square.ly);
        gc.strokeOval(square.lx - square.handleMoveRadius / 2, square.ly - square.handleMoveRadius / 2, square.handleMoveRadius, square.handleMoveRadius);
        gc.strokeOval(square.rx - square.handleScaleRadius / 2, square.ry - square.handleScaleRadius / 2, square.handleScaleRadius, square.handleScaleRadius);
        gc.strokeText("L", square.lx - 2, square.ly - 2);
        gc.strokeText("R", square.rx + 2, square.ry + 2);
    }

    private void drawAngleLine() {
        gc.setStroke(Color.GREEN);
        gc.strokeLine(angleLine.baseX, angleLine.baseY, angleLine.dirX, angleLine.dirY);
        gc.strokeText(">", angleLine.dirX + 2, angleLine.dirY + 2);
        gc.strokeOval(angleLine.baseX - angleLine.handleBaseRadius / 2, angleLine.baseY - angleLine.handleBaseRadius / 2, angleLine.handleBaseRadius, angleLine.handleBaseRadius);
        gc.strokeOval(angleLine.dirX - angleLine.handleDirRadius / 2, angleLine.dirY - angleLine.handleDirRadius / 2, angleLine.handleDirRadius, angleLine.handleDirRadius);
        gc.setStroke(Color.RED);
        gc.strokeText("0", angleLine.baseX - 2, angleLine.baseY - 2);
    }

    public SquarePane(MainWindow mainWindow) {
        vbox = new VBox(5);
        square = new Square(20, 20, 40, 40);
        angleLine = new AngleLine(50, 20, 90, 30);
        BorderPane bordImgViewPlane = new BorderPane();
        canvas = new Canvas(paneWidth, paneHeight);
        bordImgViewPlane.setCenter(canvas);
        gc = canvas.getGraphicsContext2D();
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
        });


        canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                e -> {if (MainWindow.mode.equals(MainWindow.Mode.BBOX)) {
                    dragSquares(e);
                } else if (MainWindow.mode.equals(MainWindow.Mode.ANGLE)) {
                    dragAngleLine(e);
                }
                });

        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED,
                e -> { if (MainWindow.mode.equals(MainWindow.Mode.BBOX)) {
                        releaseSquares(e);
                    } else if (MainWindow.mode.equals(MainWindow.Mode.ANGLE)) {
                    releaseAngleLine(e);
                }
                });




        new AnimationTimer() {
            final long startNanoTime = System.nanoTime();

            public void handle(long currentNanoTime) {
                double t = (currentNanoTime - startNanoTime) / 1000000000.0;
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
                if (mainWindow.currentImage != null ) {
                    canvas.setWidth(mainWindow.currentImage.getWidth());
                    canvas.setHeight(mainWindow.currentImage.getHeight());
                    gc.drawImage(mainWindow.currentImage, 0, 0);
                }
                if (MainWindow.mode.equals(MainWindow.Mode.BBOX)) {
                    drawSquares();
                }
                else if (MainWindow.mode.equals(MainWindow.Mode.ANGLE)) {
                    drawAngleLine();
                }
            }
        }.start();


        vbox.getChildren().addAll(bordImgViewPlane);
    }

    public void addToBox(Pane box) {
        box.getChildren().add(vbox);
    }


}
