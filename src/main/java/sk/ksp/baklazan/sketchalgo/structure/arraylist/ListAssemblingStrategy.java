package sk.ksp.baklazan.sketchalgo.structure.arraylist;
import sk.ksp.baklazan.sketchalgo.structure.*;
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

/** A class that puts images coresponding to elements of an ArrayList together */
public abstract class ListAssemblingStrategy
{
	public abstract BufferedImage assemble(ArrayList<BufferedImage> elements, int cellWidth, int cellHeight);
	
	public abstract ListAssemblingStrategy getPerpendicular();
}

/** Cells horizontally */
class HorizontalAssemblingStrategy extends ListAssemblingStrategy
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
	public BufferedImage assemble(ArrayList<BufferedImage> elements, int cellWidth, int cellHeight)
	{
		BufferedImage result = 
			new BufferedImage((cellWidth+2) * elements.size()+2, cellHeight+4, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = result.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, result.getWidth(), result.getHeight());
		graphics.setColor(Color.WHITE);
		for(int i=0; i<elements.size(); i++)
		{
			graphics.fillRect((cellWidth+2) * i+2, 2, cellWidth, cellHeight);
			graphics.drawImage(elements.get(i), (cellWidth+2)*i+2, 2, null);
		}
		return result;
	}
	
	@Override
	public ListAssemblingStrategy getPerpendicular()
	{
		return VerticalAssemblingStrategy.getInstance();
	}
}

/** Cells vertically */
class VerticalAssemblingStrategy extends ListAssemblingStrategy
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
	public BufferedImage assemble(ArrayList<BufferedImage> elements, int cellWidth, int cellHeight)
	{
		BufferedImage result = 
			new BufferedImage(cellWidth+4, (cellHeight+2)*elements.size()+2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = result.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, result.getWidth(), result.getHeight());
		graphics.setColor(Color.WHITE);
		for(int i=0; i<elements.size(); i++)
		{
			graphics.fillRect(2, (cellHeight+2)*i+2, cellWidth, cellHeight);
			graphics.drawImage(elements.get(i), 2, (cellHeight+2)*i+2, null);
		}
		return result;
	}
	
	@Override
	public ListAssemblingStrategy getPerpendicular()
	{
		return HorizontalAssemblingStrategy.getInstance();
	}
}

/**Cells in binary tree, where 1 is root and sons of X are X*2 and X*2+1.
 * Element 0 will be ignored.
 */
class TreeAssemblingStrategy extends ListAssemblingStrategy
{
	private TreeAssemblingStrategy()
	{
	}
	
	private static class singletonHolder
	{
		private static final TreeAssemblingStrategy INSTANCE = new TreeAssemblingStrategy();
	}
	
	public static TreeAssemblingStrategy getInstance()
	{
		return singletonHolder.INSTANCE;
	}
	
	private BufferedImage assemblePart(ArrayList<BufferedImage> elements, int cellWidth, int cellHeight, int index)
	{
		if(index >= elements.size()) return null;
		BufferedImage left = assemblePart(elements, cellWidth, cellHeight, index*2);
		BufferedImage right = assemblePart(elements, cellWidth, cellHeight, index*2+1);
		int width = cellWidth + 4, height = cellHeight + 4;
		if(left != null)
		{
			width = 2*left.getWidth();
			height += Math.min(cellHeight, cellWidth)/2 + left.getHeight();
		}
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = result.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect((width - cellWidth - 4)/2, 0, cellWidth + 4, cellHeight + 4);
		graphics.setColor(Color.WHITE);
		graphics.fillRect((width - cellWidth)/2, 2, cellWidth, cellHeight);
		graphics.drawImage(elements.get(index), (width - cellWidth)/2, 2, null);
		graphics.setColor(Color.BLACK);
		if(left != null)
		{
			graphics.drawImage(left, 0, height - left.getHeight(), null);
			graphics.drawLine((width-cellWidth)/2-1, cellHeight + 3, left.getWidth()/2,height - left.getHeight()+1);
		}
		if(right != null)
		{
			graphics.drawImage(right, (width/2 * 3 - right.getWidth())/2, height - left.getHeight(), null);
			graphics.drawLine((width+cellWidth)/2+1, cellHeight + 3, (width + left.getWidth())/2,height - left.getHeight()+1);
		}
		return result;
	}
	
	@Override
	public BufferedImage assemble(ArrayList<BufferedImage> elements, int cellWidth, int cellHeight)
	{
		BufferedImage result = assemblePart(elements, cellWidth, cellHeight, 1);
		if(result == null) return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		return result;
	}
	
	@Override
	public ListAssemblingStrategy getPerpendicular()
	{
		return null;
	}
}