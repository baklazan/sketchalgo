package sk.ksp.baklazan.sketchalgo;
import sk.ksp.baklazan.sketchalgo.structure.*;
import java.util.*;
import java.lang.*;
import java.lang.ref.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.Graphics2D;
import java.awt.color.*;
import java.awt.Color;
import javafx.scene.canvas.*;
import javafx.application.*;
import javafx.embed.swing.*;

public class DefaultTheme extends Theme
{
	public Color getOverlay, setOverlay, addOverlay, removeOverlay;
	
	public DefaultTheme()
	{
		getOverlay = new Color(0, 200, 0, 100);
		setOverlay = new Color(255, 127, 0, 100);
		addOverlay = new Color(0, 0, 255, 100);
		removeOverlay = new Color(255, 0, 0, 100);
		backgroundColor = new Color(255, 255, 255, 255);
		borderColor = new Color(0, 0, 0, 255);
		textColor = new Color(0, 0, 0, 255);
		minCellWidth = 20;
		minCellHeight = 20;
		externalBorderInner = 0;
		externalBorderOuter = 2;
		internalBorder = 2;
		internalBorderThin = 1;
		textMargin = 2;
	}
	
	private BufferedImage overlay(BufferedImage image, Color color)
	{
		BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage screen = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphicsResult = result.createGraphics();
		graphicsResult.drawImage(image, 0, 0, null);
		Graphics2D graphicsScreen = screen.createGraphics();
		graphicsScreen.setColor(color);
		graphicsScreen.fillRect(0, 0, screen.getWidth(), screen.getHeight());
		graphicsResult.drawImage(screen, 0, 0, null);
		return result;
	}
	
	@Override
	public BufferedImage drawGet(BufferedImage image)
	{
		return overlay(image, getOverlay);
	}
	
	@Override
	public BufferedImage drawSet(BufferedImage image)
	{
		return overlay(image, setOverlay);
	}
	
	@Override
	public BufferedImage drawAdd(BufferedImage image)
	{
		return overlay(image, addOverlay);
	}
	
	@Override
	public BufferedImage drawRemove(BufferedImage image)
	{
		return overlay(image, removeOverlay);
	}
}