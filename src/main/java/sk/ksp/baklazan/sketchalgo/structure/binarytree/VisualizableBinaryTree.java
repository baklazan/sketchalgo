package sk.ksp.baklazan.sketchalgo.structure.binarytree;
import sk.ksp.baklazan.sketchalgo.structure.*;
import sk.ksp.baklazan.sketchalgo.Theme;
import sk.ksp.baklazan.sketchalgo.DefaultTheme;
import java.util.*;
import java.lang.*;
import java.awt.image.*;
import java.awt.Rectangle;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.awt.FontMetrics;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.scene.*;
import javafx.application.*;


public class VisualizableBinaryTree extends BinaryTree implements VisualizableStructure
{
	/** Basic VisualizableStructure information*/
	private String myName;
	private StructureDisplayer displayer;
	
	/** Strategies (for customization)*/
	private Theme theme;
	
	
	/** VisualizableStructure overridden methods*/
	
	@Override
	public BufferedImage draw()
	{
		return draw(null);
	}
	
	@Override
	public BufferedImage draw(Rectangle size)
	{
		if(size == null) size = preferredSize();
		if(root == null) return new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		if(size.width <= 0) size.width = 1;
		if(size.height <= 0) size.height = 1;
		
		Rectangle useSize = cropToPreferred(size);
		
		Rectangle cellSize = computeCellSize(useSize);
		BufferedImage image = drawSubtree(root, cellSize).image;
		BufferedImage result = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gr = result.createGraphics();
		gr.drawImage(image, (size.width - image.getWidth())/2, (size.height - image.getHeight())/2, null);
		
		if(myName != null)
		{
			Graphics2D graphics = result.createGraphics();
			FontMetrics metrics = graphics.getFontMetrics();
			Rectangle2D rect = metrics.getStringBounds(myName, graphics);
			int height = result.getHeight() + (int)rect.getHeight()+2*theme.textMargin;
			int width = Math.max(result.getWidth(), (int)rect.getWidth()+2*theme.textMargin);
			BufferedImage captionedResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			graphics = captionedResult.createGraphics();
			graphics.setColor(theme.textColor);
			graphics.drawString(myName, (int)(-rect.getX())+theme.textMargin, (int)(-rect.getY())+theme.textMargin);
			graphics.drawImage(result, 0, (int)rect.getHeight()+2*theme.textMargin, null);
			return captionedResult;
		}
		return result;
	}
	
	@Override
	public Rectangle preferredSize()
	{
		if(root == null) return new Rectangle(1,1);
		Rectangle cellSize = preferredCellSize(root);
		return preferredTreeSize(root, cellSize);
	}
	
	@Override
	public void setDisplayer(StructureDisplayer displayer)
	{
		this.displayer = displayer;
		if(this.displayer != null)
		{
			this.displayer.requestRedraw(this, 0);
		}
	}
	
	@Override
	public void setInner(boolean inner)
	{
	}
	
	
	/** Constructors and related methods*/
	
	public VisualizableBinaryTree(BinaryNode root)
	{
		this(root, null);
	}
	
	public VisualizableBinaryTree(BinaryNode root, String name)
	{
		super(root);
		this.myName = name;
	}
	
	/** public setters */
	
	public void setTheme(Theme theme)
	{
		this.theme = theme;
	}
	
	
	/** misc private methods */
	
	private void enforceMinSize(Rectangle rect)
	{
		rect.height = Math.max(rect.height, theme.minCellHeight);
		rect.width = Math.max(rect.width, theme.minCellWidth);
	}
	
	private Rectangle cropToPreferred(Rectangle size)
	{
		Rectangle preferred = preferredSize();
		return new Rectangle(Math.min(size.width, preferred.width), Math.min(size.height, preferred.height));
	}
	
	private Rectangle preferredCellSize(BinaryNode node)
	{
		Rectangle result = ObjectDrawer.preferredSize(node.getValue(), theme);
		if(node.getLeft() != null)
		{
			Rectangle left = preferredCellSize(node.getLeft());
			result.width = Math.max(result.width, left.width);
			result.height = Math.max(result.height, left.height);
		}
		if(node.getRight() != null)
		{
			Rectangle right = preferredCellSize(node.getRight());
			result.width = Math.max(result.width, right.width);
			result.height = Math.max(result.height, right.height);
		}
		enforceMinSize(result);
		return result;
	}
	
