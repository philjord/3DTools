package tools.compressedtexture.ktx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.media.j3d.ImageComponent;
import javax.media.j3d.KTXImageComponent2D;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;

import com.numericalactivity.dktxtools.ktx.KTXFormatException;

import javaawt.image.BufferedImage;
import tools.compressedtexture.CompressedTextureLoader;

/**
 * See DDSTextureLoader
 */
public class KTXTextureLoader extends CompressedTextureLoader
{

	/**
	 * Returns the associated Texture object or null if the image failed to load
	 * Note it may return a Texture loaded earlier
	 * @param file a dds image file
	 * @return A {@code Texture} with the associated DDS image or null if the image failed to load
	 */
	public static Texture getTexture(File file)
	{
		String filename = file.getAbsolutePath();
		try
		{
			return getTexture(filename, new FileInputStream(file));
		}
		catch (IOException e)
		{
			System.out.println(
					"" + CompressedTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
			return null;
		}
	}

	/**
	 * Returns the associated Texture object or null if the image failed to load
	 * Note it may return a Texture loaded earlier
	 * @param filename just a useful name for teh inputstreams source
	 * @param inputStream which is fully read into a {@code ByteBuffer} and must contain a dds texture
	 * @return A {@code Texture} with the associated DDS image
	 */
	public static Texture getTexture(String filename, InputStream inputStream)
	{
		// Check the cache for an instance first
		Texture ret_val = loadedTextures.get(filename);

		if (ret_val == null)
		{
			try
			{
				KTXImage ktxImage = new KTXImage(toByteBuffer(inputStream));
				Texture2D tex = createTexture(filename, ktxImage);

				loadedTextures.put(filename, tex);
				ret_val = tex;
			}
			catch (IOException e)
			{
				System.out.println(
						"" + KTXTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
				return null;
			}
			catch (KTXFormatException e)
			{
				System.out.println(
						"" + KTXTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
				return null;
			}
		}
		return ret_val;
	}

	/**
	 * Note avoid mappedbytebufffers as that will push texture loading (disk activity) onto the j3d thread
	 * which is bad, pull everything into byte arrays on the current thread
	 * @param filename
	 * @param inputBuffer
	 * @return
	 */

	public static Texture getTexture(String filename, ByteBuffer inputBuffer)
	{
		// Check the cache for an instance first
		Texture ret_val = loadedTextures.get(filename);

		if (ret_val == null)
		{
			try
			{
				KTXImage ktxImage = new KTXImage(inputBuffer);
				Texture2D tex = createTexture(filename, ktxImage);
				loadedTextures.put(filename, tex);
				ret_val = tex;
			}
			catch (IOException e)
			{
				System.out.println(
						"" + KTXTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
				return null;
			}
			catch (KTXFormatException e)
			{
				System.out.println(
						"" + KTXTextureLoader.class + " had a  IO problem with " + filename + " : " + e + " " + e.getStackTrace()[0]);
				return null;
			}
		}
		return ret_val;

	}

	private static Texture2D createTexture(String filename, KTXImage ktxImage)
	{

		// unsupported type will have failed already		

		int levels = ktxImage.getNumMipMaps();
		// now check how big it should be! sometime these things run out with 0 width or 0 height size images
		int levels2 = Math.min(computeLog(ktxImage.getWidth()), computeLog(ktxImage.getHeight())) + 1;
		// use the lower of the two, to avoid 0 sizes going to the driver
		levels = levels > levels2 ? levels2 : levels;

		// always 1 level
		levels = levels == 0 ? 1 : levels;

		Texture2D tex = new Texture2D(ktxImage.getNumMipMaps() <= 1 ? Texture.BASE_LEVEL : Texture.MULTI_LEVEL_MIPMAP, Texture.RGBA,
				ktxImage.getWidth(), ktxImage.getHeight());

		tex.setName(filename);

		tex.setBaseLevel(0);
		tex.setMaximumLevel(levels - 1);

		tex.setBoundaryModeS(Texture.WRAP);
		tex.setBoundaryModeT(Texture.WRAP);

		// better to let machine decide
		tex.setMinFilter(Texture.NICEST);
		tex.setMagFilter(Texture.NICEST);

		//defaults to Texture.ANISOTROPIC_NONE
		if (anisotropicFilterDegree > 0)
		{
			tex.setAnisotropicFilterMode(Texture.ANISOTROPIC_SINGLE_VALUE);
			tex.setAnisotropicFilterDegree(anisotropicFilterDegree);
		}

		for (int i = 0; i < levels; i++)
		{
			BufferedImage image = new KTXBufferedImage(ktxImage, i, filename);
			tex.setImage(i, new KTXImageComponent2D(ImageComponent.FORMAT_RGBA, image));
		}

		loadedTextures.put(filename, tex);

		return tex;

	}

}
