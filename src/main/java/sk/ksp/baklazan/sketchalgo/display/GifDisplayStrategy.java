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
	ImageTypeSpecifier imageType;
	BufferedImage lastFrame;
	Rectangle size;
	FileImageOutputStream output;
	
	public GifDisplayStrategy(Rectangle size, File file) throws FileNotFoundException, IOException
	{
		this.size = size;
		lastFrame = null;
		Iterator<ImageWriter> iterator = ImageIO.getImageWritersByFormatName("GIF");
		writer = iterator.next();
		
		imageType = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
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
	
	
	/* This part is heavily inspired by this class:
	 * http://elliot.kroo.net/software/java/GifSequenceWriter/GifSequenceWriter.java
	 */
	
	private IIOMetadataNode getNode(IIOMetadataNode parent, String sonName)
	{
		int numberOfChildren = parent.getLength();
		for(int i=0; i<numberOfChildren; i++)
		{
			IIOMetadataNode child = (IIOMetadataNode)parent.item(i);
			if(child.getNodeName().compareToIgnoreCase(sonName) == 0)
			{
				return child;
			}
		}
		IIOMetadataNode result = new IIOMetadataNode(sonName);
		parent.appendChild(result);
		return result;
	}
	
	private void debug(IIOMetadataNode root)
	{
		int length = root.getLength();
		System.err.println(root.getNodeName());
		System.err.println("has " + length + " children:");
		for(int i=0; i<length; i++)
		{
			debug((IIOMetadataNode)(root.item(i)));
		}
		if(root.getNodeName().equals("ImageDescriptor"))
		{
			System.err.println("imageLeftPosition = " + root.getAttribute("imageLeftPosition"));
		}
	}
	
	@Override 
	public void addFrame(BufferedImage image, int time)
	{
		if(time > 0)
		{
			try
			{
				String formatName = metaData.getNativeMetadataFormatName();
				IIOMetadataNode root = (IIOMetadataNode)(metaData.getAsTree(formatName));
				IIOMetadataNode controlExtension = getNode(root, "GraphicControlExtension");
				controlExtension.setAttribute("delayTime", Integer.toString(time/10));
				metaData.setFromTree(formatName, root);
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