package sk.ksp.baklazan.sketchalgo.structure.arraylist;
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


public class VisualizableArrayList<E> extends ArrayList<E> implements VisualizableStructure, StructureDisplayer
{
	/** Basic VisualizableStructure information*/
	private String myName;
	private StructureDisplayer displayer;
	private boolean inner;
	
	/** Strategies (for customization)*/
	private SleepConstants sleepConstants;
	private Theme theme;
	private ListAssemblingStrategy assemblingStrategy;
	
	/** Internal stuff*/
	private ArrayList<Boolean> beingRead, beingWritten;
	
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
		if(size.width <= 0) size.width = 1;
		if(size.height <= 0) size.height = 1;
		Rectangle cellSize = assemblingStrategy.cellSize(size, super.size(), theme, inner);
		cellSize.width = Math.max(cellSize.width, 1);
		cellSize.height = Math.max(cellSize.height, 1);
		ArrayList<BufferedImage> childrenImage = new ArrayList<BufferedImage>();
		for(int i=0; i<super.size(); i++)
		{
			BufferedImage image = ObjectDrawer.draw(super.get(i), theme, cellSize);
			childrenImage.add(image);
		}
		
		for(int i=0; i<super.size(); i++)
		{
			BufferedImage image = childrenImage.get(i);
			if(beingWritten.get(i))
			{
				image = theme.drawSet(image);
			}
			else if(beingRead.get(i))
			{
				image = theme.drawGet(image);
			}
			childrenImage.set(i, image);
		}
		
		BufferedImage result = assemblingStrategy.assemble(childrenImage, cellSize, theme, inner);
		
		if(sleepConstants.getType == SleepConstants.GET_BATCH)
		{
			for(int i=0; i<beingRead.size(); i++)beingRead.set(i, false);
		}
		
		if(result.getWidth() > size.width || result.getHeight() > size.height)
		{
			BufferedImage croppedResult = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D crGr = croppedResult.createGraphics();
			crGr.drawImage(result, 0, 0, null);
			result = croppedResult;
		}
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
		int maxWidth = 0, maxHeight = 0;
		for(int i=0; i<super.size(); i++)
		{
			Rectangle size = ObjectDrawer.preferredSize(super.get(i), theme);
			maxWidth = Math.max(maxWidth, (int)size.getWidth());
			maxHeight = Math.max(maxHeight, (int)size.getHeight());
		}
		
		Rectangle cellSize = new Rectangle(maxWidth, maxHeight);
		enforceMinSize(cellSize);
		return assemblingStrategy.preferredSize(cellSize, super.size(), theme, inner);
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
		this.inner = inner;
	}
	
	/** StructureDislplayer overriden methods*/
	
	@Override
	public void requestRedraw(VisualizableStructure caller, int delay)
	{
		if(displayer != null) displayer.requestRedraw(this, delay);
	}
	
	/** ArrayList[E] overriden methods */
	
	@Override
	public boolean add(E e)
	{
		super.add(e);
		setToInner(e);
		beingRead.add(false);
		beingWritten.add(false);
		ensureCompatibleStrategy(e);
		requestRedrawAndDelay(sleepConstants.sleepAdd);
		register(e);
		return true;
	}
	
	@Override
	public E get(int index)
	{
		switch(sleepConstants.getType)
		{
			case SleepConstants.GET_INSTANT:
			{
				beingRead.set(index, true);
				requestRedrawAndDelay(sleepConstants.sleepGet);
				beingRead.set(index, false);
				requestRedrawAndDelay(0);
				break;
			}
			case SleepConstants.GET_BATCH:
			{
				beingRead.set(index, true);
				break;
			}
			case SleepConstants.GET_SILENT:
			{
				break;
			}
		}
		return super.get(index);
	}
	
	@Override
	public E set(int index, E element)
	{
		
		beingWritten.set(index, true);
		requestRedrawAndDelay(sleepConstants.sleepSet / 2);
		E result = super.set(index, element);
		ensureCompatibleStrategy(element);
		requestRedrawAndDelay(sleepConstants.sleepSet / 2);
		beingWritten.set(index, false);
		requestRedrawAndDelay(0);
		register(element);
		return result;
	}
	
	/** Constructors and related methods*/
	
	public VisualizableArrayList()
	{
		this(null);
	}
	
	public VisualizableArrayList(String name)
	{
		super();
		init(name);
	}
	
	private void init(String name)
	{
		displayer = null;
		beingRead = new ArrayList<Boolean>();
		beingWritten = new ArrayList<Boolean>();
		myName = name;
		sleepConstants = new SleepConstants(0, 100, 0, 300, SleepConstants.GET_INSTANT);
		assemblingStrategy = HorizontalAssemblingStrategy.getInstance();
		theme = new DefaultTheme();
		inner = false;
	}
	
	/** public setters */
	
	public void setTheme(Theme theme)
	{
		this.theme = theme;
	}
	
	public void setSleepConstants(SleepConstants sleepConstants)
	{
		this.sleepConstants = sleepConstants;
	}
	
	public void setAssemblingStrategy(ListAssemblingStrategy strategy)
	{
		assemblingStrategy = strategy;
		for(int i=0; i<super.size(); i++)
		{
			ensureCompatibleStrategy(super.get(i));
		}
	}
	
	/** misc private methods */
	
	private void enforceMinSize(Rectangle rect)
	{
		rect.height = Math.max(rect.height, theme.minCellHeight);
		rect.width = Math.max(rect.width, theme.minCellWidth);
	}
	
	private void requestRedrawAndDelay(int time)
	{
		if(displayer != null)
		{
			displayer.requestRedraw(this, time);
		}
	}
	
	private void ensureCompatibleStrategy(Object o)
	{
		if(o instanceof VisualizableArrayList)
		{
			VisualizableArrayList list = (VisualizableArrayList)(o);
			
			if(list.assemblingStrategy == this.assemblingStrategy)
			{
				ListAssemblingStrategy perpendicular = this.assemblingStrategy.getPerpendicular();
				if(perpendicular != null)
				{
					list.setAssemblingStrategy(perpendicular);
				}
			}
		}
	}
	
	private void register(Object o)
	{
		if(o instanceof VisualizableStructure)
		{
			((VisualizableStructure)(o)).setDisplayer(this);
		}
	}
	
	private void setToInner(Object o)
	{
		if(o instanceof VisualizableStructure)
		{
			((VisualizableStructure)(o)).setInner(true);
		}
	}
	
}