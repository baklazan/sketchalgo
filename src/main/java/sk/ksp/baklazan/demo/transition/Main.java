package sk.ksp.baklazan.demo.transition;
import sk.ksp.baklazan.sketchalgo.*;
import sk.ksp.baklazan.sketchalgo.structure.SleepConstants;
import sk.ksp.baklazan.sketchalgo.structure.arraylist.*;
import sk.ksp.baklazan.sketchalgo.display.TransitionDisplayStrategy;
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
import javafx.animation.*;
import javafx.event.*;


public class Main extends Application
{
	private Visualizer visualizer;
	private DefaultDSFactory factory;
	Transition animation;
	
	@Override
	public void start(Stage primaryStage)
	{
		VBox root = new VBox();
		Canvas canvas = new Canvas(1100,600);
		root.getChildren().add(canvas);
		Button playButton = new Button("play");
		playButton.setOnAction(replayAnimation);
		root.getChildren().add(playButton);
		Scene scene = new Scene(root);
		
		primaryStage.setTitle("Testing transition");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		TransitionDisplayStrategy display = new TransitionDisplayStrategy(canvas);
		visualizer = new Visualizer(display);
		factory = visualizer.getFactory();
		have_fun();
		animation = display.getAnimation();
	}
	
	private void have_fun()
	{
		int n;
		Scanner in = new Scanner(System.in);
		n = in.nextInt();
		visualizer.setAlgorithmState("Loading matrices");
		
		ArrayList<ArrayList<Integer> > matrix1, matrix2, matrix3;
		matrix1 = factory.createArrayList("matrix1", 
		                                  true, 
		                                  factory.createSleepConstants(0, 0, 0, SleepConstants.GET_SILENT),
		                                  null, 
		                                  VerticalAssemblingStrategy.getInstance());
		matrix2 = factory.createArrayList("matrix2", 
		                                  true, 
		                                  factory.createSleepConstants(0, 0, 0, SleepConstants.GET_SILENT), 
		                                  factory.createHint(matrix1, LayoutHint.Direction.RIGHT),
		                                  VerticalAssemblingStrategy.getInstance());
		
		for(int i=0; i<n; i++)
		{
			ArrayList<Integer> row = factory.createArrayList(null, false, factory.createSleepConstants(0,0,0,true));
			for(int j=0; j<n; j++)
			{
				int e = in.nextInt();
				row.add(e);
			}
			matrix1.add(row);
		}
		for(int i=0; i<n; i++)
		{
			ArrayList<Integer> row = factory.createArrayList(null, false, factory.createSleepConstants(0, 0,0, true));
			for(int j=0; j<n; j++)
			{
				int e = in.nextInt();
				row.add(e);
			}
			matrix2.add(row);
		}
		matrix3 = factory.createArrayList("result", 
		                                  true, 
		                                  factory.createSleepConstants(0, 0, 0, SleepConstants.GET_SILENT),
		                                  factory.createHint(matrix2, LayoutHint.Direction.RIGHT),
		                                  VerticalAssemblingStrategy.getInstance());
		for(int i=0; i<n; i++)
		{
			ArrayList<Integer> row = factory.createArrayList(null, false, factory.createSleepConstants(0, 0, 600, true));
			for(int j=0; j<n; j++)
			{
				row.add(0);
			}
			matrix3.add(row);
		}
		visualizer.setAlgorithmState("Multiplying matrices");
		for(int y=0; y<n; y++)
		{
			for(int x=0; x<n; x++)
			{
				int res = 0;
				for(int i=0; i<n; i++)
				{
					res += matrix1.get(y).get(i) * matrix2.get(i).get(x);
				}
				matrix3.get(y).set(x, res);
			}
		}
		
		TreeMap<String, ArrayList<Integer> > mapa = factory.createTreeMap("mapa");
		for(int i=0; i<10; i++)
		{
			ArrayList<Integer> row = factory.createArrayList(null, false);
			for(int j=0; j<i; j++)
			{
				row.add(j);
			}
			mapa.put(new Integer(i).toString(), row);
		}
	}
	
	EventHandler<ActionEvent> replayAnimation = new EventHandler<ActionEvent>()
	{
		@Override
		public void handle(ActionEvent event)
		{
			System.out.println("replay");
			animation.stop();
			animation.playFromStart();
		}
	};
	
	public static void main(String [] args)
	{
		launch(args);
	}
}

