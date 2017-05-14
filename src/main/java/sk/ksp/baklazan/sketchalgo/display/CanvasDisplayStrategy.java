package sk.ksp.baklazan.sketchalgo.display;
import sk.ksp.baklazan.sketchalgo.structure.*;
import java.util.*;
import java.lang.*;
import java.lang.ref.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.color.*;
import java.awt.Rectangle;
import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.application.*;
import javafx.embed.swing.*;

public class CanvasDisplayStrategy implements DisplayStrategy
{
	Canvas canvas;
	
	public CanvasDisplayStrategy(Canvas canvas)
	{
		this.canvas = canvas;
	}
	
	private void flip(BufferedImage surface)
	{
		GraphicsContext gc = canvas.getGraphicsContext2D();
		Platform.runLater(() -> gc.setFill(Color.WHITE));
		Platform.runLater(() -> gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight()));
		Platform.runLater(() -> gc.drawImage(SwingFXUtils.toFXImage(surface, null), 0, 0));
	}
	
	@Override
	public Rectangle getSize()
	{
		return new Rectangle((int)(canvas.getWidth()), (int)(canvas.getHeight()));
	}
	
	@Override 
	public void addFrame(BufferedImage image, int time)
	{
		flip(image);
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