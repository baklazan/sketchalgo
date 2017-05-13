package sk.ksp.baklazan.demo.test;
import sk.ksp.baklazan.sketchalgo.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.Enum.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.shape.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.input.*;
import javafx.scene.*;
import javafx.stage.Stage;
import javafx.collections.*;
import javafx.beans.value.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;


public class Main extends Application
{
	@Override
	public void start(Stage primaryStage)
	{
		Pane root = new Pane();
		Canvas canvas = new Canvas(1100,600);
		root.getChildren().add(canvas);
		Scene scene = new Scene(root);
		
		primaryStage.setTitle("Testing playground");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		Thread task = new MainTask(canvas);
		task.setDaemon(true);
		task.start();
	}
	
	
	public static void main(String [] args)
	{
		launch(args);
	}
}

