package sk.ksp.baklazan.sketchalgo.structure;
import sk.ksp.baklazan.sketchalgo.*;
import java.awt.image.*;
import java.awt.Rectangle;


public class VisualWrapped<T> extends Wrapped<T> implements VisualizableStructure, StructureDisplayer
{
	protected StructureDisplayer displayer;
	protected boolean inner;
	protected Theme theme;
	protected String myName;
	protected SleepConstants sleepConstants;
	
	private boolean beingRead, beingWritten;
	
	@Override
	public void setDisplayer(StructureDisplayer displayer)
	{
		this.displayer = displayer;
	}
	
	@Override
	public void setInner(boolean inner)
	{
		this.inner = inner;
	}
	
	public void setSleepConstants(SleepConstants sleepConstants)
	{
		this.sleepConstants = sleepConstants;
	}
	
	public void setTheme(Theme theme)
	{
		this.theme = theme;
	}
	
	@Override
	public BufferedImage draw()
	{
		return draw(preferredSize());
	}
	
	@Override
	public BufferedImage draw(Rectangle size)
	{
		BufferedImage image = drawBasic(size);
		if(beingWritten)
		{
			image = theme.drawSet(image);
		}
		else if(beingRead)
		{
			image = theme.drawGet(image);
		}
		if(myName != null && !inner)
		{
			image = ObjectDrawer.addCaption(image, myName, theme);
		}
		if(sleepConstants.getType == SleepConstants.GET_BATCH) beingRead = false;
		return image;
	}
	
	protected BufferedImage drawBasic(Rectangle size)
	{
		return ObjectDrawer.draw(wrapped, theme, size);
	}
	
	@Override
	public Rectangle preferredSize()
	{
		return ObjectDrawer.preferredSize(wrapped, theme);
	}
	
	@Override
	public void requestRedraw(VisualizableStructure caller, int delay)
	{
		if(displayer != null)
		{
			displayer.requestRedraw(this, delay);
		}
	}
	
	public VisualWrapped(T t)
	{
		this(t, null);
	}
	
	public VisualWrapped(T t, String name)
	{
		super(t);
		this.myName = name;
		beingWritten = false;
		beingRead = false;
		sleepConstants = new SleepConstants(0, 200, 0, 300, SleepConstants.GET_SILENT);
	}
	
	@Override
	public T get()
	{
		switch(sleepConstants.getType)
		{
			case SleepConstants.GET_INSTANT:
			{
				beingRead = true;
				requestRedrawAndDelay(sleepConstants.sleepGet);
				beingRead = false;
				requestRedrawAndDelay(0);
				break;
			}
			case SleepConstants.GET_BATCH:
			{
				beingRead = true;
				break;
			}
			case SleepConstants.GET_SILENT:
			{
				break;
			}
		}
		return super.get();
	}
	
	@Override
	public void set(T val)
	{
		beingWritten = true;
		requestRedrawAndDelay(sleepConstants.sleepSet/2);
		super.set(val);
		requestRedrawAndDelay(sleepConstants.sleepSet/2);
		beingWritten = false;
		requestRedrawAndDelay(0);
	}
	
	private void requestRedrawAndDelay(int time)
	{
		if(displayer != null)
		{
			displayer.requestRedraw(this, time);
		}
	}
}