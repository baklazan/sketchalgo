package sk.ksp.baklazan.sketchalgo.structure;
import sk.ksp.baklazan.sketchalgo.*;
import java.awt.image.*;

public interface VisualizableStructure
{
	BufferedImage draw();
	void setDisplayer(StructureDisplayer sd);
}