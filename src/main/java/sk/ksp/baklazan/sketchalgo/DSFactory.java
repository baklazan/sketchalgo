package sk.ksp.baklazan.sketchalgo;

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
}