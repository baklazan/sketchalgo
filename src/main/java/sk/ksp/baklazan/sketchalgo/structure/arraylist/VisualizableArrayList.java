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


public class VisualizableArrayList<E> extends ArrayList<E> implements VisualizableStructure, StructureDisplayer
{
	private int boxWidth, boxHeight;
	private int cellWidth, cellHeight;
	private StructureDisplayer displayer;
	private String myName;
	private ArrayList<Boolean> beingRead, beingWritten;
	private SleepConstants sleepConstants;
	private ListAssemblingStrategy assemblingStrategy;
	
	public static class SleepConstants
	{
		private int sleepGet, sleepSet, sleepAdd;
		private boolean batchGet;
		public SleepConstants(int sleepAdd, int sleepGet, int sleepSet, boolean batchGet)
		{
			this.sleepGet = sleepGet;
			this.sleepSet = sleepSet;
			this.sleepAdd = sleepAdd;
			this.batchGet = batchGet;
		}
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
		cellHeight = Math.max(cellHeight, 20);
		cellWidth = Math.max(cellWidth, 20);
	}
	
	private void requestRedrawAndDelay(int time)
	{
		if(displayer != null)
		{
			displayer.requestRedraw(this);
			if(time > 0)
			{
				try
				{
					Thread.sleep(time);
				}
				catch(Exception e)
				{
				}
			}
		}
	}
	
	
	private static BufferedImage draw(Object o)
	{
		if(o instanceof VisualizableStructure)
		{
			return ((VisualizableStructure)(o)).draw();
		}
		else
		{
			String string = o.toString();
			if(string.length() > 8) string = string.substring(0,8);
			
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
	
	@Override
	public BufferedImage draw()
	{
		ArrayList<BufferedImage> childrenImage = new ArrayList<BufferedImage>();
		int maxWidth = 0, maxHeight = 0;
		for(int i=0; i<super.size(); i++)
		{
			BufferedImage image = draw(super.get(i));
			childrenImage.add(image);
			maxWidth = Math.max(maxWidth, image.getWidth());
			maxHeight = Math.max(maxHeight, image.getHeight());
		}
		
		cellWidth = maxWidth;//adjustDimension(cellWidth, maxWidth);
		cellHeight = maxHeight;//adjustDimension(cellHeight, maxHeight);
		enforceMinSize();
		
		for(int i=0; i<super.size(); i++)
		{
			BufferedImage resized = new BufferedImage(cellWidth, cellHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = resized.createGraphics();
			BufferedImage image = childrenImage.get(i);
			graphics.drawImage(image, (cellWidth - image.getWidth())/2, (cellHeight - image.getHeight())/2, null);
			
			if(beingWritten.get(i) || beingRead.get(i))
			{
				BufferedImage screen = new BufferedImage(cellWidth, cellHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D gr = screen.createGraphics();
				if(beingRead.get(i))gr.setColor(new Color(0, 200, 0, 100));
				if(beingWritten.get(i))gr.setColor(new Color(255, 0, 0, 100));
				gr.fillRect(0,0, screen.getWidth(), screen.getHeight());
				graphics.drawImage(screen, 0, 0, null);
			}
			childrenImage.set(i, resized);
		}
		
		BufferedImage result = assemblingStrategy.assemble(childrenImage, cellWidth, cellHeight);
		
		if(sleepConstants.batchGet)
		{
			for(int i=0; i<beingRead.size(); i++)beingRead.set(i, false);
		}
		
		if(myName != null)
		{
			Graphics2D graphics = result.createGraphics();
			FontMetrics metrics = graphics.getFontMetrics();
			Rectangle2D rect = metrics.getStringBounds(myName, graphics);
			int height = result.getHeight() + (int)rect.getHeight()+4;
			int width = Math.max(result.getWidth(), (int)rect.getWidth()+4);
			BufferedImage captionedResult = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			graphics = captionedResult.createGraphics();
			graphics.setColor(Color.BLACK);
			graphics.drawString(myName, (int)(-rect.getX())+2, (int)(-rect.getY())+2);
			graphics.drawImage(result, 0, (int)rect.getHeight()+4, null);
			return captionedResult;
		}
		return result;
	}
	
	private void init(String name)
	{
		boxWidth = 0;
		boxHeight = 0;
		cellWidth = 0;
		cellHeight = 0;
		displayer = null;
		beingRead = new ArrayList<Boolean>();
		beingWritten = new ArrayList<Boolean>();
		myName = name;
		sleepConstants = new SleepConstants(0, 100, 300, false);
		assemblingStrategy = HorizontalAssemblingStrategy.getInstance();
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
	
	@Override
	public int getBoxWidth()
	{
		return boxWidth;
	}
	@Override
	public int getBoxHeight()
	{
		return boxHeight;
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
		requestRedrawAndDelay(sleepConstants.sleepSet / 2);
		beingWritten.set(index, false);
		ensureCompatibleStrategy(element);
		requestRedrawAndDelay(0);
		register(element);
		return result;
	}
	
	@Override
	public boolean add(E e)
	{
		super.add(e);
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
		beingRead.set(index, true);
		if(!sleepConstants.batchGet)
		{
			requestRedrawAndDelay(sleepConstants.sleepGet);
			beingRead.set(index, false);
			requestRedrawAndDelay(0);
		}
		return super.get(index);
	}
	
	@Override
	public void setDisplayer(StructureDisplayer displayer)
	{
		this.displayer = displayer;
		if(this.displayer != null)
		{
			this.displayer.requestRedraw(this);
		}
	}
	
	@Override
	public void requestRedraw(VisualizableStructure caller)
	{
		if(displayer != null) displayer.requestRedraw(this);
	}
	
	
	@Override
	public void requestResizeRedraw(VisualizableStructure caller)
	{
		if(displayer != null) displayer.requestResizeRedraw(this);
	}
}