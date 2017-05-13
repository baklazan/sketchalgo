package sk.ksp.baklazan.sketchalgo.structure.arraylist;
import sk.ksp.baklazan.sketchalgo.structure.*;
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

/** Cells vertically */
public class VerticalAssemblingStrategy extends ListAssemblingStrategy
{
	private VerticalAssemblingStrategy()
	{
	}
	
	private static class singletonHolder
	{
		private static final VerticalAssemblingStrategy INSTANCE = new VerticalAssemblingStrategy();
	}
	
	public static VerticalAssemblingStrategy getInstance()
	{
		return singletonHolder.INSTANCE;
	}
	
	@Override
	public BufferedImage assemble(ArrayList<BufferedImage> elements, int cellWidth, int cellHeight, Theme theme, boolean inner)
	{
		int border = theme.getExternalBorder(inner);
		int width = cellWidth + 2*border;
		int height = cellHeight * elements.size();
		height += theme.internalBorder * Math.max(0,elements.size()-1);
		height += 2*border;
		height = Math.max(height, 1);
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = result.createGraphics();
		graphics.setColor(theme.borderColor);
		graphics.fillRect(0, 0, result.getWidth(), result.getHeight());
		graphics.setColor(theme.backgroundColor);
		for(int i=0; i<elements.size(); i++)
		{
			int x = border;
			int y = (cellHeight + theme.internalBorder) * i + border;
			graphics.fillRect(x, y, cellWidth, cellHeight);
			graphics.drawImage(elements.get(i), x, y, null);
		}
		return result;
	}
	
	@Override
	public Rectangle preferredSize(int cellWidth, int cellHeight, int count, Theme theme, boolean inner)
	{
		int border = theme.getExternalBorder(inner);
		int height = cellHeight * count + theme.internalBorder*(count-1) + 2*border;
		int width = cellWidth + 2*border;
		return new Rectangle(width, height);
	}
	
	@Override
	public Rectangle cellSize(Rectangle totalSize, int count, Theme theme, boolean inner)
	{
		if(count == 0)return new Rectangle(1,1);
		int border = theme.getExternalBorder(inner);
		int width = totalSize.width - 2*border;
		int height = totalSize.height - 2*border;
		height -= (count-1) * theme.internalBorder;
		height /= count;
		return new Rectangle(width, height);
	}
	
	@Override
	public ListAssemblingStrategy getPerpendicular()
	{
		return HorizontalAssemblingStrategy.getInstance();
	}
}
