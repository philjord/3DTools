package awt.tools.image;

import java.awt.image.BufferedImage;

import javax.media.j3d.ImageComponent2D;
import javax.media.j3d.Texture;

import com.sun.j3d.utils.image.awt.TextureLoader;

/**
 * Separated out from SimpleIamgeLoader, so servers don't have to load up the full j3d jar set
 * @author philip
 *
 */
public class J3dSimpleImageLoader
{

	/**
	 * Notice no exception thrown from here, make sure fileName is a good un
	 * Return simple non mip mapped texture 2D, not for use with DDS image format
	 * @param fileName
	 * @return
	 */
	public static Texture getTexture(String fileName)
	{
		BufferedImage im = SimpleImageLoader.getImage(fileName);
		//used to force power of 2
		TextureLoader tl = new TextureLoader(im, TextureLoader.GENERATE_MIPMAP);
		return tl.getTexture();

		/*if (im.getType() == BufferedImage.TYPE_INT_RGB)
		{
			Texture tex = new Texture2D(Texture.BASE_LEVEL, Texture.RGB, im.getWidth(), im.getHeight());
			tex.setImage(0, new ImageComponent2D(ImageComponent.FORMAT_RGB, im));
			return tex;
		}
		else
		{
			Texture tex = new Texture2D(Texture.BASE_LEVEL, Texture.RGBA, im.getWidth(), im.getHeight());
			tex.setImage(0, new ImageComponent2D(ImageComponent.FORMAT_RGBA, im));
			return tex;
		}*/
	}
	/**
	 * Notice no exception thrown from here, make sure fileName is a good un
	 * @param fileName
	 * @return
	 */
	public static ImageComponent2D getImageComponent2D(String fileName)
	{
		BufferedImage im = SimpleImageLoader.getImage(fileName);
		TextureLoader tl = new TextureLoader(im);

		return tl.getImage();

		/*	if (im.getType() == BufferedImage.TYPE_INT_RGB)
			{
				return new ImageComponent2D(ImageComponent.FORMAT_RGB, im);
			}
			else
			{
				return new ImageComponent2D(ImageComponent.FORMAT_RGBA, im);
			}*/
	}

}
