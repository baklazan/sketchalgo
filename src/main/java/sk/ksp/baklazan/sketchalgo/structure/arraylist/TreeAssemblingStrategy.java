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
import javafx.scene.*;
import javafx.application.*;
import java.awt.Rectangle;


/**Cells in binary tree, where 1 is root and sons of X are X*2 and X*2+1.
 * Element 0 will be ignored.
 */
public class TreeAssemblingStrategy extends ListAssemblingStrategy
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
	
	private BufferedImage assemblePart(ArrayList<BufferedImage> elements, Rectangle cellSize, int index, Theme theme)
	{
		int border = theme.getExternalBorder(false);
		if(index >= elements.size()) return null;
		BufferedImage left = assemblePart(elements, cellSize, index*2, theme);
		BufferedImage right = assemblePart(elements, cellSize, index*2+1, theme);
		int width = cellSize.width + 2*border, height = cellSize.height + 2*border;
		if(left != null)
		{
			width = 2*left.getWidth();
			height += Math.min(cellSize.height, cellSize.width)/2 + left.getHeight();
		}
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = result.createGraphics();
		graphics.setColor(theme.borderColor);
		graphics.fillRect((width - cellSize.width - 2*border)/2, 0, cellSize.width + 2*border, cellSize.height + 2*border);
		graphics.setColor(theme.backgroundColor);
		graphics.fillRect((width - cellSize.width)/2, border, cellSize.width, cellSize.height);
		graphics.drawImage(elements.get(index), (width - cellSize.width)/2, border, null);
		graphics.setColor(theme.borderColor);
		if(left != null)
		{
			graphics.drawImage(left, 0, height - left.getHeight(), null);
			int xFrom = (width-cellSize.width-border)/2, yFrom = cellSize.height + border*3/2;
			int xTo = left.getWidth()/2, yTo = height - left.getHeight()+1;
			graphics.drawLine(xFrom, yFrom, xTo, yTo);
		}
		if(right != null)
		{
			graphics.drawImage(right, (width/2 * 3 - right.getWidth())/2, height - left.getHeight(), null);
			int xFrom = (width+cellSize.width+border)/2, yFrom = cellSize.height + border*3/2;
			int xTo = (width + left.getWidth())/2, yTo = height - left.getHeight()+1;
			graphics.drawLine(xFrom, yFrom, xTo, yTo);
		}
		return result;
	}
	
	@Override
	public BufferedImage assemble(ArrayList<BufferedImage> elements, Rectangle cellSize, Theme theme, boolean inner)
	{
		BufferedImage result = assemblePart(elements, cellSize, 1, theme);
		if(result == null) return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		return result;
	}
	
	private int numberOfLayers(int count)
	{
		count--;
		int result = 0;
		for(int power = 1; power <= count; power *= 2)result++;
		return result;
	}
	
	private int widthOfBottom(int count)
	{
		if(count <= 1)return 0;
		int l = numberOfLayers(count);
		int result = 1;
		for(int i=1; i<l; i++) result *= 2;
		return result;
	}
	
	@Override
	public Rectangle preferredSize(Rectangle cellSize, int count, Theme theme, boolean inner)
	{
		if(count <= 1)return new Rectangle(1, 1);
		int layers = numberOfLayers(count);
		int width = widthOfBottom(count) * (cellSize.width+2*theme.getExternalBorder(inner));
		int height = layers * (cellSize.height+2*theme.getExternalBorder(inner));
		height += Math.min(cellSize.width, cellSize.height)/2 * (layers-1);
		return new Rectangle(width, height);
	}
	
	@Override
	public Rectangle cellSize(Rectangle totalSize, int count, Theme theme, boolean inner)
	{
		if(count <= 1) return new Rectangle(totalSize.width, totalSize.height);
		int layers = numberOfLayers(count);
		int bottomWidth = widthOfBottom(count);
		int border = theme.getExternalBorder(inner);
		int width = totalSize.width / bottomWidth - 2*border;
		int heightIfSquare = layers * (width+2*border) + width/2*(layers-1);
		int height;
		if(heightIfSquare > totalSize.height)
		{
			height = (int)((totalSize.height-layers * 2*border) / (layers + (double)(layers-1)/2));
		}
		else
		{
			height = (totalSize.height - width/2 * (layers-1))/layers - 2*border;
		}
		return new Rectangle(width, height);
	}
	
	@Override
	public ListAssemblingStrategy getPerpendicular()
	{
		return null;
	}
}