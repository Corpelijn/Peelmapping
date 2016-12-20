/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package design4natureclienttablet;

import design4natureserver.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Bas
 */
public class Design4NatureClientTablet extends Application {
    
    private static Client client;
    
    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button();
        btn.setText("Ready");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                client.sendMessage("c:ready");
            }
        });
        
        Button btnGrid = new Button();
        btnGrid.setText("Draw grid");
        btnGrid.setLayoutY(30);
        btnGrid.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                if (btnGrid.getText().equals("Draw grid")) {
                    btnGrid.setText("Hide grid");
                    Map.instance().setGrid(true);
                } else {
                    btnGrid.setText("Draw grid");
                    Map.instance().setGrid(false);
                }
            }
        });
        
        Button btnSlugs = new Button();
        btnSlugs.setText("Draw slugs");
        btnSlugs.setLayoutY(60);
        btnSlugs.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                if (btnSlugs.getText().equals("Draw slugs")) {
                    btnSlugs.setText("Hide slugs");
                    Map.instance().setSlugs(true);
                } else {
                    btnSlugs.setText("Draw slugs");
                    Map.instance().setSlugs(false);
                }
            }
        });
        
        Button btnKillAll = new Button();
        btnKillAll.setText("Kill all");
        btnKillAll.setLayoutY(90);
        btnKillAll.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                
                client.sendMessage("k:killall");
            }
        });
        
        new Thread(() -> {
            while (true) {
                Platform.runLater(() -> {
                    Map.instance().draw();
                });
                
                try {
                    Thread.sleep(350);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Design4NatureClientTablet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        
        Group root = new Group();
        
        Canvas canv = new Canvas(1200, 1000);
        canv.setLayoutX(60);
        
        root.getChildren().add(canv);
        root.getChildren().add(btn);
        root.getChildren().add(btnGrid);
        root.getChildren().add(btnSlugs);
        root.getChildren().add(btnKillAll);
        
        Scene scene = new Scene(root, Color.BLACK);
        
        Map.create(canv, false);
        
        primaryStage.setTitle("Tablet client");
        primaryStage.setX(0);
        primaryStage.setY(0);
        primaryStage.setScene(scene);
        primaryStage.show();
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("This is the TABLET!");
        client = new Client();
        if (args.length > 0) {
            client.start(args[0]);
        } else {
            client.start();
        }
        launch(args);
    }
    
}
