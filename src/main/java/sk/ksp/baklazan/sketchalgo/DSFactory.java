package sk.ksp.baklazan.sketchalgo;
import sk.ksp.baklazan.sketchalgo.structure.Wrapped;

/** Factory creating data structures associated with algorithm watcher*/
public abstract class DSFactory
{
	protected AlgorithmWatcher watcher;
	
	public DSFactory()
	{
	}
	
	public DSFactory(AlgorithmWatcher watcher)
	{
		this.watcher = watcher;
	}
	
	void setWatcher(AlgorithmWatcher watcher)
	{
		this.watcher = watcher;
	}
	
	public <T> Wrapped<T> wrap(T t)
	{
		return wrap(t, false);
	}
	
	public <T> Wrapped<T> wrap(T t, boolean register)
	{
		return wrap(t, null, register);
	}
	
	public abstract <T> Wrapped<T> wrap(T t, String name, boolean register);
}