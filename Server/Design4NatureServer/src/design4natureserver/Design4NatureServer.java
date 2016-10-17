/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureserver;

import design4nature.Server;
import design4natureserver.Listener;
import design4natureserver.Map;
import design4natureserver.PQRS;
import design4natureserver.Player;
import design4natureserver.PlayerCollision;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    private Group window2nd;

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

                String[] xy = PQRS.d2xy(lat, lon).split(",");
                if (xy[0].equals("buiten bereik")) {
                    continue;
                }

                int x = Integer.parseInt(xy[0]);
                int y = Integer.parseInt(xy[1]);

//                int x = Integer.parseInt(message[0]);
//                int y = Integer.parseInt(message[1]);
                boolean result = Map.instance().addPathToPlayer(msg.getSender().getId(), x, y);
                if (!result) {
                    Map.instance().addPlayer(msg.getSender().getId(), msg.getSender().getName());
                    Map.instance().addPathToPlayer(msg.getSender().getId(), x, y);
                }

                //drawLineTo(msg.getSender(), x, y);
//                Platform.runLater(() -> {
//                    map.draw();
//                });
//                
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(Design4NatureServer.class.getName()).log(Level.SEVERE, null, ex);
//                }
            }
        });
        serverInput.start();

        new Thread(() -> {
            while (true) {
                Platform.runLater(() -> {
                    Map.instance().draw();
                });

                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Design4NatureServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();

        Group root = new Group();
        window2nd = new Group();
        Canvas canv = new Canvas(1920, 1080);
        canvas = canv.getGraphicsContext2D();
        Map.create(canv, false);
        Map.instance().addListener(this);
        Map.instance().draw();
        root.getChildren().add(canv);

        Canvas adminDraw = new Canvas(970, 540);
        adminDraw.setLayoutX(100);
        adminDraw.setLayoutY(20);
        window2nd.getChildren().add(adminDraw);

        new Thread(() -> {
            while (true) {
                Platform.runLater(() -> {
                    WritableImage image = canv.snapshot(null, null);
                    adminDraw.getGraphicsContext2D().drawImage(image, 0, 0, 970, 540);
                });

                try {
                    Thread.sleep(800);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Design4NatureServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();

        Stage secondStage = new Stage();

        int index = 0;
        for (Screen screen : Screen.getScreens()) {
            Rectangle2D bounds = screen.getVisualBounds();

            if (index == 1) {
                primaryStage.setX(bounds.getMinX());
                primaryStage.setY(bounds.getMinY());
            } else if (index == 0) {
                secondStage.setX(bounds.getMinX() + 800);
                secondStage.setY(bounds.getMinY());
            }
            index++;
        }

        primaryStage.setScene(new Scene(root, Color.LIGHTGRAY));
        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.show();

        secondStage.setScene(new Scene(window2nd));
        //secondStage.setMaximized(true);
        secondStage.show();
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
            btn.setLayoutX(20);
            btn.setBackground(new Background(new BackgroundFill(player.getColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            btn.setLayoutY(5 + player.getId() * 30);
            btn.setOnAction(new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    Server.killClient(player.getId());
                    btn.setDisable(true);
                }
            });

            window2nd.getChildren().add(btn);
        });
    }
}
