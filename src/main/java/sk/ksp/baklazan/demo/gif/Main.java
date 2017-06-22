package sk.ksp.baklazan.demo.gif;
import sk.ksp.baklazan.sketchalgo.*;
import sk.ksp.baklazan.sketchalgo.structure.SleepConstants;
import sk.ksp.baklazan.sketchalgo.structure.arraylist.*;
import sk.ksp.baklazan.sketchalgo.display.GifDisplayStrategy;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.Enum.*;
import java.awt.Rectangle;


public class Main
{
	private static void have_fun(Visualizer visualizer, DefaultDSFactory factory)
	{
		int n;
		Scanner in = new Scanner(System.in);
		n = in.nextInt();
		visualizer.setAlgorithmState("Loading matrices");
		
		ArrayList<ArrayList<Integer> > matrix1, matrix2, matrix3;
		matrix1 = factory.createArrayList("matrix1", 
		                                  true, 
		                                  factory.createSleepConstants(300, 0, 0, SleepConstants.GET_SILENT),
		                                  null, 
		                                  VerticalAssemblingStrategy.getInstance());
		matrix2 = factory.createArrayList("matrix2", 
		                                  true, 
		                                  factory.createSleepConstants(300, 0, 0, SleepConstants.GET_SILENT), 
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
			ArrayList<Integer> row = factory.createArrayList(null, false, factory.createSleepConstants(0, 0, 3000, true));
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
	
	
	public static void main(String [] args)
	{
		Visualizer visualizer;
		DefaultDSFactory factory;
		if(args.length < 1)
		{
			System.err.println("Usage: java sk.ksp.baklazan.demo.gif.Main [output file]");
		}
		else
		{
			String filename = args[0];
			try
			{
				File file = new File(filename);
				GifDisplayStrategy display = new GifDisplayStrategy(new Rectangle(500, 500), file);
				factory = new DefaultDSFactory();
				visualizer = new Visualizer(display, factory);
				have_fun(visualizer, factory);
				display.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}

