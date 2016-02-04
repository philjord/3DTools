package awt.tools.image;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.swing.ImageIcon;

import tools.image.ImageToBufferedImage;

public class SimpleImageLoader
{
	//Notice jpg output by MSPaint are really simple, paintshop might spit out crazy complex images
	// But you REALLY have to save as bmp then close paint then save as jpeg to clean images up for mac

	// Mac wont't load some jpg files, possibly related to the color sRGB property being set? (right click file properties -> details)
	// but also if it has a resolution set to 2 as well

	//jpeg of 96 dpi and 24 bitdepth appear good?

	//Could use URL paths to enable intra jar resource loading, see AC3DLoader
	//Note that AC3DLoader has just failed on me and I'm falling back to this class A-ha! ImageIO appears broken in mac?

	// this is most portable
	// Class.getResource(String)
	// Class.getResourceAsStream(String)
	// Toolkit.getDefaultToolkit().getImage(filename)
	// TextureLoader from j3d

	/* S H I T! read this from Image!
	*      * Image objects created by the Toolkit and Component classes which are
	* loaded from files, URLs or produced by an {@link ImageProducer}
	* are unloaded and all local resources are released.
	* These objects can later be reloaded from their original source
	* as needed when they are rendered, just as when they were first
	* created.
	* </ul>
	
	public void flush() {
	   if (surfaceManager != null) {
	       surfaceManager.flush();
	   }
	}
	*/
	//JAI pure java now!

	/**
	 * Notice there is no exception throwen for a file that does not exists you MUST check for this before calling this method
	 * In fact this method prevents all exception throwing (why?)
	 * @param imageName
	 * @return
	 */
	public static BufferedImage getImage(String imageName)
	{
		IOException except = null;

		// no file, no work!		
		if (!new File(imageName).exists())
		{
			System.out.println("SimpleImageLoader, File does not exist! " + imageName);
			return null;
		}

		BufferedImage ret = null;
		// Can't request dds images from here now not compatible with BufferedImage anymore
	/*	if (imageName.toLowerCase().endsWith(".dds"))
		{
			try
			{
				DDSImage ddsImage = DDSImage.read(imageName);
				return new DDSBufferedImage(ddsImage, 0, imageName);
			}
			catch (IOException e)
			{
				except = e;
				// let's check for non dds format with bad extension (note SimpleImageLoader.)
			}
		}*/

		ret = getImageJAI(imageName);
		if (ret != null)
			return ret;

		try
		{
			ret = ImageIO.read(new File(imageName));
			if (ret != null)
				return ret;
		}
		catch (IOException e)
		{
			except = e;
		}

		if (imageName.toLowerCase().endsWith(".jpg") || imageName.toLowerCase().endsWith(".jpeg")
				|| imageName.toLowerCase().endsWith(".gif") || imageName.toLowerCase().endsWith(".png"))
		{
			Image im = new ImageIcon(Toolkit.getDefaultToolkit().createImage(imageName)).getImage();
			//often a non null return butt no data!
			if (im != null && im.getHeight(null) != -1)
			{
				return ImageToBufferedImage.toBufferedImage(im);
			}
		}

		if (except != null)
		{
			System.out.println("IOException during load image: " + imageName + " " + except + " " + except.getStackTrace()[0]);
		}
		else
		{
			System.out.println("SimpleImageLoader, failed to load " + imageName);
		}
		return null;

	}

	public static BufferedImage getImageJAI(String imageFileName)
	{
		return JAI.create("fileload", imageFileName).getAsBufferedImage();

		//String filename = "c:\\temp\\java\\DuneBoard2.JPG";
		/*	FileSeekableStream fi = null;
			try
			{
				// create a stream on the input file specified. 
				fi = new FileSeekableStream(imageName);
				ImageDecoder dec = ImageCodec.createImageDecoder("jpeg", fi, null);
				System.out.println("dec " + dec);
				RenderedImage page = dec.decodeAsRenderedImage(0);
				System.out.println("page " + page);
			}
			catch (IOException ex)
			{
				System.out.println("Error opening the image");
				System.exit(0);
			}*/
		//PlanarImage image1 = JAI.create("fileload", imageFileName);
		//int width = image1.getWidth();
		//int height = image1.getHeight();
		//System.out.println("width " + width);
		//System.out.println("renderimage " + image1);
		// Raster rastPic = renderimage.getData();
		// int widthPix = rastPic.getWidth();
		// int heightPix = rastPic.getHeight();
		// System.out.println("widthPix " +widthPix);
		// PlanarImage myPlanarImg = PlanarImage.wrapRenderedImage(renderimage);
		//ColorModel cm = PlanarImage.createColorModel(image1.getSampleModel());
		//BufferedImage myBufImg = image1.getAsBufferedImage(null, cm);

	}

	/**
	 * IOEXception is not thrown, make sure the InputStream is super awesome.
	 * @param imageName
	 * @return
	 */
	public static BufferedImage getImage(String imageName, InputStream in)
	{
		//Can't request dds like this
		/*if (imageName.toLowerCase().endsWith(".dds"))
		{
			try
			{
				DDSImage ddsImage = DDSImage.read(DDSTextureLoader.toByteBuffer(in));
				return new DDSBufferedImage(ddsImage, 0, imageName);
			}
			catch (IOException e)
			{
				System.out.println("IOException during load image: " + imageName);
			}
		}*/

		BufferedImage ret = getImageJAI(in);
		if (ret != null)
			return ret;

		try
		{
			return ImageIO.read(in);
		}
		catch (IOException e)
		{
			System.out.println("IOException during load image: " + imageName);
		}

		return null;
	}

	public static BufferedImage getImageJAI(InputStream in)
	{
		return JAI.create("readstream", in).getAsBufferedImage();
	}

}
