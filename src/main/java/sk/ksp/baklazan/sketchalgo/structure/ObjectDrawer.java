package sk.ksp.baklazan.sketchalgo.structure;
import java.util.*;
import java.lang.*;
import java.awt.image.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.*;
import java.awt.FontMetrics;
import java.awt.Color;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.scene.*;
import javafx.application.*;


public class ObjectDrawer
{
	public static BufferedImage draw(Object o)
	{
		if(o instanceof VisualizableStructure)
		{
			return ((VisualizableStructure)(o)).draw();
		}
		else
		{
			String string = o.toString();
			
			BufferedImage tmp = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = tmp.createGraphics();
			FontMetrics metrics = graphics.getFontMetrics();
			Rectangle2D rect = metrics.getStringBounds(string, graphics);
			BufferedImage result = new BufferedImage((int)rect.getWidth()+4, (int)rect.getHeight()+4, BufferedImage.TYPE_INT_ARGB);
			graphics = result.createGraphics();
			graphics.setColor(Color.BLACK);
			graphics.drawString(string, (int)(-rect.getX())+2, (int)(-rect.getY())+2);
			return result;
		}
	}
}