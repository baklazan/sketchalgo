package sk.ksp.baklazan.sketchalgo;
import sk.ksp.baklazan.sketchalgo.structure.*;
import sk.ksp.baklazan.sketchalgo.structure.arraylist.*;
import sk.ksp.baklazan.sketchalgo.structure.binarytree.*;
import sk.ksp.baklazan.sketchalgo.structure.map.*;
import java.util.*;
import java.lang.*;

public class DefaultDSFactory extends DSFactory
{
	protected Theme theme;
	
	public Theme getTheme()
	{
		return theme;
	}
	
	public DefaultDSFactory()
	{
		this(new DefaultTheme());
	}
	
	public DefaultDSFactory(AlgorithmWatcher watcher, Theme theme)
	{
		super(watcher);
		this.theme = theme;
	}
	
	public DefaultDSFactory(Theme theme)
	{
		this.theme = theme;
	}
	
	public <E> ArrayList<E> createArrayList(boolean registered)
	{
		return createArrayList(null, registered, null, null, null);
	}
	
	public <E> ArrayList<E> createArrayList(String name, boolean registered)
	{
		return createArrayList(name, registered, null, null, null);
	}
	
	public <E> ArrayList<E> createArrayList(String name)
	{
		return createArrayList(name, true, null, null, null);
	}
	
	public <E> ArrayList<E> createArrayList(String name, LayoutHint hint)
	{
		return createArrayList(name, true, null, hint, null);
	}
	
	public <E> ArrayList<E> createArrayList(String name, boolean registered, SleepConstants sleepConstants)
	{
		return createArrayList(name, registered, sleepConstants, null, null);
	}
	
	public <E> ArrayList<E> createArrayList(String name, 
	                                        boolean registered,
	                                        SleepConstants sleepConstants, 
	                                        LayoutHint hint)
	{
		return createArrayList(name, registered, sleepConstants, hint, null);
	}
	
	public <E> ArrayList<E> createArrayList(String name, 
	                                        boolean registered, 
	                                        SleepConstants sleepConstants,
	                                        LayoutHint hint,
	                                        ListAssemblingStrategy assemblingStrategy)
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
		if(assemblingStrategy != null)
		{
			result.setAssemblingStrategy(assemblingStrategy);
		}
		result.setTheme(theme);
		return result;
	}
	
	public SleepConstants createSleepConstants(int sleepAdd, int sleepGet, int sleepSet, boolean batchGet)
	{
		int getType;
		if(batchGet)
		{
			getType = SleepConstants.GET_BATCH;
		}
		else
		{
			getType = SleepConstants.GET_INSTANT;
		}
		return createSleepConstants(sleepAdd, sleepGet, sleepSet, getType);
	}
	
	public SleepConstants createSleepConstants(int sleepAdd, int sleepGet, int sleepSet, int getType)
	{
		return createSleepConstants(sleepAdd, sleepGet, 0, sleepSet, getType);
	}
	
	public SleepConstants createSleepConstants(int sleepAdd, int sleepGet, int sleepRemove, int sleepSet, int getType)
	{
		return new SleepConstants(sleepAdd, sleepGet, sleepRemove, sleepSet, getType);
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
		return createTreeMap(name, registered, null);
	}
	
	public <K, V> TreeMap<K, V> createTreeMap(String name, boolean register, SleepConstants sleepConstants)
	{
		VisualizableTreeMap<K, V> result = new VisualizableTreeMap<K, V>(name);
		result.setTheme(theme);
		if(register)
		{
			result.setDisplayer(watcher);
			watcher.register(result);
		}
		if(sleepConstants != null)
		{
			result.setSleepConstants(sleepConstants);
		}
		return result;
	}
	
	public LayoutHint createHint(Object reference, LayoutHint.Direction direction)
	{
		return new LayoutHint(reference, direction);
	}
	
	@Override
	public <T> Wrapped<T> wrap(T t, String name, boolean register, SleepConstants sleepConstants)
	{
		VisualWrapped<T> result = new VisualWrapped<T>(t, name);
		result.setTheme(theme);
		if(sleepConstants != null)
		{
			result.setSleepConstants(sleepConstants);
		}
		if(register)
		{
			result.setDisplayer(watcher);
			watcher.register(result);
		}
		return result;
	}
	
	public <T extends BinaryNode> Wrapped<T> wrapBinaryNode(T t)
	{
		return wrapBinaryNode(t, false);
	}
	
	public <T extends BinaryNode> Wrapped<T> wrapBinaryNode(T t, boolean register)
	{
		return wrapBinaryNode(t, null, register);
	}
	
	public <T extends BinaryNode> Wrapped<T> wrapBinaryNode(T t, String name, boolean register)
	{
		return wrapBinaryNode(t, name, register, null);
	}
	
	public <T extends BinaryNode> Wrapped<T> wrapBinaryNode(T t, String name, boolean register, SleepConstants sleepConstants)
	{
		WrappedBinaryNode<T> result = new WrappedBinaryNode<T>(t, name);
		result.setTheme(theme);
		if(sleepConstants != null)
		{
			result.setSleepConstants(sleepConstants);
		}
		if(register)
		{
			result.setDisplayer(watcher);
			watcher.register(result);
		}
		return result;
	}
}