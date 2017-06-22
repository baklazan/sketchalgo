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
import javafx.animation.*;
import javafx.util.*;

public class TransitionDisplayStrategy implements DisplayStrategy
{
	Canvas canvas;
	SequentialTransition animation;
	BufferedImage lastFrame;
	
	public TransitionDisplayStrategy(Canvas canvas)
	{
		this.canvas = canvas;
		animation = new SequentialTransition();
		lastFrame = null;
	}
	
	@Override
	public Rectangle getSize()
	{
		return new Rectangle((int)(canvas.getWidth()), (int)(canvas.getHeight()));
	}
	
	@Override 
	public void addFrame(BufferedImage image, int time)
	{
		if(time > 0)
		{
			animation.getChildren().add(new StillTransition(image, canvas, time));
		}
		lastFrame = image;
	}
	
	public Transition getAnimation()
	{
		if(lastFrame != null)
		{
			animation.getChildren().add(new StillTransition(lastFrame, canvas, 0));
		}
		return animation;
	}
	
	public void clearAnimation()
	{
		animation = new SequentialTransition();
	}
}

class StillTransition extends Transition
{
	BufferedImage image;
	Canvas canvas;
	
	public StillTransition(BufferedImage image, Canvas canvas, int duration)
	{
		this.image = image;
		this.canvas = canvas;
		setCycleDuration(new Duration(200));
	}
	
	@Override
	protected void interpolate(double time)
	{
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setFill(Color.WHITE);
		gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.drawImage(SwingFXUtils.toFXImage(image, null), 0, 0);
	}
}