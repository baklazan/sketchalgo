package sk.ksp.baklazan.sketchalgo;
import sk.ksp.baklazan.sketchalgo.display.DisplayStrategy;
import sk.ksp.baklazan.sketchalgo.structure.*;
import java.util.*;
import java.lang.*;
import java.lang.ref.*;
import java.awt.image.*;
import java.awt.Font;
import java.awt.geom.*;
import java.awt.Graphics2D;
import java.awt.color.*;
import java.awt.Rectangle;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.application.*;
import javafx.embed.swing.*;

public class Visualizer implements AlgorithmWatcher
{
	private DefaultDSFactory factory;
	private Canvas canvas;
	private ArrayList<WeakReference<VisualizableStructure> > structures;
	private Map<VisualizableStructure, LayoutHint> hints;
	private String algorithmState;
	private Theme theme;
	private DisplayStrategy displayStrategy;
	
	@Override
	public DefaultDSFactory getFactory()
	{
		return factory;
	}
	
	public Visualizer(DisplayStrategy displayStrategy)
	{
		this(displayStrategy, new DefaultTheme());
	}
	
	public Theme getTheme()
	{
		return theme;
	}
	
	public Visualizer(DisplayStrategy displayStrategy, Theme theme)
	{
		factory = new DefaultDSFactory(this, theme);
		this.displayStrategy = displayStrategy;
		structures = new ArrayList<WeakReference<VisualizableStructure> >();
		hints = new IdentityHashMap<VisualizableStructure, LayoutHint>();
		algorithmState = null;
		this.theme = theme;
	}
	
	@Override
	public void register(VisualizableStructure structure)
	{
		structures.add(new WeakReference<VisualizableStructure>(structure));
		redraw();
	}
	
	@Override
	public void register(VisualizableStructure structure, LayoutHint hint)
	{
		hints.put(structure, hint);
		register(structure);
	}
	
	public void unregister(Object o)
	{
		for(int i=0; i<structures.size(); i++)
		{
			if(structures.get(i).get() == o)
			{
				structures.remove(i);
				i--;
			}
		}
	}
	
	public void setAlgorithmState(String s)
	{
		algorithmState = s;
		redraw();
	}
	
	
	private BufferedImage draw()
	{
		Rectangle size = displayStrategy.getSize();
		BufferedImage surface = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = surface.createGraphics();
		
		Map<Object, Rectangle> place = new IdentityHashMap<Object, Rectangle>();
		int maxY = 0;
		
		if(algorithmState != null)
		{
			Font curFont = graphics.getFont();
			graphics.setFont(curFont.deriveFont((float)curFont.getSize()*2));
			Rectangle2D rect = graphics.getFontMetrics().getStringBounds(algorithmState, graphics);
			graphics.setColor(java.awt.Color.BLACK);
			graphics.drawString(algorithmState, (int)(-rect.getX())+2, (int)(-rect.getY())+2);
			maxY += (int)rect.getHeight()+4;
		}
		
		
		for(int i=0; i<structures.size(); i++)
		{
			VisualizableStructure structure = structures.get(i).get();
			if(structure == null)
			{
				structures.remove(i);
				i--;
				continue;
			}
			BufferedImage image = structure.draw();
			int x = 0, y = maxY;
			
			//Yes, I know, I should use Optional<>s
			LayoutHint hint = hints.get(structure);
			if(hint != null)
			{
				Object reference = hint.reference.get();
				if(reference != null)
				{
					Rectangle rect = place.get(reference);
					if(rect != null)
					{
						Rectangle myRect = new Rectangle(image.getWidth(), image.getHeight());
						x = hint.getX(rect, myRect);
						y = hint.getY(rect, myRect);
					}
				}
			}
			
			graphics.drawImage(image, x, y, null);
			place.put(structure, new Rectangle(x, y, image.getWidth(), image.getHeight()));
			maxY = Math.max(y + image.getHeight(), maxY);
		}
		return surface;
	}
	
	private void redraw()
	{
		redraw(0);
	}
	
	private void redraw(int delay)
	{
		BufferedImage frame = draw();
		displayStrategy.addFrame(frame, delay);
	}
	
	@Override
	public void requestRedraw(VisualizableStructure caller, int delay)
	{
		redraw(delay);
	}
	
}