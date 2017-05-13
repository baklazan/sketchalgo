package sk.ksp.baklazan.sketchalgo.structure;
import sk.ksp.baklazan.sketchalgo.*;
import java.awt.image.*;
import java.awt.Rectangle;

public interface VisualizableStructure
{
	BufferedImage draw();
	BufferedImage draw(Rectangle size);
	Rectangle preferredSize();
	void setDisplayer(StructureDisplayer sd);
	void setInner(boolean inner);
}