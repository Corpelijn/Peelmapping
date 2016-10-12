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
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import shared.Message;

/**
 *
 * @author Bas
 */
public class Design4NatureServer extends Application implements Listener {

    private GraphicsContext canvas;
    private int xp1 = -1;
    private int xp2 = -1;
    private int yp1 = -1;
    private int yp2 = -1;
    private Map map;
    private Group root;

    @Override
    public void start(Stage primaryStage) {
        Thread t = new Thread(() -> {
            new Server().start();
        });
        t.start();

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

//                int x = Integer.parseInt(message[0]);
//                int y = Integer.parseInt(message[1]);
                boolean result = map.addPathToPlayer(msg.getSender().getId(), x, y);
                if (!result) {
                    map.addPlayer(msg.getSender().getId(), msg.getSender().getName());
                    map.addPathToPlayer(msg.getSender().getId(), x, y);
                }

                //drawLineTo(msg.getSender(), x, y);
                Platform.runLater(() -> {
                    map.draw();
                });
            }
        });
        serverInput.start();

        root = new Group();
        Canvas canv = new Canvas(1850, 1000);
        canv.setLayoutX(70);
        canvas = canv.getGraphicsContext2D();
        map = new Map((int) canv.getWidth(), (int) canv.getHeight(), canvas, false);
        map.addListener(this);
        map.draw();
        //drawShapes();
        root.getChildren().add(canv);

        primaryStage.setScene(new Scene(root, Color.BLACK));
        primaryStage.setFullScreen(true);
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

    @Override
    public void onCollision(PlayerCollision collisionInfo) {
        System.out.println(collisionInfo);
        Server.killClient(collisionInfo.player.getId());
    }

    @Override
    public void onAddPlayer(Player player) {
        Platform.runLater(() -> {
            Button btn = new Button();
            btn.setText("Kill " + player.getName());
            btn.setLayoutY(5 + player.getId() * 30);
            btn.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    Server.killClient(player.getId());
                    btn.setDisable(true);
                }
            });

            root.getChildren().add(btn);
        });
    }
}
