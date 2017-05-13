package sk.ksp.baklazan.sketchalgo.display;
import java.awt.image.*;
import java.awt.Rectangle;


public interface DisplayStrategy
{
	Rectangle getSize();
	void addFrame(BufferedImage frame, int time);
}