package sk.ksp.baklazan.sketchalgo.structure.arraylist;
import sk.ksp.baklazan.sketchalgo.structure.*;
import sk.ksp.baklazan.sketchalgo.Theme;
import java.util.*;
import java.lang.*;
import java.awt.image.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.*;
import java.awt.Rectangle;
import java.awt.FontMetrics;
import java.awt.Color;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.scene.*;
import javafx.application.*;

/** A class that puts images coresponding to elements of an ArrayList together */
public abstract class ListAssemblingStrategy
{
	public abstract BufferedImage assemble(ArrayList<BufferedImage> elements, Rectangle cellSize, Theme theme, boolean inner);
	
	public abstract ListAssemblingStrategy getPerpendicular();
	
	public abstract Rectangle preferredSize(Rectangle cellSize, int count, Theme theme, boolean inner);
	
	public abstract Rectangle cellSize(Rectangle totalSize, int count, Theme theme, boolean inner);
}
