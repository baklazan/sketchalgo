package sk.ksp.baklazan.sketchalgo;
import sk.ksp.baklazan.sketchalgo.structure.*;
import sk.ksp.baklazan.sketchalgo.structure.arraylist.*;
import sk.ksp.baklazan.sketchalgo.structure.map.*;
import java.util.*;
import java.lang.*;

public class DefaultDSFactory extends DSFactory
{
	public DefaultDSFactory(AlgorithmWatcher watcher)
	{
		super(watcher);
	}
	
	public <E> ArrayList<E> createArrayList(boolean registered)
	{
		return createArrayList(null, registered, null, null);
	}
	
	public <E> ArrayList<E> createArrayList(String name, boolean registered)
	{
		return createArrayList(name, registered, null, null);
	}
	
	public <E> ArrayList<E> createArrayList(String name)
	{
		return createArrayList(name, true, null, null);
	}
	
	public <E> ArrayList<E> createArrayList(String name, LayoutHint hint)
	{
		return createArrayList(name, true, null, hint);
	}
	
	public <E> ArrayList<E> createArrayList(String name, boolean registered, VisualizableArrayList.SleepConstants sleepConstants)
	{
		return createArrayList(name, registered, sleepConstants, null);
	}
	
	public <E> ArrayList<E> createArrayList(String name, 
	                                        boolean registered, 
	                                        VisualizableArrayList.SleepConstants sleepConstants,
	                                        LayoutHint hint)
	{
		VisualizableArrayList<E> result = new VisualizableArrayList<E>(name);
		if(registered)
		{
			result.setDisplayer(watcher);
			if(hint != null)
			{
				watcher.register(result, hint);
			}
			else
			{
				watcher.register(result);
			}
		}
		if(sleepConstants != null)
		{
			result.setSleepConstants(sleepConstants);
		}
		return result;
	}
	
	public VisualizableArrayList.SleepConstants createSleepConstants(int sleepAdd, int sleepGet, int sleepSet, boolean batchGet)
	{
		return new VisualizableArrayList.SleepConstants(sleepAdd, sleepGet, sleepSet, batchGet);
	}
	
	public <K, V> TreeMap<K, V> createTreeMap(String name)
	{
		return createTreeMap(name, true);
	}
	
	public <K, V> TreeMap<K, V> createTreeMap(boolean registered)
	{
		return createTreeMap(null, registered);
	}
	
	public <K, V> TreeMap<K, V> createTreeMap()
	{
		return createTreeMap(null, true);
	}
	
	public <K, V> TreeMap<K, V> createTreeMap(String name, boolean registered)
	{
		VisualizableTreeMap<K, V> result = new VisualizableTreeMap<K, V>(name);
		if(registered)
		{
			result.setDisplayer(watcher);
			watcher.register(result);
		}
		return result;
	}
	
	public LayoutHint createHint(Object reference, LayoutHint.Direction direction)
	{
		return new LayoutHint(reference, direction);
	}
}