package sk.ksp.baklazan.sketchalgo.structure;
import sk.ksp.baklazan.sketchalgo.*;
import java.awt.image.*;

public interface VisualizableStructure
{
	BufferedImage draw();
	int getBoxWidth();
	int getBoxHeight();
	void setDisplayer(StructureDisplayer sd);
}