	private Rectangle preferredTreeSize(BinaryNode node, Rectangle cellSize)
	{
		if(node == null) return new Rectangle(0,0);
		Rectangle left = preferredTreeSize(node.getLeft(), cellSize);
		Rectangle right = preferredTreeSize(node.getRight(), cellSize);
		int width = left.width + cellSize.width + right.width + 2*theme.externalBorderOuter;
		int height = cellSize.height + Math.max(left.height, right.height) + 2*theme.externalBorderOuter;
		if(node.getLeft() != null || node.getRight() != null)
		{
			height += Math.min(cellSize.width, cellSize.height)/2;
		}
		return new Rectangle(width, height); 
	}
	
	private Rectangle computeCellSize(Rectangle size)
	{
		int border = 2*theme.externalBorderOuter;
		int numberOfNodes = nodesUnder(root);
		int width = size.width / numberOfNodes - border;
		int maxDepth = maximumDepth(root);
		int heightIfSquare = maxDepth * (width+border) + (maxDepth-1)*width/2;
		int height;
		if(heightIfSquare > size.height)
		{
			height = (size.height - maxDepth * border)/(maxDepth*3-1) *2;
			if(height % 2 == 0)
			{
				if((height+border) * maxDepth + height/2 * (maxDepth-1) <= size.height-maxDepth)
				{
					height++;
				}
			}
		}
		else
		{
			height = (size.height - maxDepth*border - (maxDepth-1)*width/2)/maxDepth;
		}
		return new Rectangle(width, height);
	}
	
	private class ImageAndPosition
	{
		public BufferedImage image;
		public int position;
		public ImageAndPosition(BufferedImage image, int position)
		{
			this.image = image;
			this.position = position;
		}
	}
	
	private ImageAndPosition drawSubtree(BinaryNode node, Rectangle cellSize)
	{
		if(node == null)return null;
		int border = theme.externalBorderOuter;
		ImageAndPosition leftSubtree = drawSubtree(node.getLeft(), cellSize);
		ImageAndPosition rightSubtree = drawSubtree(node.getRight(), cellSize);
		int spaceHeight = Math.min(cellSize.width, cellSize.height)/2;
		int height = cellSize.height + 2*border;
		int width = cellSize.width + 2*border;
		if(leftSubtree != null || rightSubtree != null)
		{
			height += spaceHeight;
			int childrenHeight = 0;
			if(leftSubtree != null)
			{
				childrenHeight = Math.max(childrenHeight, leftSubtree.image.getHeight());
				width += leftSubtree.image.getWidth();
			}
			if(rightSubtree != null)
			{
				childrenHeight = Math.max(childrenHeight, rightSubtree.image.getHeight());
				width += rightSubtree.image.getWidth();
			}
			height += childrenHeight;
		}
		BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = result.createGraphics();
		int verticalOffset = cellSize.height + 2*border + spaceHeight;
		int horizontalOffset = 0;
		if(leftSubtree != null)
		{
			graphics.drawImage(leftSubtree.image, 0, verticalOffset, null);
			horizontalOffset = leftSubtree.image.getWidth();
			graphics.setColor(theme.borderColor);
			int xFrom = leftSubtree.position, yFrom = verticalOffset;
			int xTo = leftSubtree.image.getWidth(), yTo = cellSize.height + 2*border;
			graphics.drawLine(xFrom, yFrom, xTo, yTo);
		}
		graphics.setColor(theme.borderColor);
		graphics.fillRect(horizontalOffset, 0, cellSize.width + 2*border, cellSize.height+2*border);
		graphics.setColor(theme.backgroundColor);
		graphics.fillRect(horizontalOffset+border, border, cellSize.width, cellSize.height);
		graphics.drawImage(ObjectDrawer.draw(node.getValue(), theme, cellSize), horizontalOffset+border, border, null);
		int position = horizontalOffset + cellSize.width/2 + border;
		horizontalOffset += cellSize.width + 2*border;
		if(rightSubtree != null)
		{
			graphics.drawImage(rightSubtree.image, horizontalOffset, verticalOffset, null);
			graphics.setColor(theme.borderColor);
			int xFrom = horizontalOffset + rightSubtree.position, yFrom = verticalOffset;
			int xTo = horizontalOffset, yTo = cellSize.height + 2*border;
			graphics.drawLine(xFrom, yFrom, xTo, yTo);
		}
		return new ImageAndPosition(result, position);
	}
	
	private int maximumDepth(BinaryNode node)
	{
		if(node == null)return 0;
		return 1 + Math.max(maximumDepth(node.getLeft()), maximumDepth(node.getRight()));
	}
	
	private int nodesUnder(BinaryNode node)
	{
		if(node == null)return 0;
		return 1 + nodesUnder(node.getLeft()) + nodesUnder(node.getRight());
	}
}