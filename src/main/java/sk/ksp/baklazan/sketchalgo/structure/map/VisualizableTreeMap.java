package sk.ksp.baklazan.sketchalgo.structure.map;
import sk.ksp.baklazan.sketchalgo.structure.*;
import sk.ksp.baklazan.sketchalgo.Theme;
import sk.ksp.baklazan.sketchalgo.DefaultTheme;
import java.util.*;
import java.lang.*;
import java.awt.image.*;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.geom.*;
import java.awt.Rectangle;
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
	private Theme theme;
	private StructureDisplayer displayer;
	private String myName;
	private TreeSet<K> beingWritten, beingAdded;
	private TreeSet<Object> beingRemoved, beingRead;
	private SleepConstants sleepConstants;
	private boolean inner;
	
	
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
	
	public void setTheme(Theme theme)
	{
		this.theme = theme;
	}
	
	@Override
	public void setInner(boolean inner)
	{
		this.inner = inner;
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
		keyCellHeight = Math.max(keyCellHeight, theme.minCellHeight);
		keyCellWidth = Math.max(keyCellWidth, theme.minCellWidth);
		valCellHeight = Math.max(valCellHeight, theme.minCellHeight);
		valCellWidth = Math.max(valCellWidth, theme.minCellWidth);
	}
	
	private void requestRedrawAndDelay(int time)
	{
		if(displayer != null)
		{
			displayer.requestRedraw(this, time);
		}
	}
	
	private Rectangle preferredKeySize()
	{
		int maxKeyWidth = 0, maxKeyHeight = 0;
		for(Map.Entry<K, V> entry : super.entrySet())
		{
			Rectangle keyRect = ObjectDrawer.preferredSize(entry.getKey(), theme);
			maxKeyWidth = Math.max(maxKeyWidth, keyRect.width);
			maxKeyHeight = Math.max(maxKeyHeight, keyRect.height);
		}
		
		keyCellWidth = maxKeyWidth;//adjustDimension(keyCellWidth, maxKeyWidth);
		keyCellHeight = maxKeyHeight;//adjustDimension(keyCellHeight, maxKeyHeight);
		enforceMinSize();
		return new Rectangle(keyCellWidth, keyCellHeight);
	}
	
	private Rectangle preferredValSize()
	{
		int maxValWidth = 0, maxValHeight = 0;
		for(Map.Entry<K, V> entry : super.entrySet())
		{
			Rectangle valRect = ObjectDrawer.preferredSize(entry.getValue(), theme);
			maxValWidth = Math.max(maxValWidth, valRect.width);
			maxValHeight = Math.max(maxValHeight, valRect.height);
		}
		
		valCellWidth = maxValWidth;//adjustDimension(valCellWidth, maxValWidth);
		valCellHeight = maxValHeight;//adjustDimension(valCellHeight, maxValHeight);
		enforceMinSize();
		return new Rectangle(valCellWidth, valCellHeight);
	}
	
	@Override
	public Rectangle preferredSize()
	{
		Rectangle keySize = preferredKeySize();
		Rectangle valSize = preferredValSize();
		int cellWidth = keySize.width + valSize.width + theme.internalBorderThin;
		int cellHeight = Math.max(keySize.height, valSize.height);
		int width = cellWidth+2*theme.getExternalBorder(inner);
		int height = cellHeight * super.size();
		height += theme.internalBorder * Math.max(0,super.size()-1);
		height += 2*theme.getExternalBorder(inner);
		return new Rectangle(width, height);
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
		if(super.size() == 0) return new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		
		int border = theme.getExternalBorder(inner);
		int cellHeight = size.height - 2*border;
		cellHeight -= (super.size()-1) * theme.internalBorder;
		cellHeight /= super.size();
		cellHeight = Math.max(cellHeight, 1);
		int cellWidth = Math.max(size.width-2*border, 1);
		int horizontalOverhead = 2*border + theme.internalBorderThin;
		double horizontalRatio = (size.width-horizontalOverhead)/(preferredSize().width-horizontalOverhead);
		Rectangle keySize = preferredKeySize();
		keyCellWidth = (int)(keySize.width * horizontalRatio);
		valCellWidth = size.width - horizontalOverhead - keyCellWidth;
		keySize = new Rectangle(keyCellWidth, cellHeight);
		Rectangle valSize = new Rectangle(valCellWidth, cellHeight);
		
		ArrayList<BufferedImage> keyImages = new ArrayList<BufferedImage>(), valImages = new ArrayList<BufferedImage>();
		ArrayList<K> ithKey = new ArrayList<K>();
		for(Map.Entry<K, V> entry : super.entrySet())
		{
			ithKey.add(entry.getKey());
			
			BufferedImage image = ObjectDrawer.draw(entry.getKey(), theme, keySize);
			keyImages.add(image);
			image = ObjectDrawer.draw(entry.getValue(), theme, valSize);
			valImages.add(image);
		}
		
		BufferedImage result = 
			new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = result.createGraphics();
		graphics.setColor(theme.borderColor);
		graphics.fillRect(0, 0, result.getWidth(), result.getHeight());
		graphics.setColor(theme.backgroundColor);
		for(int i=0; i<super.size(); i++)
		{
			int baseX = border, baseY = (cellHeight+theme.internalBorder)*i+border;
			int baseValX = baseX + keyCellWidth + theme.internalBorderThin;
			graphics.fillRect(baseX, baseY, keyCellWidth, cellHeight);
			graphics.fillRect(baseValX, baseY, valCellWidth, cellHeight);
			BufferedImage keyImage = keyImages.get(i);
			BufferedImage valImage = valImages.get(i);
			K key = ithKey.get(i);
			if(beingRemoved.contains(key))
			{
				keyImage = theme.drawRemove(keyImage);
				valImage = theme.drawRemove(valImage);
			}
			else if(beingAdded.contains(key))
			{
				keyImage = theme.drawAdd(keyImage);
				valImage = theme.drawAdd(valImage);
			}
			else if(beingWritten.contains(key))
			{
				keyImage = theme.drawSet(keyImage);
				valImage = theme.drawSet(valImage);
			}
			else if(beingRead.contains(key))
			{
				keyImage = theme.drawGet(keyImage);
				valImage = theme.drawGet(valImage);
			}
			graphics.drawImage(keyImage, baseX, baseY, null);
			graphics.drawImage(valImage, baseValX, baseY, null);
			
		}
		
		if(sleepConstants.batchGet)
		{
			beingRead.clear();
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
		theme = new DefaultTheme();
		inner = false;
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
	
	private void setToInner(Object o)
	{
		if(o instanceof VisualizableStructure)
		{
			((VisualizableStructure)(o)).setInner(true);
		}
	}
	
	@Override
	public V put(K key, V value)
	{
		V result = null;
		setToInner(key);
		setToInner(value);
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
			this.displayer.requestRedraw(this, 0);
		}
	}
	
	@Override
	public void requestRedraw(VisualizableStructure caller, int delay)
	{
		if(displayer != null) displayer.requestRedraw(this, delay);
	}
	
	
}