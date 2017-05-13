package sk.ksp.baklazan.sketchalgo;
import sk.ksp.baklazan.sketchalgo.structure.*;
import java.util.*;
import java.lang.*;
import java.lang.ref.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.color.*;
import java.awt.Color;
import javafx.scene.canvas.*;
import javafx.application.*;
import javafx.embed.swing.*;

public abstract class Theme
{
	public Color backgroundColor, borderColor, textColor;
	public int minCellWidth, minCellHeight;
	public int externalBorderInner, externalBorderOuter;
	public int internalBorder, internalBorderThin;
	public int textMargin;
	public abstract BufferedImage drawGet(BufferedImage image);
	public abstract BufferedImage drawAdd(BufferedImage image);
	public abstract BufferedImage drawSet(BufferedImage image);
	public abstract BufferedImage drawRemove(BufferedImage image);
	public int getExternalBorder(boolean inner)
	{
		if(inner)return externalBorderInner;
		else return externalBorderOuter;
	}
}
