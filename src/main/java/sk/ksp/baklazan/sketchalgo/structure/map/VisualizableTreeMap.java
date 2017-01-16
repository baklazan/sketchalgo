package sk.ksp.baklazan.sketchalgo.structure.map;
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


public class VisualizableTreeMap<K, V> extends TreeMap<K, V> implements VisualizableStructure, StructureDisplayer
{
	private int keyCellWidth, valCellWidth, keyCellHeight, valCellHeight;
	private StructureDisplayer displayer;
	private String myName;
	private TreeSet<K> beingWritten, beingAdded;
	private TreeSet<Object> beingRemoved, beingRead;
	private SleepConstants sleepConstants;
		
	public static class SleepConstants
	{
		private int sleepGet, sleepSet, sleepAdd, sleepRemove;
		private boolean batchGet;
		public SleepConstants(int sleepAdd, int sleepGet, int sleepRemove, int sleepSet, boolean batchGet)
		{
			this.sleepGet = sleepGet;
			this.sleepSet = sleepSet;
			this.sleepAdd = sleepAdd;
			this.sleepRemove = sleepRemove;
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
		keyCellHeight = Math.max(keyCellHeight, 20);
		keyCellWidth = Math.max(keyCellWidth, 20);
		valCellHeight = Math.max(valCellHeight, 20);
		valCellWidth = Math.max(valCellWidth, 20);
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
	
	
	@Override
	public BufferedImage draw()
	{
		ArrayList<BufferedImage> keyImage = new ArrayList<BufferedImage>(), valImage = new ArrayList<BufferedImage>();
		ArrayList<K> ithKey = new ArrayList<K>();
		int maxKeyWidth = 0, maxKeyHeight = 0, maxValWidth = 0, maxValHeight = 0;
		for(Map.Entry<K, V> entry : super.entrySet())
		{
			ithKey.add(entry.getKey());
			BufferedImage image = ObjectDrawer.draw(entry.getKey());
			keyImage.add(image);
			maxKeyWidth = Math.max(maxKeyWidth, image.getWidth());
			maxKeyHeight = Math.max(maxKeyHeight, image.getHeight());
			
			image = ObjectDrawer.draw(entry.getValue());
			valImage.add(image);
			maxValWidth = Math.max(maxValWidth, image.getWidth());
			maxValHeight = Math.max(maxValHeight, image.getHeight());
		}
		
		keyCellWidth = adjustDimension(keyCellWidth, maxKeyWidth);
		keyCellHeight = adjustDimension(keyCellHeight, maxKeyHeight);
		valCellWidth = adjustDimension(valCellWidth, maxValWidth);
		valCellHeight = adjustDimension(valCellHeight, maxValHeight);
		enforceMinSize();
		
		int cellWidth = keyCellWidth + valCellWidth + 1;
		int cellHeight = Math.max(keyCellHeight, valCellHeight);
		BufferedImage result = 
			new BufferedImage(cellWidth + 4, (cellHeight+2)*super.size()+2, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = result.createGraphics();
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, result.getWidth(), result.getHeight());
		graphics.setColor(Color.WHITE);
		for(int i=0; i<super.size(); i++)
		{
			int baseX = 2, baseY = (cellHeight+2)*i+2, baseValX = baseX + keyCellWidth + 1;
			graphics.fillRect(baseX, baseY, keyCellWidth, cellHeight);
			graphics.fillRect(baseValX, baseY, valCellWidth, cellHeight);
			BufferedImage image = keyImage.get(i);
			graphics.drawImage(image, 
			                   baseX + (keyCellWidth - image.getWidth())/2,
			                   baseY + (cellHeight - image.getHeight())/2, 
			                   null);
			image = valImage.get(i);
			graphics.drawImage(image, 
			                   baseValX + (valCellWidth - image.getWidth())/2,
			                   baseY + (cellHeight - image.getHeight())/2, 
			                   null);
			K key = ithKey.get(i);
			if(beingWritten.contains(key) || 
			   beingRead.contains(key) || 
			   beingAdded.contains(key) || 
			   beingRemoved.contains(key))
			{
				BufferedImage screen = new BufferedImage(cellWidth, cellHeight, BufferedImage.TYPE_INT_ARGB);
				Graphics2D gr = screen.createGraphics();
				if(beingRead.contains(key))gr.setColor(new Color(0, 200, 0, 100));
				if(beingWritten.contains(key))gr.setColor(new Color(255, 128, 0, 100));
				if(beingAdded.contains(key))gr.setColor(new Color(0, 0, 255, 100));
				if(beingRemoved.contains(key))gr.setColor(new Color(255, 0, 0, 100));
				gr.fillRect(0,0, screen.getWidth(), screen.getHeight());
				graphics.drawImage(screen, baseX, baseY, null);
			}
		}
		
		if(sleepConstants.batchGet)
		{
			beingRead.clear();
		}
		
		if(myName != null)
		{
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
		keyCellWidth = 0;
		keyCellHeight = 0;
		valCellWidth = 0;
		valCellHeight = 0;
		displayer = null;
		beingRead = new TreeSet<Object>();
		beingWritten = new TreeSet<K>();
		beingAdded = new TreeSet<K>();
		beingRemoved = new TreeSet<Object>();
		myName = name;
		sleepConstants = new SleepConstants(200, 100, 300, 200, false);
	}
	
	public void setSleepConstants(SleepConstants sleepConstants)
	{
		this.sleepConstants = sleepConstants;
	}
	
	public VisualizableTreeMap()
	{
		this(null);
	}
	
	public VisualizableTreeMap(String name)
	{
		super();
		init(name);
	}
	
	
	private void register(Object o)
	{
		if(o instanceof VisualizableStructure)
		{
			((VisualizableStructure)(o)).setDisplayer(this);
		}
	}
	
	@Override
	public V put(K key, V value)
	{
		V result = null;
		if(super.containsKey(key))
		{
			beingWritten.add(key);
			requestRedrawAndDelay(sleepConstants.sleepSet / 2);
			result = super.put(key, value);
			requestRedrawAndDelay(sleepConstants.sleepSet / 2);
			beingWritten.remove(key);
		}
		else
		{
			beingAdded.add(key);
			result = super.put(key, value);
			requestRedrawAndDelay(sleepConstants.sleepAdd);
			beingAdded.remove(key);
		}
		requestRedrawAndDelay(0);
		register(key);
		register(value);
		return result;
	}
	
	@Override
	public V remove(Object key)
	{
		if(super.containsKey(key))
		{
			beingRemoved.add(key);
			requestRedrawAndDelay(sleepConstants.sleepRemove);
			beingRemoved.remove(key);
		}
		V result = super.remove(key);
		requestRedrawAndDelay(0);
		return result;
	}
	
	@Override
	public V get(Object key)
	{
		if(super.containsKey(key))
		{
			beingRead.add(key);
			if(!sleepConstants.batchGet)
			{
				requestRedrawAndDelay(sleepConstants.sleepGet);
				beingRead.remove(key);
				requestRedrawAndDelay(0);
			}
		}
		return super.get(key);
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