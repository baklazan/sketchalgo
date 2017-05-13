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
	
	private BufferedImage assemblePart(ArrayList<BufferedImage> elements, int cellWidth, int cellHeight, int index, Theme theme)
	{
		int border = theme.getExternalBorder(false);
		if(index >= elements.size()) return null;
		BufferedImage left = assemblePart(elements, cellWidth, cellHeight, index*2, theme);
		BufferedImage right = assemblePart(elements, cellWidth, cellHeight, index*2+1, theme);
		int width = cellWidth + 2*border, height = cellHeight + 2*border;
		if(left != null)
		{
			width = 2*left.getWidth();
			height += Math.min(cellHeight, cellWidth)/2 + left.getHeight();
		}
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = result.createGraphics();
		graphics.setColor(theme.borderColor);
		graphics.fillRect((width - cellWidth - 2*border)/2, 0, cellWidth + 2*border, cellHeight + 2*border);
		graphics.setColor(theme.backgroundColor);
		graphics.fillRect((width - cellWidth)/2, border, cellWidth, cellHeight);
		graphics.drawImage(elements.get(index), (width - cellWidth)/2, border, null);
		graphics.setColor(theme.borderColor);
		if(left != null)
		{
			graphics.drawImage(left, 0, height - left.getHeight(), null);
			graphics.drawLine((width-cellWidth-border)/2, cellHeight + border*3/2, left.getWidth()/2,height - left.getHeight()+1);
		}
		if(right != null)
		{
			graphics.drawImage(right, (width/2 * 3 - right.getWidth())/2, height - left.getHeight(), null);
			graphics.drawLine((width+cellWidth+border)/2, 
			                  cellHeight + border*3/2,
			                  (width + left.getWidth())/2,height - left.getHeight()+1);
		}
		return result;
	}
	
	@Override
	public BufferedImage assemble(ArrayList<BufferedImage> elements, int cellWidth, int cellHeight, Theme theme, boolean inner)
	{
		BufferedImage result = assemblePart(elements, cellWidth, cellHeight, 1, theme);
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
	public Rectangle preferredSize(int cellWidth, int cellHeight, int count, Theme theme, boolean inner)
	{
		if(count <= 1)return new Rectangle(1, 1);
		int layers = numberOfLayers(count);
		int width = widthOfBottom(count) * (cellWidth+2*theme.getExternalBorder(inner));
		int height = layers * (cellHeight+2*theme.getExternalBorder(inner)) + Math.min(cellWidth, cellHeight)/2 * (layers-1);
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