package sk.ksp.baklazan.sketchalgo;
import java.util.*;
import java.lang.*;
import java.lang.ref.*;
import java.awt.Rectangle;

public class LayoutHint
{
	public static enum Direction {LEFT, RIGHT, UP, DOWN}
	public WeakReference<Object> reference;
	public Direction direction;
	
	public LayoutHint(Object reference, Direction direction)
	{
		this.direction = direction;
		this.reference = new WeakReference<Object>(reference);
	}
	
	public int getX(Rectangle refRect, Rectangle myRect)
	{
		switch(direction)
		{
			case LEFT:
			{
				return (int)(refRect.getX() - myRect.getWidth());
			}
			case RIGHT:
			{
				return (int)(refRect.getX() + refRect.getWidth());
			}
			case UP:
			case DOWN:
			{
				return (int)(refRect.getX());
			}
		}
		return 0;
	}
	
	public int getY(Rectangle refRect, Rectangle myRect)
	{
		switch(direction)
		{
			case LEFT:
			case RIGHT:
			{
				return (int)(refRect.getY());
			}
			case UP:
			{
				return (int)(refRect.getY() - myRect.getHeight());
			}
			case DOWN:
			{
				return (int)(refRect.getY() + refRect.getHeight());
			}
		}
		return 0;
	}
}