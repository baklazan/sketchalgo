package sk.ksp.baklazan.sketchalgo.structure.arraylist;
import sk.ksp.baklazan.sketchalgo.structure.*;
import sk.ksp.baklazan.sketchalgo.Theme;
import sk.ksp.baklazan.sketchalgo.DefaultTheme;
import java.util.*;
import java.lang.*;
import java.awt.image.*;
import java.awt.Rectangle;
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


public class VisualizableArrayList<E> extends ArrayList<E> implements VisualizableStructure, StructureDisplayer
{
	private int cellWidth, cellHeight;
	private Theme theme;
	private StructureDisplayer displayer;
	private String myName;
	private ArrayList<Boolean> beingRead, beingWritten;
	private SleepConstants sleepConstants;
	private ListAssemblingStrategy assemblingStrategy;
	private boolean inner;
	
	public static class SleepConstants
	{
		public static enum GetType {BATCH, INSTANT, SILENT}
		private int sleepGet, sleepSet, sleepAdd;
		private GetType getType;
		public SleepConstants(int sleepAdd, int sleepGet, int sleepSet, GetType getType)
		{
			this.sleepGet = sleepGet;
			this.sleepSet = sleepSet;
			this.sleepAdd = sleepAdd;
			this.getType = getType;
		}
	}
	
	@Override
	public void setInner(boolean inner)
	{
		this.inner = inner;
	}
	
	public void setTheme(Theme theme)
	{
		this.theme = theme;
	}
	
	private int adjustDimension(int old, int requested)
	{
		if(old < requested) return Math.max((int)(old * 1.5), requested);
		if(old > requested)
		{
			if(requested * 5 > old) return old;
			return requested;
		}
		return old;
	}
	
	private void enforceMinSize()
	{
		cellHeight = Math.max(cellHeight, theme.minCellHeight);
		cellWidth = Math.max(cellWidth, theme.minCellWidth);
	}
	
	private void requestRedrawAndDelay(int time)
	{
		if(displayer != null)
		{
			displayer.requestRedraw(this, time);
		}
	}
	
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
		cellWidth = Math.max(cellSize.width, 1);
		cellHeight = Math.max(cellSize.height, 1);
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
		
		BufferedImage result = assemblingStrategy.assemble(childrenImage, cellWidth, cellHeight, theme, inner);
		
		if(sleepConstants.getType == SleepConstants.GetType.BATCH)
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
		
		cellWidth = maxWidth;
		cellHeight = maxHeight;
		enforceMinSize();
		return assemblingStrategy.preferredSize(cellWidth, cellHeight, super.size(), theme, inner);
	}
	
	private void init(String name)
	{
		cellWidth = 0;
		cellHeight = 0;
		displayer = null;
		beingRead = new ArrayList<Boolean>();
		beingWritten = new ArrayList<Boolean>();
		myName = name;
		sleepConstants = new SleepConstants(0, 100, 300, SleepConstants.GetType.INSTANT);
		assemblingStrategy = HorizontalAssemblingStrategy.getInstance();
		theme = new DefaultTheme();
		inner = false;
	}
	
	public void setSleepConstants(SleepConstants sleepConstants)
	{
		this.sleepConstants = sleepConstants;
	}
	
	public VisualizableArrayList()
	{
		this(null);
	}
	
	public VisualizableArrayList(String name)
	{
		super();
		init(name);
	}
	
	
	public void setAssemblingStrategy(ListAssemblingStrategy strategy)
	{
		assemblingStrategy = strategy;
		for(int i=0; i<super.size(); i++)
		{
			ensureCompatibleStrategy(super.get(i));
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
	
	private void setToInner(Object o)
	{
		if(o instanceof VisualizableStructure)
		{
			((VisualizableStructure)(o)).setInner(true);
		}
	}
	
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
			case INSTANT:
			{
				beingRead.set(index, true);
				requestRedrawAndDelay(sleepConstants.sleepGet);
				beingRead.set(index, false);
				requestRedrawAndDelay(0);
			}
			case BATCH:
			{
				beingRead.set(index, true);
				break;
			}
			case SILENT:
			{
				break;
			}
		}
		return super.get(index);
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
	public void requestRedraw(VisualizableStructure caller, int delay)
	{
		if(displayer != null) displayer.requestRedraw(this, delay);
	}
	

}