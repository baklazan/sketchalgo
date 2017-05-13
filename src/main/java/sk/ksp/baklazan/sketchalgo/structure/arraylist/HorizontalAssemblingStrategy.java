package sk.ksp.baklazan.sketchalgo.structure.arraylist;
import sk.ksp.baklazan.sketchalgo.structure.*;
import sk.ksp.baklazan.sketchalgo.Theme;
import java.util.*;
import java.lang.*;
import java.awt.image.*;
import java.awt.Graphics2D;
import java.awt.geom.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import java.awt.Rectangle;
import javafx.scene.*;
import javafx.application.*;

/** Cells horizontally */
public class HorizontalAssemblingStrategy extends ListAssemblingStrategy
{
	private HorizontalAssemblingStrategy()
	{
	}
	
	private static class singletonHolder
	{
		private static final HorizontalAssemblingStrategy INSTANCE = new HorizontalAssemblingStrategy();
	}
	
	public static HorizontalAssemblingStrategy getInstance()
	{
		return singletonHolder.INSTANCE;
	}
	
	@Override
	public BufferedImage assemble(ArrayList<BufferedImage> elements, Rectangle cellSize, Theme theme, boolean inner)
	{
		int border = theme.getExternalBorder(inner);
		int width = cellSize.width * elements.size();
		width += theme.internalBorder * Math.max(0,elements.size()-1);
		width += 2*border;
		width = Math.max(1, width);
		int height = cellSize.height + 2*border;
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = result.createGraphics();
		graphics.setColor(theme.borderColor);
		graphics.fillRect(0, 0, result.getWidth(), result.getHeight());
		graphics.setColor(theme.backgroundColor);
		for(int i=0; i<elements.size(); i++)
		{
			int x = (cellSize.width + theme.internalBorder) * i + border;
			int y = border;
			graphics.fillRect(x, y, cellSize.width, cellSize.height);
			graphics.drawImage(elements.get(i), x, y, null);
		}
		return result;
	}
	
	@Override
	public Rectangle preferredSize(Rectangle cellSize, int count, Theme theme, boolean inner)
	{
		int border = theme.getExternalBorder(inner);
		int width = cellSize.width * count + theme.internalBorder * (count-1) + 2*border;
		int height = cellSize.height + 2*border;
		return new Rectangle(width, height);
	}
	
	@Override
	public Rectangle cellSize(Rectangle totalSize, int count, Theme theme, boolean inner)
	{
		int border = theme.getExternalBorder(inner);
		if(count == 0)return new Rectangle(1,1);
		int width = totalSize.width - 2*border;
		width -= (count-1)*theme.internalBorder;
		width /= count;
		int height = totalSize.height - 2*border;
		return new Rectangle(width, height);
	}
	
	@Override
	public ListAssemblingStrategy getPerpendicular()
	{
		return VerticalAssemblingStrategy.getInstance();
	}
}
