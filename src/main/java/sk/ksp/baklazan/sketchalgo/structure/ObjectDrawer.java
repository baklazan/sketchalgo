package sk.ksp.baklazan.sketchalgo.structure;
import sk.ksp.baklazan.sketchalgo.Theme;
import java.util.*;
import java.lang.*;
import java.awt.image.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.*;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Rectangle;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.scene.*;
import javafx.application.*;


public class ObjectDrawer
{
	public static BufferedImage draw(Object o, Theme theme)
	{
		return draw(o, theme, null);
	}
	
	public static BufferedImage draw(Object o, Theme theme, Rectangle size)
	{
		if(o instanceof VisualizableStructure)
		{
			return ((VisualizableStructure)(o)).draw(size);
		}
		else
		{
			String string = o.toString();
			
			BufferedImage tmp = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = tmp.createGraphics();
			FontMetrics metrics = graphics.getFontMetrics();
			Rectangle2D rect = metrics.getStringBounds(string, graphics);
			BufferedImage result;
			if(size == null)
			{
				int width = (int)rect.getWidth()+2*theme.textMargin;
				int height = (int)rect.getHeight()+2*theme.textMargin;
				result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			}
			else
			{
				result = new BufferedImage(Math.max(1,size.width), Math.max(1,size.height), BufferedImage.TYPE_INT_ARGB);
			}
			graphics = result.createGraphics();
			graphics.setColor(theme.textColor);
			int marginX = (int)result.getWidth() - (int)rect.getWidth();
			int marginY = (int)result.getHeight() - (int)rect.getHeight();
			graphics.drawString(string, (int)(-rect.getX())+marginX/2, (int)(-rect.getY())+marginY/2);
			return result;
		}
	}
	
	public static Rectangle preferredSize(Object o, Theme theme)
	{
		if(o instanceof VisualizableStructure)
		{
			return ((VisualizableStructure)(o)).preferredSize();
		}
		else
		{
			String string = o.toString();
			BufferedImage tmp = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = tmp.createGraphics();
			FontMetrics metrics = graphics.getFontMetrics();
			Rectangle2D rect = metrics.getStringBounds(string, graphics);
			return new Rectangle((int)rect.getWidth()+2*theme.textMargin, (int)rect.getHeight()+2*theme.textMargin);
		}
	}
}