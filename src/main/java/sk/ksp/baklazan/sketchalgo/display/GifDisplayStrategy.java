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
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;
import java.io.*;

public class GifDisplayStrategy implements DisplayStrategy
{
	ImageWriter writer;
	IIOMetadata metaData;
	ImageWriteParam writeParam;
	BufferedImage lastFrame;
	Rectangle size;
	FileImageOutputStream output;
	
	public GifDisplayStrategy(Rectangle size, File file) throws FileNotFoundException, IOException
	{
		this.size = size;
		lastFrame = null;
		Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("GIF");
		writer = iterator.next();
		
		ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
		writeParam = writer.getDefaultWriteParam();
		metaData = writer.getDefaultImageMetadata(imageType, writeParam);
		output = new FileImageOutputStream(file);
		writer.setOutput(output);
		writer.prepareWriteSequence(null);
	}
	
	
	@Override
	public Rectangle getSize()
	{
		return size;
	}
	
	@Override 
	public void addFrame(BufferedImage image, int time)
	{
		if(time > 0)
		{
			try
			{
				writer.writeToSequence(new IIOImage(image, null, metaData), writeParam);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			lastFrame = null;
		}
		else
		{
			lastFrame = image;
		}
	}
	
	public void close()
	{
		if(lastFrame != null)
		{
			try
			{
				writer.writeToSequence(new IIOImage(lastFrame, null, metaData), writeParam);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		try
		{
			writer.endWriteSequence();
			output.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}