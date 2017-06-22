package sk.ksp.baklazan.sketchalgo;
import sk.ksp.baklazan.sketchalgo.structure.*;

/** API for the visualized algorithm */
public interface AlgorithmWatcher extends StructureDisplayer
{
	/** Returns factory that creates data structures associated with
	 *  this watcher.
	 */
	DSFactory getFactory();
	
	void register(VisualizableStructure structure);
	void register(VisualizableStructure structure, LayoutHint hint);
}