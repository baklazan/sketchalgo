package sk.ksp.baklazan.sketchalgo;

/** Factory creating data structures associated with algorithm watcher*/
public abstract class DSFactory
{
	AlgorithmWatcher watcher;
	
	public DSFactory(AlgorithmWatcher watcher)
	{
		this.watcher = watcher;
	}
}