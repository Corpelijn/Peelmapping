/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import design4nature.Server;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import shared.Message;

/**
 *
 * @author Bas
 */
public class Design4NatureServer extends Application {

    private GraphicsContext canvas;
    private int xp1 = -1;
    private int xp2 = -1;
    private int yp1 = -1;
    private int yp2 = -1;
    private Map map;

    @Override
    public void start(Stage primaryStage) {
        Thread t = new Thread(() -> {
            new Server().start();
        });
        t.start();

        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Message message = Server.getNextReceivedMessage();
                if (message == null) {
                    return;
                }
                System.out.println(message);
            }
        });

        Thread serverInput = new Thread(() -> {
            while (true) {
                Message msg = Server.getNextReceivedMessage();
                if (msg == null) {
                    continue;
                }

                // Get the Lat en Lon positions as local positions
                String[] message = msg.getData().split(",");
                float lat = Float.parseFloat(message[0]);
                float lon = Float.parseFloat(message[1]);

                String[] xy = d2xy(lat, lon).split(",");
                if (xy[0].equals("buiten bereik")) {
                    continue;
                }

                int x = Integer.parseInt(xy[0]);
                int y = Integer.parseInt(xy[1]);

//                x -= 161000;
//                y -= 384000;
                boolean result = map.addPathToPlayer(msg.getSender(), x, y);
                if (!result) {
                    map.addPlayer(msg.getSender());
                    map.addPathToPlayer(msg.getSender(), x, y);
                }

                //drawLineTo(msg.getSender(), x, y);
                Platform.runLater(() -> {
                    map.draw(canvas);
                });
            }
        });
        serverInput.start();

        Group root = new Group();
        Canvas canv = new Canvas(1850, 1000);
        canvas = canv.getGraphicsContext2D();
        map = new Map((int) canv.getWidth(), (int) canv.getHeight());
        //drawShapes();
        root.getChildren().add(canv);
        //root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private String d2xy(float lat, float lon) {
        float tmplat = lat;
        float tmplng = lon;
        double X = Math.round(gps2X(tmplat, tmplng));
        double Y = Math.round(gps2Y(tmplat, tmplng));
        return 50.57 < tmplat && 53.63 > tmplat && 3.29 < tmplng && 7.58 > tmplng ? ((int) X + "," + (int) Y) : "buiten bereik";
    }

    private double gps2X(double b, double c) {
        double a = 0;

        double lat0 = 52.1551744;
        double lng0 = 5.38720621;
        double X0 = 155000;
        double Y0 = 463000;

        double dlat = 0.36 * (b - lat0);
        double dlng = 0.36 * (c - lng0);
        for (int i = 1; 10 > i; i++) {
            a += PQRS.XpqR()[i].R * Math.pow(dlat, PQRS.XpqR()[i].p) * Math.pow(dlng, PQRS.XpqR()[i].q);
        }
        return X0 + a;
    }

    private double gps2Y(double b, double c) {
        double a = 0;

        double lat0 = 52.1551744;
        double lng0 = 5.38720621;
        double X0 = 155000;
        double Y0 = 463000;

        double dlat = 0.36 * (b - lat0);
        double dlng = 0.36 * (c - lng0);
        for (int i = 1; 11 > i; i++) {
            a += PQRS.YpqS()[i].S * Math.pow(dlat, PQRS.YpqS()[i].p) * Math.pow(dlng, PQRS.YpqS()[i].q);
        }
        return Y0 + a;
    }

    private void drawShapes() {
        canvas.setFill(Color.GREEN);
        canvas.setStroke(Color.BLUE);
        canvas.setLineWidth(1);
        canvas.strokeLine(40, 10, 10, 40);
        canvas.fillOval(10, 60, 30, 30);
        canvas.strokeOval(60, 60, 30, 30);
        canvas.fillRoundRect(110, 60, 30, 30, 10, 10);
        canvas.strokeRoundRect(160, 60, 30, 30, 10, 10);
        canvas.fillArc(10, 110, 30, 30, 45, 240, ArcType.OPEN);
        canvas.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
        canvas.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND);
        canvas.strokeArc(10, 160, 30, 30, 45, 240, ArcType.OPEN);
        canvas.strokeArc(60, 160, 30, 30, 45, 240, ArcType.CHORD);
        canvas.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
        canvas.fillPolygon(new double[]{10, 40, 10, 40},
                new double[]{210, 210, 240, 240}, 4);
        canvas.strokePolygon(new double[]{60, 90, 60, 90},
                new double[]{210, 210, 240, 240}, 4);
        canvas.strokePolyline(new double[]{110, 140, 110, 140},
                new double[]{210, 210, 240, 240}, 4);
    }

    private void drawLineTo(int sender, int x, int y) {
        Platform.runLater(() -> {
            if ((xp1 != -1 && sender == 1) || (xp2 != -1 && sender == 2)) {
                if (sender == 1) {
                    canvas.setStroke(Color.RED);
                } else {
                    canvas.setStroke(Color.GREEN);
                }
                canvas.setLineWidth(1);

                canvas.strokeLine(
                        sender == 1 ? xp1 : xp2,
                        sender == 1 ? yp1 : yp2,
                        x, y);
            }

            if (sender == 1) {
                xp1 = x;
                yp1 = y;
            } else {
                xp2 = x;
                yp2 = y;
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
