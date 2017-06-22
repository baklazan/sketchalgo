package sk.ksp.baklazan.sketchalgo.structure;
import sk.ksp.baklazan.sketchalgo.*;
import java.awt.image.*;
import java.awt.Rectangle;


public class VisualWrapper<T> extends ObjectWrapper<T> implements VisualizableStructure
{
	private StructureDisplayer displayer;
	private boolean inner;
	private Theme theme;
	private String myName;
	
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
		return ObjectDrawer.draw(wrapped, theme, size);
	}
	
	@Override
	public Rectangle preferredSize()
	{
		return ObjectDrawer.preferredSize(wrapped, theme);
	}
	
	public VisualWrapper(T t)
	{
		this(null, t);
	}
	
	public VisualWrapper(String name, T t)
	{
		super(t);
		this.myName = name;
	}
}