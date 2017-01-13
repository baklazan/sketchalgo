package sk.ksp.baklazan.sketchalgo.structure;

/** An interface for classes that display VisualizableStructures */
public interface StructureDisplayer
{
	void requestRedraw(VisualizableStructure caller);
	void requestResizeRedraw(VisualizableStructure caller);
}