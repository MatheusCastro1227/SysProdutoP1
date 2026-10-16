package com.example.sysprodutop1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApplication.class.getResource("Main-view.fxml"));
        Scene scene = new Scene(loader.load(), 1150, 650);
        stage.setTitle("Sistema de Produtos");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}