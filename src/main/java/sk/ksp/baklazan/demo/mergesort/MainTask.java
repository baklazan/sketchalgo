package sk.ksp.baklazan.demo.mergesort;
import sk.ksp.baklazan.sketchalgo.*;
import sk.ksp.baklazan.sketchalgo.display.CanvasDisplayStrategy;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.Enum.*;
import javafx.scene.shape.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.input.*;
import javafx.scene.canvas.*;
import javafx.scene.*;
import javafx.collections.*;
import javafx.beans.value.*;


public class MainTask extends Thread
{
	private Visualizer visualizer;
	private DefaultDSFactory factory;
	
	public MainTask(Canvas canvas)
	{
		super("main task thread");
		this.factory = new DefaultDSFactory();
		this.visualizer = new Visualizer(new CanvasDisplayStrategy(canvas), this.factory);
	}
	
	private void mergeSort(ArrayList<Integer> list)
	{
		visualizer.setAlgorithmState("Sorting " + list);
		if(list.size() == 1)return;
		ArrayList<Integer> left = factory.createArrayList("left", 
		                                                  true,
		                                                  factory.createSleepConstants(300,0,1200,true),
		                                                  factory.createHint(list, LayoutHint.Direction.DOWN));
		for(int i=0; i<list.size()/2; i++)
		{
			left.add(list.get(i));
		}
		mergeSort(left);
		visualizer.setAlgorithmState("Sorting " + list);
		ArrayList<Integer> right = factory.createArrayList("right",
		                                                   true, 
		                                                   factory.createSleepConstants(300,0,1200,true),
		                                                   factory.createHint(left, LayoutHint.Direction.RIGHT));
		for(int i=list.size()/2; i<list.size(); i++)
		{
			right.add(list.get(i));
		}
		mergeSort(right);
		visualizer.setAlgorithmState("Merging " + left + " and " + right);
		int il = 0, ir = 0;
		for(int i=0; i<list.size(); i++)
		{
			boolean fromLeft;
			if(il == left.size()) fromLeft = false;
			else if(ir == right.size()) fromLeft = true;
			else fromLeft = left.get(il) < right.get(ir);
			
			if(fromLeft)
			{
				list.set(i, left.get(il));
				il++;
			}
			else
			{
				list.set(i, right.get(ir));
				ir++;
			}
		}
		visualizer.unregister(left);
		visualizer.unregister(right);
	}
	
	private void bubbleSort(ArrayList<Integer> list)
	{
		int n = list.size();
		boolean sorted = false;
		while(!sorted)
		{
			sorted = true;
			for(int i=0; i<n-1; i++)
			{
				if(list.get(i) > list.get(i+1))
				{
					sorted = false;
					Integer tmp = list.get(i);
					list.set(i, list.get(i+1));
					list.set(i+1, tmp);
				}
			}
		}
	}
	
	private void qSort(ArrayList<Integer> list, int from, int to)
	{
		if(to - from < 2)return;
		Integer pivot = list.get((from + to)/2);
		int a = from, b = to-1;
		while(a < b)
		{
			while(a < to && list.get(a) < pivot) a++;
			while(b >= from && list.get(b) >= pivot) b--;
			if(a < b)
			{
				Integer tmp = list.get(a);
				list.set(a, list.get(b));
				list.set(b, tmp);
			}
		}
		int mensikon = a;
		a = mensikon;
		b = to-1;
		while(a < b)
		{
			while(a < to && list.get(a) <= pivot) a++;
			while(b >= mensikon && list.get(b) > pivot) b--;
			if(a < b)
			{
				Integer tmp = list.get(a);
				list.set(a, list.get(b));
				list.set(b, tmp);
			}
		}
		int vacsizac = a;
		qSort(list, from, mensikon);
		qSort(list, vacsizac, to);
	}
	
	private void qSort(ArrayList<Integer> list)
	{
		qSort(list, 0, list.size());
	}
	
	@Override
	public void run()
	{
		int n;
		Scanner in = new Scanner(System.in);
		n = in.nextInt();
		visualizer.setAlgorithmState("Loading array");
		ArrayList<Integer> list = factory.createArrayList("array", true, factory.createSleepConstants(0,0,1200,true));
		for(int i=0; i<n; i++)
		{
			list.add(new Integer(in.nextInt()));;
		}
		
		mergeSort(list);
		visualizer.setAlgorithmState("Array sorted!");
	}
}