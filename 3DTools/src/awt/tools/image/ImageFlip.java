package awt.tools.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageFlip
{
	/**
	 * This method flips the image horizontally
	 * @param img --> BufferedImage Object to be flipped horizontally
	 * @return
	 */
	public static BufferedImage horizontalflip(BufferedImage img)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(w, h, img.getType());
		Graphics2D g = dimg.createGraphics();
		/**
		 * img - the specified image to be drawn. This method does nothing if
		 * img is null. dx1 - the x coordinate of the first corner of the
		 * destination rectangle. dy1 - the y coordinate of the first corner of
		 * the destination rectangle. dx2 - the x coordinate of the second
		 * corner of the destination rectangle. dy2 - the y coordinate of the
		 * second corner of the destination rectangle. sx1 - the x coordinate of
		 * the first corner of the source rectangle. sy1 - the y coordinate of
		 * the first corner of the source rectangle. sx2 - the x coordinate of
		 * the second corner of the source rectangle. sy2 - the y coordinate of
		 * the second corner of the source rectangle. observer - object to be
		 * notified as more of the image is scaled and converted.
		 *
		 */
		g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
		g.dispose();
		return dimg;
	}

	/**
	 * This method flips the image vertically
	 * @param img --> BufferedImage object to be flipped
	 * @return
	 */
	public static BufferedImage verticalflip(BufferedImage img)
	{
		int w = img.getWidth();
		int h = img.getHeight();
		BufferedImage dimg = new BufferedImage(w, h, img.getColorModel().getTransparency());
		Graphics2D g = dimg.createGraphics();
		g.drawImage(img, 0, 0, w, h, 0, h, w, 0, null);
		g.dispose();
		return dimg;
	}

	/**
	 * This method reads an image from the file
	 * @param fileLocation -- > eg. "C:/testImage.jpg"
	 * @return BufferedImage of the file read
	 */
	public static BufferedImage readImage(String fileLocation)
	{
		BufferedImage img = null;
		try
		{
			img = ImageIO.read(new File(fileLocation));
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

		return img;

	}

	/**
	 * This method writes a buffered image to a file
	 * @param img -- > BufferedImage
	 * @param fileLocation --> e.g. "C:/testImage.jpg"
	 * @param extension --> e.g. "jpg","gif","png"
	 */
	public static void writeImage(BufferedImage img, String fileLocation, String extension)
	{
		try
		{
			BufferedImage bi = img;
			File outputfile = new File(fileLocation);
			ImageIO.write(bi, extension, outputfile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Testing the various methods
	 */
	public static void main(String args[])
	{
		String inputImageLocation = "C:/temp/myImage.jpg";
		String outputImageLocationHF = "C:/temp/myImageHorizontalFlip.jpg";
		String outputImageLocationVF = "C:/temp/myImageVerticalFlip.jpg";
		String extension = "jpg";

		/**
		 * Reading image from the file
		 */
		System.out.println("Reading Image From :" + inputImageLocation);
		BufferedImage inputImage = ImageFlip.readImage(inputImageLocation);
		BufferedImage img = inputImage;

		/**
		 * Doing Horizontal Flip and saving to file
		 */
		System.out.print("\nDoing Horizontal Flip...");
		img = ImageFlip.horizontalflip(img);
		System.out.println("Completed");
		ImageFlip.writeImage(img, outputImageLocationHF, extension);
		System.out.println("Flipped Image Saved To: " + outputImageLocationHF);

		/**
		 * Doing Vertical Flip and saving to file
		 */
		System.out.print("\nDoing Vertical Flip...");
		img = inputImage;
		img = ImageFlip.verticalflip(img);
		System.out.println("Completed");
		ImageFlip.writeImage(img, outputImageLocationVF, extension);
		System.out.println("Flipped Image Saved To: " + outputImageLocationVF);
	}

}
